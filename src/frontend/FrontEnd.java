package frontend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

/**
 * Created by oscar on 2017-02-13.
 */
public class FrontEnd {
    private ServerSocket m_serverSocket;
    private Socket m_socket;
    private volatile Vector<Connection> m_connectedServers = new Vector<>();

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

    public synchronized Vector<Connection> getConnectedServers() {
        return m_connectedServers;
    }

    private void listenForMessages() {
        System.out.println("Listening for messages");
        while (true) {
            try {
                m_socket = m_serverSocket.accept();
                System.out.println("New connection!");
                Connection connection = new Connection(this, m_socket);
                Thread connectionThread = new Thread(connection);
                connectionThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
