package server;

import java.io.*;
import java.net.*;

public class ServerListener extends Thread {
	private ServerSocket socket;

	public ServerListener(int port) throws IOException {
		socket = new ServerSocket(port);
	}

	/**
	 * Listen for new connections, starting a new ClientConnection for each
	 */
	public void run() {
		System.out.println("Waiting for client messages... ");
		
		// Keep listening
		while (true) {
			Socket client;
			try {
				// Wait for new connections and accept
				client = socket.accept();
				// Create a new ClientConnection thread for the new socket and start the thread
				ClientConnection cc = new ClientConnection(client);
				cc.start();
			} catch (IOException e) {
				System.out.println("Error when accepting connection");
				e.printStackTrace();
			}
		}
	}
}
