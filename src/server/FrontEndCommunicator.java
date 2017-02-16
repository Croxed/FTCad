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
	
    private volatile boolean isConnected = false;

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
				
				isConnected = true;
				
				Thread ping = new Thread(new Pinger());
				ping.start();
				
				listenToFrontEnd();
				
				isConnected = false;
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

	/**
	 * Listen for messages from the front end
	 */
	private void listenToFrontEnd() {
		while (true) {
			try {
				Object obj = input.readObject();
				
				if (obj instanceof ConnectionRespondMessage) {
					primary = ((ConnectionRespondMessage) obj).isPrimary();
					System.out.println("Frontend responded with a ConnectionRespondMessage");
				} else if (obj instanceof PingMessage) {
					System.out.print(".");
				} else {
					System.out.println("Can't parse message" + obj.getClass());
				}
			} catch (ClassNotFoundException e) {
				System.out.println("Can't parse message");
			} catch (IOException e) {
				System.out.println("Error when reading");
				e.printStackTrace();
				return;
			}
		}
	}
	/**
	 * Get which type of server it is
	 */
	public Boolean getType() {
		return primary;
	}

	/**
	 * Keep pinging the frontend
	 */
    private class Pinger implements Runnable{
        public void run() {
            while (isConnected) {
                try {
                    output.writeObject(new PingMessage());
                    System.out.print("!");
                } catch (IOException e) {
                    System.err.println("Could not ping");
                }
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Could not sleep");
                }
            }
        }
    }
}
