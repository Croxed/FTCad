package common.ClientWithServer;

import common.ClientWithServer.EventHandler;
import common.ClientWithServer.EventReceiver;
import common.PingMessage;
import common.Pingu;
import common.ThreadSafeObjectWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A handler for connecting and listening to events from the primary server
 */
public class ServerConnection {
	// Keep track of the connection
    private Socket mSocket;
    private ThreadSafeObjectWriter mOutput;
    private ObjectInputStream mIStream;
    // The EventReceiver to send received messages to
    private EventReceiver mEr;

    /**
     * Create a new server connection instance
     * @param er The instance to send any received messages to
     * @param serverAddress The address of the primary server
     * @param serverPort The port of the primary server
     * @throws IOException if the connection failed
     */
    public ServerConnection(EventReceiver er, InetAddress serverAddress, int serverPort) throws IOException {
        mEr = er;
        mSocket = new Socket(serverAddress, serverPort);
        mOutput = new ThreadSafeObjectWriter(new ObjectOutputStream(mSocket.getOutputStream()));
        mIStream = new ObjectInputStream(mSocket.getInputStream());
    }

    
    /**
     * Keep the connection alive and listen for events
     */
    public void run() {
        // Start pinger
        Pingu pingRunnable = new Pingu(mOutput);
        Thread pingThread = new Thread(pingRunnable);
        pingThread.start();

        // Listen for server actions
        listenForServerActions();

        System.out.println("Connection Finished, stopping pinger and closing socket");
        
        // Stop pinger
        try {
        	pingRunnable.shutdown();
            pingThread.interrupt();
            pingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        try {
            mSocket.close();
        } catch (IOException e) {
        }
    }

    /**
     * Listens to the actions received from the server.
     * Begins waiting for an object
     * If the object is an EvenHandler, add the object to the clients ArrayList
     * If the object is a pingMessage, print out the ping
     */
    private void listenForServerActions() {
        System.out.println("Listening for server actions");
        while (true) {
            try {
            	// Wait for object
                Object input = mIStream.readObject();
                if (input instanceof EventHandler) {
                	// The object was an EventHandler, send to the receiver
                    EventHandler eh = (EventHandler) input;
                    mEr.addEvents(eh);
                } else if (input instanceof PingMessage) {
                    System.out.print(".");
                } else {
                    System.err.println("Unknown message receieved from the server");
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Unknown message receieved from the server");
            } catch (IOException e) {
            	// The socket closed, exit
                return;
            }
        }
    }
    
    /**
     * Writes the object to the server
     * @param obj Object to write to the outputStream.
     */
    public void write(Object obj) {
        try {
            mOutput.writeObject(obj);
        } catch (IOException e) {
            System.err.println("Socket is closed. You're probably not connected yet...");
        }
    }
}
