package frontend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FrontEnd {
    private ServerSocket m_serverSocket;
    private Socket m_socket;
    private volatile Vector<Connection> m_connectedServers = new Vector<>();
    private Connection m_PrimaryServer;

    /**
     * Create a front end, where the port number is the port where the front end should listen for
     * new connections.
     * @param portNumber Port number to listen on
     * @throws IOException If front end could not create a new ServerSocket.
     */
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

    /**
     * Get a vector of connected servers.
     * @return Vector with connected servers.
     */
    public synchronized Vector<Connection> getConnectedServers() {
        return m_connectedServers;
    }

    /**
     * Listens for new connections.
     * Creates a new connection when someone connects to the front end.
     */
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

    void removeServer(Connection serverConnection){
        if(m_PrimaryServer == serverConnection){
            pickPrimary();
            try {
                m_PrimaryServer.sendIsPrimary();
            } catch (IOException e) {
                System.err.println("Could not update primary server...");
            }
        }
        m_connectedServers.remove(serverConnection);
    }

    Connection getPrimary(){
        if(m_PrimaryServer == null){
            pickPrimary();
        }
        return m_PrimaryServer;
    }

    private void pickPrimary(){
        m_PrimaryServer = m_connectedServers.size() != 0 ? m_connectedServers.firstElement() : null;
    }
}
