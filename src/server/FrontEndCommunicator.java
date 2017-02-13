package server;

import java.io.*;
import java.net.*;

import common.*;

public class FrontEndCommunicator extends Thread {
	private Server server;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private final String hostName;
	private final int hostPort;
	
	private Boolean type;

	public FrontEndCommunicator(Server _server, String _hostName, int _hostPort) {
		server = _server;
		hostName = _hostName;
		hostPort = _hostPort;
	}
	
	private boolean isConnected() {
		return (socket != null && socket.isConnected());
	}
	
	
	public void run() {
		while (true) {
			try {
				socket = new Socket(java.net.InetAddress.getByName(hostName), hostPort);
				input = new ObjectInputStream(socket.getInputStream());
				output = new ObjectOutputStream(socket.getOutputStream());
				
				output.writeObject(new ServerRequestMessage(hostPort));
				
				listenToFrontEnd();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void listenToFrontEnd() {
		while (true) {
			try {
				Object obj = input.readObject();
				
				if (obj instanceof ServerRespondMessage) {
					System.out.println("Frontend responded");
				} else {
					System.out.println("Can't parse message");
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Can't parse message");
			} catch (IOException e) {
				System.out.println("Error when reading");
				return;
			}
		}
	}
	
	public Boolean getType() {
		return type;
	}
}
