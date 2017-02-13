package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
	private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>(); // Keep a list of the clients
	
	private ServerSocket socket;


	/**
	 * Start the server
	 * @param starting arguments when opening from a terminal
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java Server portnumber");
			System.exit(-1);
		}
		try {
			// Start the server and listen for connections
			Server instance = new Server(Integer.parseInt(args[0]));
			instance.listenForConnections();
		} catch (NumberFormatException e) {
			System.err.println("Error: Port number must be an integer." + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("Error: Can't open socket on port." + e);
			System.exit(-1);
		}
	}

	/**
	 * Start the server and bind to a port
	 * @param port number to start the server on
	 * @throws IOException if the server could not bind to the specified port
	 */
	public Server(int port) throws IOException {
		socket = new ServerSocket(port);
	}
	

	/**
	 * Listen for new connections, starting a new ClientConnection for each
	 */
	private void listenForConnections() {
		System.out.println("Waiting for client messages... ");
		
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

}
