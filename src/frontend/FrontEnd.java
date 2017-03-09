package frontend;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class FrontEnd {
    private ServerSocket m_serverSocket;
    private volatile Vector<Connection> m_connectedServers = new Vector<>();
    private Connection m_PrimaryServer;
    private long m_time;

    /**
     * Create a front end, where the port number is the port where the front end should listen for
     * new connections.
     *
     * @param portNumber Port number to listen on
     * @throws IOException If front end could not create a new ServerSocket.
     */
    public FrontEnd(int portNumber) throws IOException {
        m_serverSocket = new ServerSocket(portNumber);
        m_time = System.currentTimeMillis();
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
     * Listens for new connections.
     * Creates a new connection when someone connects to the front end.
     */
    private void listenForMessages() {
        System.out.println("Listening for messages");
        while (true) {
            try {
                Socket m_socket = m_serverSocket.accept();
                System.out.println("New connection!");
                Connection connection = new Connection(this, m_socket);
                Thread connectionThread = new Thread(connection);
                connectionThread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove a server from the connected servers.
     * Also picks a new primary server if the removed one was the former
     * primary server
     * @param serverConnection ServerConnection to remove
     */
    void removeServer(Connection serverConnection) {
        m_connectedServers.remove(serverConnection);
        if (m_PrimaryServer == serverConnection) {
            pickPrimary();
            try {
                if(m_PrimaryServer != null) {
                    m_PrimaryServer.sendIsPrimary();
                }
            } catch (IOException e) {
                System.err.println("Could not update primary server...");
            }
        }
    }

    /**
     * Add a new server to the list of connected servers
     * @param server Server to add
     * @param wasPrimary Determines if
     */
    public synchronized void addServer(Connection server, boolean wasPrimary) {
        m_connectedServers.add(server);
        if (m_PrimaryServer == null && wasPrimary) {
            m_PrimaryServer = server;
        }
    }

    /**
     * Get the primary server. If there's no primary server, pick one.
     * @return the primary server
     */
    Connection getPrimary() {
        if (m_PrimaryServer == null) {
            pickPrimary();
        }
        return m_PrimaryServer;
    }

    /**
     * Pick a primary server if any server has connected to the FrontEnd.
     */
    private void pickPrimary() {
        m_PrimaryServer = m_connectedServers.size() != 0 ? m_connectedServers.firstElement() : null;
    }

    /**
     * Wait 5 seconds on startup for all servers to connect
     * This is to wait for former primary servers to connect to FrontEnd on crash.
     */
    public void waitForAllowance() {
        if (m_time + 5000 > System.currentTimeMillis()) {
            try {
                Thread.sleep(m_time + 5000 - System.currentTimeMillis());
            } catch (Exception e) {
                System.err.println("Could not sleep");
            }
        }
    }
}
