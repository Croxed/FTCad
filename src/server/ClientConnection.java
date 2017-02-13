package server;

import java.io.*;
import java.net.*;

import common.*;

public class ClientConnection extends Thread {
	//private final Server server;
	private final Socket socket;
	
	private final ObjectInputStream input; 
	private final ObjectOutputStream output;
	
	/**
	 * Creates a new client connection
	 * @throws IOException if input and output streams can't be created
	 */
	public ClientConnection(Socket _socket) throws IOException {
		//server = _server;
		socket = _socket;
		
		input = new ObjectInputStream(socket.getInputStream());
		output = new ObjectOutputStream(socket.getOutputStream());
	}

	/**
	 * Thread main loop, used to listen to messages
	 */
	public void run() {
		while(true) {
			try {
				Object inData = input.readObject();
				
				// A message has arrived, try to parse and handle it
				if (inData instanceof PingMessage) {
					System.out.println("Ping received");
				} else {
					System.out.println("Object not recognized");
				}
			} catch (ClassNotFoundException e1) {
				System.out.println("Object not recognized");
				e1.printStackTrace();
			} catch (IOException e) {
				// Can't receive on socket, client has disconnected
				try { socket.close(); } catch (IOException e2) {}
				return;
			}
		}
	}

}
