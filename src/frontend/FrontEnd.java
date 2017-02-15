package frontend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by oscar on 2017-02-13.
 */
public class FrontEnd {
    private ServerSocket m_serverSocket;
    private Socket m_socket;

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
        Connection connection = new Connection(this);
        Thread connectionThread = new Thread(connection);
        connectionThread.start();
        while (true) {
            try {
                m_socket = m_serverSocket.accept();
                connection.setNewSocket(m_socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
