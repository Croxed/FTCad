package DCAD;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import common.DeleteEventMessage;
import common.ClientWithFrontEnd.ConnectionRequestMessage;
import common.ClientWithFrontEnd.ConnectionRespondMessage;

/**
 * A class that represents the CAD client side connection. 
 * The ServerConnection should connect to a front end and ask for connection details to the primary server. 
 *
 */
public class ConnectionHandler implements Runnable {

	private InetAddress mFrontEndAddress;
	private int mFrontEndPort = -1;
	private InetAddress mServerAddress;
	private int mServerPort;
	private GUI mGUI;
	private ServerConnection mSc;
	
	/**
	 * Constructs the ServerConnection, creating the address and setting the port.
	 * @param frontEndAddress the address to the front end
	 * @param frontEndPort the port to the front end
	 */
	public ConnectionHandler(GUI gui, String frontEndAddress, int frontEndPort) {
		mGUI = gui;
		try {
			mFrontEndAddress = InetAddress.getByName(frontEndAddress);
		} catch (UnknownHostException e) { e.printStackTrace(); }
		mFrontEndPort = frontEndPort;
	}

	/**
	 * Runs the front end. Connects to the front end and establishes the connection to the server.
	 */
	@Override
	public void run() {
		while(true) {
			mGUI.removeEvents();
		
			getMainServerFromFrontend();
			
			try {
				mSc = new ServerConnection(mGUI, mServerAddress, mServerPort);
				mSc.run();
			} catch (IOException e) {
				System.out.println("Connection to server could not be established");
				e.printStackTrace();
			}
		}
	}

	/**
	 * Attempts to the connect to the front end. 
	 * When connected, attempts to receive connection details from the front end. 
	 * Then, disconnects from the front end and connects to the specified primary server. 
	 */
	private void getMainServerFromFrontend() {
		// Try and connect to the frontend   
		while(true) {
			System.out.println("Attempting to connect to Frontend");
			try {
				Socket socket = new Socket(mFrontEndAddress, mFrontEndPort);
				ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

				System.out.println("Getting address information to the main server");
				try {
					output.writeObject(new ConnectionRequestMessage());
					
					try {
						Object response = input.readObject();
						
						if(response instanceof ConnectionRespondMessage) {
							ConnectionRespondMessage msg = (ConnectionRespondMessage)response;
							// Set the address to the primary server
							mServerAddress = msg.getAddress();
							mServerPort = msg.getPort();
							System.out.println("Will connect to primary server with IP: " + mServerAddress + " and port: " + mServerPort);
							try {
								socket.close();
							} catch (IOException e) {
								System.err.println("Could not disconnect from the Frontend");
							}
							return;
						} else {
							System.out.println("Unknown text received from frontend");
						}
					} catch (IOException | ClassNotFoundException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					System.out.println("Could not send a connection request message");
				}
				
				System.out.println("Disconnecting from Frontend");
				try {
					socket.close();
				} catch (IOException e) {
					System.err.println("Could not disconnect from the Frontend");
				}
			} catch (IOException e) {
				System.out.println("Could not connect to Frontend");
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
	}

	
	/**
	 * Send a newly created object to the server. 
	 * @param deletedGObject the deleted object
	 */
	public void sendDeleteObject(GObject deletedGObject) {
		mSc.write(new DeleteEventMessage(deletedGObject.getUID()));
	}
	
	/**
	 * Send a newly created object to the server. 
	 * @param createdGObject the created object
	 */
	public void sendCreateObject(GObject createdGObject) {
		mSc.write(createdGObject);
	}

	
}
