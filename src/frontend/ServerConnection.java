package frontend;

import common.PingMessage;
import common.ServerWithFrontEnd.ConnectionRespondMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by oscar on 2017-02-13.
 */
public class ServerConnection implements Runnable {
    private Socket m_socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private volatile boolean isConnected;

    public ServerConnection(Socket socket, ObjectOutputStream oStream, ObjectInputStream iStream) {
        m_socket = socket;
        outputStream = oStream;
        inputStream = iStream;
        isConnected = true;
        try {
            System.out.println("Trying to send ConnectionRespondMessage");
            // TODO: always tells servers they are primary
            outputStream.writeObject(new ConnectionRespondMessage(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public InetAddress getAddress() {
        return m_socket != null ? m_socket.getInetAddress() : null;
    }

    public int getPort() {
        return m_socket != null ? m_socket.getPort() : 0;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        long m_time = 0;
        while (isConnected) {
            Object input;
            try {
                input = inputStream.readObject();
                m_time = 0;
                if (input instanceof PingMessage) {

                }
            } catch (IOException | ClassNotFoundException e) {
                if (m_time == 0)
                    m_time = System.currentTimeMillis();
            }
            if (m_time != 0 && System.currentTimeMillis() - m_time > 5000) {
                isConnected = false;
            }
        }
        try{
            m_socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
