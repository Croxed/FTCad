package server;

import java.io.*;
import java.net.*;

import common.ServerWithFrontEnd.ConnectionRespondMessage;
import common.PingMessage;
import common.ServerWithFrontEnd.ConnectionRequestMessage;

public class FrontEndCommunicator extends Thread {
	private Server server;
	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private final String hostName;
	private final int hostPort;
	
	private Boolean primary;

	/**
	 * Connects and listens to frontend 
	 */
	public FrontEndCommunicator(Server _server, String _hostName, int _hostPort) {
		server = _server;
		hostName = _hostName;
		hostPort = _hostPort;
	}
	
	private boolean isConnected() {
		return (socket != null && socket.isConnected());
	}
	

	/**
	 * Connects and listens to frontend 
	 */
	public void run() {
		while (true) {
			try {
				System.out.println("Trying to send server request message");
				socket = new Socket(java.net.InetAddress.getByName(hostName), hostPort);
				socket.setSoTimeout(5000);
				input = new ObjectInputStream(socket.getInputStream());
				output = new ObjectOutputStream(socket.getOutputStream());
			
				output.writeObject(new ConnectionRequestMessage(server.getPort()));
				System.out.println("Sent server request message");
				
				Pinger ping = new Pinger();
				ping.start();
				
				listenToFrontEnd();
				try {
					ping.interrupt();
					ping.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				socket.close();
				
			} catch (IOException e) {
				System.out.println("Connection refused. Trying again in 1 second");
				try { Thread.sleep(1000); } catch (InterruptedException sleep) { }
				
			}
		}
	}
	
	private void listenToFrontEnd() {
		while (true) {
			try {
				Object obj = input.readObject();
				
				if (obj instanceof ConnectionRespondMessage) {
					primary = ((ConnectionRespondMessage) obj).isPrimary();
					System.out.println("Frontend responded with a ConnectionRespondMessage");
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
		return primary;
	}
	
	private class Pinger extends Thread {
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					output.writeObject(new PingMessage());
					try { Thread.sleep(1000); } catch (InterruptedException sleep) { }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
