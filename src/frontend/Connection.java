package frontend;


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
    private volatile Socket m_newSocket;
    private volatile FrontEnd m_frontEnd;

    public Vector<ServerConnection> getConnectedServers() {
        return m_connectedServers;
    }

    private volatile Vector<ServerConnection> m_connectedServers = new Vector<>();

    public Connection(FrontEnd frontEnd) {
        m_frontEnd = frontEnd;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        while (true) {
            try {
                while (m_socket == m_newSocket) {
                }
                m_socket = m_newSocket;
                ObjectOutputStream outputStream = new ObjectOutputStream(m_socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(m_socket.getInputStream());
                Object input = inputStream.readObject();
                if (input instanceof common.ServerWithFrontEnd.ConnectionRequestMessage) {
                    common.ServerWithFrontEnd.ConnectionRequestMessage msg = (common.ServerWithFrontEnd.ConnectionRequestMessage) input;
                    ServerConnection serverConnection = new ServerConnection(m_frontEnd, this, m_socket, outputStream, inputStream, msg.getPortNr());
                    System.out.println("A server connected!");
                    Thread serverThread = new Thread(serverConnection);
                    serverThread.start();
                    m_connectedServers.add(serverConnection);
                } else if (input instanceof common.ClientWithFrontEnd.ConnectionRequestMessage) {
                    if(m_connectedServers.size() >= 1) {
                        ServerConnection serverConnection = m_connectedServers.lastElement();
                        System.out.println("A client connected!");
                        InetAddress address = serverConnection.getAddress();
                        int port = serverConnection.getPort();
                        System.out.println(address.toString() + ":" + port);
                        outputStream.writeObject(new common.ClientWithFrontEnd.ConnectionRespondMessage(address, port));
                    }
                    else{
                        outputStream.writeObject(new common.ClientWithFrontEnd.ConnectionRespondMessage(null, 0));
                    }
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
    }

    public void setNewSocket(Socket m_newSocket) {
        this.m_newSocket = m_newSocket;
    }
}
