package frontend;

import common.ServerRequestMessage;
import common.ServerRespondMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by oscar on 2017-02-13.
 */
public class ServerConnection {
    private Socket m_socket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    public ServerConnection(Socket socket){
        Object input;
        m_socket = socket;
        try{
            outputStream = new ObjectOutputStream(m_socket.getOutputStream());
            inputStream = new ObjectInputStream(m_socket.getInputStream());
            input = inputStream.readObject();
            if(input instanceof ServerRequestMessage){
                outputStream.writeObject(new ServerRespondMessage());
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
