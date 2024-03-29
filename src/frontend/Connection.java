package frontend;


import common.PingMessage;
import common.Pingu;
import common.ServerWithFrontEnd.isPrimaryMessage;
import common.ThreadSafeObjectWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Connection implements Runnable {
    private volatile Socket m_socket;
    private volatile FrontEnd m_frontEnd;
    private ThreadSafeObjectWriter output;
    private ObjectInputStream inputStream;
    private volatile int mPortNr;
    private volatile boolean isConnected;

    /**
     * Default constructor for Connection.
     * Connection is a TCPConnection to a client or a server.
     * @param frontEnd reference to the frontEnd
     * @param socket socket to use
     */
    public Connection(FrontEnd frontEnd, Socket socket) {
        m_frontEnd = frontEnd;
        m_socket = socket;
        isConnected = true;
    }

    /**
     * Get connection of the current socket
     *
     * @return Address of connection
     */
    public synchronized InetAddress getAddress() {
        return m_socket.getInetAddress();
    }

    /**
     * Get port of the server that connected to front end
     */
    public synchronized int getPort() {
        return mPortNr;
    }

    synchronized void sendIsPrimary() throws IOException {
        output.writeObject(new isPrimaryMessage());
    }

    /**
     * Open the streams and detect whether a server or a client connected to the front end.
     * If a server connected, add it to the list of connected servers in front end, and determine if it should be primary server.
     * It a client connected, send back the information about the primary server.
     */
    private synchronized void openStream() {
        try {
            output = new ThreadSafeObjectWriter(new ObjectOutputStream(m_socket.getOutputStream()));
            inputStream = new ObjectInputStream(m_socket.getInputStream());
            Object input = inputStream.readObject();
            //Determines if the message is from a server
            if (input instanceof common.ServerWithFrontEnd.ConnectionRequestMessage) {
                common.ServerWithFrontEnd.ConnectionRequestMessage msg = (common.ServerWithFrontEnd.ConnectionRequestMessage) input;
                mPortNr = msg.getPortNr();
                System.out.println("A server connected!");
                m_frontEnd.addServer(this, msg.wasPrimary());
                m_frontEnd.waitForAllowance();
                Connection primary = m_frontEnd.getPrimary();
                output.writeObject(new common.ServerWithFrontEnd.ConnectionRespondMessage(primary == this, primary.getAddress(), primary.getPort()));
            }
            // Determines if the message is from a client
            else if (input instanceof common.ClientWithFrontEnd.ConnectionRequestMessage) {
                Connection serverConnection = m_frontEnd.getPrimary();
                System.out.println("A client connected!");
                if (serverConnection == null)
                    output.writeObject(new common.ClientWithFrontEnd.ConnectionRespondMessage(null, 0));
                else {
                    InetAddress address = serverConnection.getAddress();
                    int port = serverConnection.getPort();
                    System.out.println(address.toString() + ":" + port);
                    output.writeObject(new common.ClientWithFrontEnd.ConnectionRespondMessage(address, port));
                }
                isConnected = false;
            }
        } catch (IOException | ClassNotFoundException e) {
            try {
                Thread.sleep(1000);
                System.err.println("Could not receive message");
            } catch (InterruptedException e1) {
                System.err.println("Could not sleep");
            }
        }
    }

    /**
     * Starts a new ping thread and listens for PingMessages.
     * If the connection hasn't received a ping within 5000 milliseconds, close the connection
     * and end the ping thread.
     */
    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        openStream();
        while (isConnected) {
            Pingu pingRunnable = new Pingu(output);
            Thread pingThread = new Thread(pingRunnable);
            pingThread.start();
            while (isConnected) {
                Object input;
                try {
                    m_socket.setSoTimeout(5000);
                    input = inputStream.readObject();
                    if (input instanceof PingMessage) {
                        System.out.print(".");
                    }
                } catch (IOException | ClassNotFoundException e) {
                    isConnected = false;
                }
            }
            try {
                System.out.println("Not connected");
                pingRunnable.shutdown();
                pingThread.interrupt();
                pingThread.join();
                m_frontEnd.removeServer(this);
                m_socket.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
