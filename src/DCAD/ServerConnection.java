package DCAD;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import common.ClientWithFrontEnd.ConnectionRequestMessage;
import common.ClientWithFrontEnd.ConnectionRespondMessage;

/**
 * A class that represents the CAD client side connection. 
 * The ServerConnection should connect to a front end and ask for connection details to the primary server. 
 * @author mattiasolsson
 *
 */
public class ServerConnection implements Runnable {

	private boolean mIsConnected = false;
	private boolean mIsListening = false;
	
	private Socket mSocket;
	private ObjectOutputStream mOStream;
	private ObjectInputStream mIStream;
	private InetAddress mFrontEndAddress;
	private int mFrontEndPort = -1;

	private InetAddress mServerAddress;
	private int mServerPort;
	private GUI mGUI;
	
	/**
	 * Constructs the ServerConnection, creating the address and setting the port.
	 * @param frontEndAddress the address to the front end
	 * @param frontEndPort the port to the front end
	 */
	public ServerConnection(GUI gui, String frontEndAddress, int frontEndPort) {
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
		
		initializeConnections();
		
		while(mIsConnected) {
			// While the client is connected to the server
			// Supply the clients actions to the server and listen for actions created from the server
			mIsListening = true;
			listenForServerActions();
		}
		
		System.out.println("Disconnecting from the server");
		try {
			disconnectSocket();
		} catch (IOException e) {
			System.err.println("Could not disconnect from the server");
		}
		
		// Maybe want to connect to the front end again or something.
		//  
	}

	/**
	 * Initializes all connections to the server. 
	 */
	private void initializeConnections() {
		
		// Set up some flags
		boolean isConnectedToFrontEnd = false;

		// Keep the number of attempts 
		int attempts = 0;
		
		// Try and connect to the frontend   
		do {
			System.out.println("Attempting to connect to Frontend");
			try {
				connectToFrontEnd();
				isConnectedToFrontEnd = true;
			} catch (IOException e) {
				isConnectedToFrontEnd = false;
			}
			
			// Take a nap after each failed attempt 
			if(!isConnectedToFrontEnd) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
			}
		} while(!isConnectedToFrontEnd && attempts++ < 10);
		
		// If is connected to the front end, proceed with getting the connection details to the primary server and connect to it
		if(isConnectedToFrontEnd) {
			System.out.println("Getting address information to the main server");
			getAddressFromFrontEnd();
			
			System.out.println("Disconnecting from Frontend");
			try {
				disconnectSocket();
			} catch (IOException e) {
				System.err.println("Could not disconnect from the Frontend");
			}
			
			System.out.println("Connecting to primary server");
			try {
				connectToPrimaryServer();
			} catch (IOException e) {
				System.err.println("Could not connect to the primary server");
			}
			// If it didn't even connect to the front end, we will end up here 
		} else {
			System.out.println("Failed to connect to the Frontend. Don't use this program any more pls");
		}

	}

	/**
	 * Listens to the actions received from the server.
	 */
	private void listenForServerActions() {
		System.out.println("Listening for server actions");
		while(mIsListening) {
			System.out.println("Waiting for input from the server");
			// Receive some input from the server
			Object input = null;
			try {
				input = mIStream.readObject();
			} catch (ClassNotFoundException | IOException e) {
				mIsListening = false;
				System.err.println("Received some weird shit from the server " + e.getMessage());
			}
			
			// Handle the input and put to / update the GUI.
			if(input instanceof GObject) {
				GObject gObject = (GObject)input;
				// Add the shape to the GUI's list of objects. But need a reference to the GUI first. 
				mGUI.addShape(gObject);
			} else {
				System.err.println("Got some unknown shit from the server");
			}
		}
	}
	
	/**
	 * Send a newly created object to the server. 
	 * @param deletedGObject the deleted object
	 */
	public void sendDeleteObject(GObject deletedGObject) {
		try {
			mOStream.writeObject(deletedGObject);
		} catch (IOException e) {
			System.err.println("Failed to send a deleted object.");
		}
	}
	
	/**
	 * Send a newly created object to the server. 
	 * @param createdGObject the created object
	 */
	public void sendCreateObject(GObject createdGObject) {
		try {
			mOStream.writeObject(createdGObject);
		} catch (IOException e) {
			System.err.println("Failed to send a ClientActionMessage to the server");
		}
	}

	/**
	 * Connects to the primary server. The address should be obtained from the front end.
	 * @throws IOException if the socket or the streams cannot be instantiated
	 */
	private void connectToPrimaryServer() throws IOException {
		mSocket = new Socket(mServerAddress, mServerPort);
		mOStream = new ObjectOutputStream(mSocket.getOutputStream());
		mIStream = new ObjectInputStream(mSocket.getInputStream());
		mIsConnected = true;
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
			// Got some unknown shit from the server probably, if ending up here!! 
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
		mIsConnected = false;
	}

}
