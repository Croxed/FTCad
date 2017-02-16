package frontend;


import common.PingMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by oscar on 2017-02-13.
 */
public class Connection implements Runnable {
    private volatile Socket m_socket;
    private volatile FrontEnd m_frontEnd;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private volatile int m_portnr;
    private volatile boolean isPrimary;
    private volatile boolean isConnected;

    public Connection(FrontEnd frontEnd, Socket socket) {
        m_frontEnd = frontEnd;
        m_socket = socket;
        isConnected = true;
    }

    public synchronized InetAddress getAddress() {
        return m_socket.getInetAddress();
    }

    public synchronized int getPort() {
        return m_portnr;
    }

    private synchronized void openStream() {
        Object input;
        try {
            outputStream = new ObjectOutputStream(m_socket.getOutputStream());
            inputStream = new ObjectInputStream(m_socket.getInputStream());
            input = inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            isConnected = false;
            System.err.println("Could not receive message");
            return;
        }
        Vector<Connection> connectedServer = m_frontEnd.getConnectedServers();
        if (input instanceof common.ServerWithFrontEnd.ConnectionRequestMessage) {
            common.ServerWithFrontEnd.ConnectionRequestMessage msg = (common.ServerWithFrontEnd.ConnectionRequestMessage) input;
            m_portnr = msg.getPortNr();
            System.out.println("A server connected!");
            connectedServer.add(this);
            try {
                outputStream.writeObject(new common.ServerWithFrontEnd.ConnectionRespondMessage(!isPrimary));
            } catch (IOException e) {
                System.err.println("Could not send message");
            }
            isPrimary = true;
        } else if (input instanceof common.ClientWithFrontEnd.ConnectionRequestMessage) {
            if (connectedServer.size() >= 1) {
                Connection serverConnection = connectedServer.lastElement();
                System.out.println("A client connected!");
                InetAddress address = serverConnection.getAddress();
                int port = serverConnection.getPort();
                System.out.println(address.toString() + ":" + port);
                try {
                    outputStream.writeObject(new common.ClientWithFrontEnd.ConnectionRespondMessage(address, port));
                } catch (IOException e) {
                    System.err.println("Could not send message");
                }
            } else {
                try {
                    outputStream.writeObject(new common.ClientWithFrontEnd.ConnectionRespondMessage(null, 0));
                } catch (IOException e) {
                    System.err.println("Could not send message");
                }
            }
            isConnected = false;
        }
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        openStream();
        while (isConnected) {
            Thread pingThread = new Thread(new Pinger());
            pingThread.start();
            while (isConnected) {
                Object input;
                try {
                    m_socket.setSoTimeout(5000);
                    input = inputStream.readObject();
                    if (input instanceof PingMessage)
                        System.out.print(".");
                } catch (IOException | ClassNotFoundException e) {
                    isConnected = false;
                }
            }
            try {
                System.out.println("Not connected");
                pingThread.interrupt();
                pingThread.join();
                m_frontEnd.getConnectedServers().remove(this);
                m_socket.close();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("Duplicates")
    private class Pinger implements Runnable {
        @Override
        public void run() {
            while (isConnected) {
                try {
                    outputStream.writeObject(new PingMessage());
                    System.out.print("!");
                } catch (IOException e) {
                    System.err.println("Could not ping");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Could not sleep");
                }
            }
        }
    }

}
