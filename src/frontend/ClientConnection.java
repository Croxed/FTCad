package frontend;

import common.ServerRespondMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by oscar on 2017-02-13.
 */
public class ClientConnection {
    private Socket m_socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ClientConnection(Socket socket, ObjectOutputStream oStream, ObjectInputStream iStream) {
        m_socket = socket;
        outputStream = oStream;
        inputStream = iStream;
        try{
            outputStream.writeObject(new ServerRespondMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
