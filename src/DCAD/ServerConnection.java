package DCAD;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import common.*;

public class ServerConnection implements Runnable {

	private boolean isConnected = false;
	
	private Socket mSocket;
	private ObjectOutputStream mOStream;
	private ObjectInputStream mIStream;
	private InetAddress mFrontEndAddress;
	private int mFrontEndPort = -1;

	private InetAddress mServerAddress;
	private int mServerPort;
	
	/**
	 * Constructs the ServerConnection, creating the address and setting the port.
	 * @param frontEndAddress the address to the front end
	 * @param frontEndPort the port to the front end
	 */
	public ServerConnection(String frontEndAddress, int frontEndPort) {
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
		
		System.out.println("Attempting to connect to Front end");
		connectToFrontEnd();
		
		System.out.println("Getting address information for main server");
		getAddressFromFrontEnd();
		
		System.out.println("Disconnecting from Front end");
		disconnectSocket();
		
		System.out.println("Connecting to server");
		connectToServer();
		
		while(isConnected) {
			// Handle server actions and stuff, like updating the GUI with other players actions. 
		}
		
		System.out.println("Disconnects from the server");
		disconnectSocket();
		
		// Maybe want to connect to the front end again or something.
		//  
	}

	/**
	 * Connects to the primary server. The address should be obtained from the front end.
	 */
	private void connectToServer() {
		try {
			mSocket = new Socket(mServerAddress, mServerPort);
			mOStream = new ObjectOutputStream(mSocket.getOutputStream());
			mIStream = new ObjectInputStream(mSocket.getInputStream());
		} catch (IOException e) { e.printStackTrace(); }
	}

	/**
	 * Connects to the front end, in order to get the address to the main server. 
	 */
	private void connectToFrontEnd() {
		try {
			mSocket = new Socket(mFrontEndAddress, mFrontEndPort);
			mOStream = new ObjectOutputStream(mSocket.getOutputStream());
			mIStream = new ObjectInputStream(mSocket.getInputStream());
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	/**
	 * Set the address and port that is needed to connect to the server. Needs to connect to the front end and disconnect after obtaining the info.
	 */
	private void getAddressFromFrontEnd() {
		Object response = null;
		try {
			mOStream.writeObject(new ClientRequestMessage());
			response = mIStream.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		if(response instanceof ServerRespondMessage) {
			// Set the address to the primary server
			// mServerAddress = ?
			// mServerPort = ?
		} else {
			// Got some unknown shit from the server probably.
		}
	}

	/**
	 * Disconnects from the Front end.
	 */
	private void disconnectSocket() {
		try {
			mOStream.close();
			mIStream.close();
			mSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
