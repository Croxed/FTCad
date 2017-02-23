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

	private Socket mSocket;
	private ObjectOutputStream mOStream;
	private ObjectInputStream mIStream;
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
		
			initializeConnections();
			
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
	private void initializeConnections() {
		// Try and connect to the frontend   
		while(true) {
			System.out.println("Attempting to connect to Frontend");
			try {
				connectToFrontEnd();

				System.out.println("Getting address information to the main server");
				getAddressFromFrontEnd();
				
				System.out.println("Disconnecting from Frontend");
				try {
					disconnectSocket();
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

	/**
	 * Connects to the front end, in order to get the address to the main server.
	 * @throws IOException if the socket or the streams cannot be opened.
	 */
	private void connectToFrontEnd() throws IOException {
		mSocket = new Socket(mFrontEndAddress, mFrontEndPort);
		mOStream = new ObjectOutputStream(mSocket.getOutputStream());
		mIStream = new ObjectInputStream(mSocket.getInputStream());
	}
	
	/**
	 * Set the address and port that is needed to connect to the server. Needs to connect to the front end and disconnect after obtaining the info.
	 */
	private void getAddressFromFrontEnd() {
		Object response = null;
		try {
			mOStream.writeObject(new ConnectionRequestMessage());
			response = mIStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if(response instanceof ConnectionRespondMessage) {
			ConnectionRespondMessage msg = (ConnectionRespondMessage)response;
			// Set the address to the primary server
			mServerAddress = msg.getAddress();
			mServerPort = msg.getPort();
			
			System.out.println("Will connect to primary server with IP: " + mServerAddress + " and port: " + mServerPort);
		} else {
			// Got some unknown shit from the frontend probably, if ending up here!! 
		}
	}

	/**
	 * Disconnects the socket, whether is connected to the front end or the primary server.
	 * @throws IOException if the streams cannot be closed, or if the socket cannot be closed. 
	 */
	private void disconnectSocket() throws IOException {
		mOStream.close();
		mIStream.close();
		mSocket.close();
	}
	
}
