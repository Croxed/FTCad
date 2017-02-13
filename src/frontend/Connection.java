package frontend;

import common.ClientRequestMessage;
import common.ServerRequestMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by oscar on 2017-02-13.
 */
public class Connection implements Runnable {
    private volatile Socket m_socket;
    private volatile Socket m_newSocket;
    private volatile Vector<Thread> m_connectedServers = new Vector<>();

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
                if (input instanceof ServerRequestMessage) {
                    ServerConnection serverConnection = new ServerConnection(m_socket, outputStream, inputStream);
                    Thread serverThread = new Thread(serverConnection);
                    serverThread.start();
                    m_connectedServers.add(serverThread);
                } else if (input instanceof ClientRequestMessage) {

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
