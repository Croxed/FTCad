package DCAD;

import java.io.*;
import java.net.*;

import common.*;

public class ServerConnection {
	private Socket mSocket;
	private ObjectOutputStream mOStream;
	private ObjectInputStream mIStream;
	private EventReceiver mEr;
	private boolean mIsConnected;
	
	public ServerConnection(EventReceiver er, InetAddress serverAddress, int serverPort) throws IOException {
		mEr = er;
		mSocket = new Socket(serverAddress, serverPort);
		mOStream = new ObjectOutputStream(mSocket.getOutputStream());
		mIStream = new ObjectInputStream(mSocket.getInputStream());
	}

	public void run() {
		mIsConnected = true;
		
		System.out.println("Starting Pinger");
		
		Thread ping = new Thread(new Pinger());
		ping.start();

		listenForServerActions();
		
		mIsConnected = false;
		
		System.out.println("Connection Finished, stopping pinger and closing socket");
		
		try {
			ping.interrupt();
			ping.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			mSocket.close();
		} catch (IOException e) { }
	}

	/**
	 * Listens to the actions received from the server.
	 */
	private void listenForServerActions() {
		System.out.println("Listening for server actions");
 		while(true) {
			try {
				// Wait for object
				Object input = mIStream.readObject();
				// Handle the input and put to / update the GUI.
				if(input instanceof EventHandler) {
					EventHandler eh = (EventHandler)input;
					// Add the shape to the GUI's list of objects. But need a reference to the GUI first. 
					mEr.addEvents(eh);
				} else if(input instanceof PingMessage) {
					System.out.println("Received a ping message");
				} else {
					System.err.println("Got some unknown shit from the server");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
	 	}
	}
	
	public void write(Object obj) {
		try {
			mOStream.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Socket is closed. You're probably not connected yet...");
		}
	}

	@SuppressWarnings("Duplicates")
	/**
	 * Keep pinging the server
	 */
    private class Pinger implements Runnable{
        public void run() {
            while (mIsConnected) {
                try {
                    mOStream.writeObject(new PingMessage());
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
