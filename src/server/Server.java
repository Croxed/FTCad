package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import common.*;

public class Server {
	public enum Type {
		PRIMARY, BACKUP
	}
	
	private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>(); // Keep a list of the clients
	
	private FrontEndCommunicator fec;
	
	private int port;
	
	private ServerSocket socket;
	
	private EventHandler eh;


	/**
	 * Start the server
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
	 * Init Server object
	 * @param port number to start the server on
	 * @throws IOException if the server could not bind to the specified port
	 */
	public Server(int _port) throws IOException {
		port = _port;
	}


	/**
	 * Talk with frontend and if successful listen for connections
	 * @param hostname of frontend
	 * @param port of frontend
	 * @throws IOException 
	 */
	private void talkWithFrontend(String hostName, int hostPort) throws IOException {
		//Starts the front end thread
		fec = new FrontEndCommunicator(this, hostName, hostPort);
		fec.start();
		// Wait for response from frontend before starting server
		while (true) {
			if (fec.getType() != null) {
				listenForConnections();
				return;
			}
			try { Thread.sleep(50); } catch (InterruptedException sleep) { }
		}
	}

	/**
	 * Listen for new connections, starting a new ClientConnection for each
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
		clients.add(cc);
	}
	
	public synchronized void addEvent(Object o) {
		System.out.println(o.toString() + " added to events list and sent to clients");
		eh.addEvent(o);
		for (ClientConnection cc : clients) {
			cc.send(o);
		}
	}

	/**
	 * @return port number the server is bound to
	 */
	public int getPort() {
		return port;
	}
}
