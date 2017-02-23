package server;

import common.EventHandler;
import common.EventReceiver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import DCAD.ServerConnection;

public class Server implements EventReceiver {
    private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>(); // Keep a list of the clients
    private FrontEndCommunicator fec;
    private int port;
    private ServerSocket socket;
    private EventHandler eh = new EventHandler();

    /**
     * Init Server object
     *
     * @param port number to start the server on
     * @throws IOException if the server could not bind to the specified port
     */
    public Server(int _port) throws IOException {
        port = _port;
    }

    /**
     * Start the server
     *
     * @param starting arguments when opening from a terminal
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Server.Server frontendadress frontendport portnumber");
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
     * @param hostname of frontend
     * @param port     of frontend
     * @throws IOException
     */
    private void talkWithFrontend(String hostName, int hostPort) throws IOException {
        //Starts the front end thread
        fec = new FrontEndCommunicator(this, hostName, hostPort);
        fec.start();
        // Wait for response from frontend before starting server
        while (true) {
        	if (fec.getType() != null) {
	            if (fec.getType() == FrontEndCommunicator.Type.PRIMARY) {
	                listenForConnections();
	                return;
	            } else if (fec.getType() == FrontEndCommunicator.Type.BACKUP) {
	            	listenToPrimary();
	            }
        	}
            try {
                Thread.sleep(50);
            } catch (InterruptedException sleep) {
            }
        }
    }

    private void listenToPrimary() {
        try {
            System.out.println("Connecting to primary server");
        	ServerConnection sc = new ServerConnection(this, fec.getPrimaryAddress(), fec.getPrimaryPort());
            sc.run();
        } catch (IOException e) {
            System.out.println("Connection to primary server could not be established");
            e.printStackTrace();
        }
	}

	/**
     * Listen for new connections, starting a new ClientConnection for each
     *
     * @throws IOException
     */
    public void listenForConnections() throws IOException {
        socket = new ServerSocket(port);
        System.out.println("Waiting for client messages on port " + port);

        // Keep listening
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

    public synchronized void addClient(ClientConnection cc) {
        System.out.println("Client added to client list");
        if (eh.numEvents() > 0) {
            cc.send(eh);
            System.out.println("Sent existing events to client");
        }
        clients.add(cc);
    }

    public synchronized void addEvent(Object o) {
        System.out.println(o.toString() + " added to events list and sent to clients");
        eh.addEvent(o);
        EventHandler singleEvent = new EventHandler();
        singleEvent.addEvent(o);
        for (ClientConnection cc : clients) {
            cc.send(singleEvent);
        }
    }

    /**
     * @return port number the server is bound to
     */
    public int getPort() {
        return port;
    }

    public enum Type {
        PRIMARY, BACKUP
    }

	@Override
	public synchronized void addEvents(EventHandler extraEh) {
		eh.addEvents(extraEh);		
	}
}
