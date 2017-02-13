package frontend;

import common.Message;
import common.ServerRequestMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by oscar on 2017-02-13.
 */
public class FrontEnd {
    private ServerSocket m_serverSocket;
    private Socket m_socket;
    private volatile Vector<ServerConnection> m_serverConnections = new Vector<>();

    public FrontEnd(int portNumber) throws IOException {
        m_serverSocket = new ServerSocket(portNumber);
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java FrontEnd portnumber");
            System.exit(-1);
        }
        try {
            // Start the server and listen for connections
            FrontEnd frontEnd = new FrontEnd(Integer.parseInt(args[0]));
            frontEnd.listenForMessages();
        } catch (NumberFormatException e) {
            System.err.println("Error: Port number must be an integer." + e);
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listenForMessages() {
        System.out.println("Listening for messages");
        while (true) {
            try {
                m_socket = m_serverSocket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(m_socket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(m_socket.getInputStream());
                Object input = inputStream.readObject();
                if (input instanceof ServerRequestMessage) {
                    m_serverConnections.add(new ServerConnection(m_socket, outputStream, inputStream));
                } else if (input instanceof Message) {

                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

    }
}
