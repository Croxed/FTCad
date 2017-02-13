package frontend;

import common.PingMessage;
import common.ServerRespondMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
            outputStream.writeObject(new ServerRespondMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
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

                e.printStackTrace();
            }
            if (System.currentTimeMillis() - m_time > 5000) {
                isConnected = false;
            }
        }
    }
}
