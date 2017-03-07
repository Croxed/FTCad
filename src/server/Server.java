package server;

import common.ClientWithServer.EventHandler;
import common.ClientWithServer.EventReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import common.ClientWithServer.ServerConnection;

/**
 * Server class containing main function for the server component
 */
public class Server implements EventReceiver {
    private ArrayList<ClientConnection> clients = new ArrayList<>(); // Keep a list of the clients
    private FrontEndCommunicator fec; // Keep a connection with the frontend alive
    private int port; // port of the server
    private EventHandler eh = new EventHandler(); // List of all draw and delete events

    /**
     * Init Server object
     * @param _port number to start the server on
     */
    public Server(int _port) {
        port = _port;
    }

    /**
     * Start the server
     * @param console arguments
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Server.Server frontendaddress frontendport portnumber");
            System.exit(-1);
        }
        try {
            // Start the server and listen for connections
            Server instance = new Server(Integer.parseInt(args[2]));
            instance.talkWithFrontend(args[0], Integer.parseInt(args[1]));
        } catch (NumberFormatException e) {
            System.err.println("Error: Port number must be an integer." + e);
            System.exit(-1);
        } catch (IOException e) {
            System.err.println("Error: Can't open socket on port." + e);
            System.exit(-1);
        }
    }

    /**
     * Talk with frontend and if successful listen for connections
     *
     * @param hostName of frontend
     * @param hostPort of frontend
     * @throws IOException If connection failed
     */
    private void talkWithFrontend(String hostName, int hostPort) throws IOException {
        //Starts the front end thread
        fec = new FrontEndCommunicator(port, hostName, hostPort);
        fec.start();
        // Wait for response from frontend before starting server
        while (true) {
        	if (fec.getType() != null) {
	            if (fec.getType() == FrontEndCommunicator.Type.PRIMARY) {
	            	// The server is a primary, listen for connections and exit if crash
	                listenForConnections();
	                return;
	            } else if (fec.getType() == FrontEndCommunicator.Type.BACKUP) {
	            	// The server is a backup, listen to the primary
	            	// Will loop back after the connection to the primary is disconnected
	            	listenToPrimary();
	            }
        	}
        	// Wait a short while
            try { Thread.sleep(50); } catch (InterruptedException e) { }
        }
    }

    /**
     * Listen to the primary server for events
     */
    private void listenToPrimary() {
        try {
            System.out.println("Connecting to primary server");
        	ServerConnection sc = new ServerConnection(this, fec.getPrimaryAddress(), fec.getPrimaryPort());
            sc.run();
        } catch (IOException e) {
            System.out.println("Connection to primary server could not be established");
        }
	}

	/**
     * Listen for new connections, starting a new ClientConnection for each
     * @throws IOException if connection failed
     */
    public void listenForConnections() throws IOException {
    	// Start a server on the specified port
        ServerSocket socket = new ServerSocket(port);
        System.out.println("Waiting for client messages on port " + port);

        // Keep listening for messages
        while (true) {
            Socket client;
            try {
                // Wait for new connections and accept
                client = socket.accept();
                // Create a new ClientConnection thread for the new socket and start the thread
                ClientConnection cc = new ClientConnection(this, client);
                cc.start();
            } catch (IOException e) {
                System.out.println("Error when accepting connection");
                e.printStackTrace();
            }
        }
    }

    /**
     * Adds the new client to the client list and sends all the current events to it
     * @param cc client to add
     */
    public synchronized void addClient(ClientConnection cc) {
        System.out.println("client added to client list");
        if (eh.numEvents() > 0) {
        	// Only send events if there are any
            cc.send(eh);
            System.out.println("Sent existing events to client");
        }
        clients.add(cc);
    }

    /**
     * Send a delete or draw event to all connected clients
     * @param o object to send
     */
    public synchronized void addEvent(Object o) {
        System.out.println(o.toString() + " added to events list and sent to clients");
        eh.addEvent(o);
        // Create an EventHandler and add the event to send
        EventHandler singleEvent = new EventHandler();
        singleEvent.addEvent(o);
        // Send the EventHandler to all connected clients
        for (ClientConnection cc : clients) {
            cc.send(singleEvent);
        }
    }

	/**
	 * Used to satisfy the EventReceiver interface, called by common.ClientWithServer.ServerConnection
	 * Only called if the server is a backup and currently listening to events from the primary server
	 */
	@Override
	public synchronized void addEvents(EventHandler extraEh) {
		eh.addEvents(extraEh);		
	}
}
