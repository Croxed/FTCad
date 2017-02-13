package frontend;

import common.ClientWithFrontEndConnectionRequestMessage;
import common.ClientWithFrontEndConnectionRespondMessage;
import common.ServerWithFrontEndConnectionRequestMessage;

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
    private volatile Vector<ServerConnection> m_connectedServers = new Vector<>();

    public Connection() {
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
                if (input instanceof ServerWithFrontEndConnectionRequestMessage) {
                    ServerConnection serverConnection = new ServerConnection(m_socket, outputStream, inputStream);
                    System.out.println("A server connected!");
                    Thread serverThread = new Thread(serverConnection);
                    serverThread.start();
                    m_connectedServers.add(serverConnection);
                } else if (input instanceof ClientWithFrontEndConnectionRequestMessage) {
                    if(m_connectedServers.size() > 1) {
                        ServerConnection serverConnection = m_connectedServers.lastElement();
                        InetAddress address = serverConnection.getAddress();
                        int port = serverConnection.getPort();
                        outputStream.writeObject(new ClientWithFrontEndConnectionRespondMessage(address, port));
                    }
                    else{
                        outputStream.writeObject(new ClientWithFrontEndConnectionRespondMessage(null, 0));
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void setNewSocket(Socket m_newSocket) {
        this.m_newSocket = m_newSocket;
    }
}
