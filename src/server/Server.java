package server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class Server {
	private ArrayList<ClientConnection> clients = new ArrayList<ClientConnection>(); // Keep a list of the clients
	
	private ServerListener sl;
	
	private int port;


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
	 * Start the server and bind to a port
	 * @param port number to start the server on
	 * @throws IOException if the server could not bind to the specified port
	 */
	public Server(int _port) throws IOException {
		port = _port;
	}


	private void talkWithFrontend(String s, int sport) {
		// ask frontend first
		try {
			sl = new ServerListener(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
