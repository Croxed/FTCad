package server;

import common.PingMessage;
import common.Pingu;
import common.ServerWithFrontEnd.ConnectionRequestMessage;
import common.ServerWithFrontEnd.ConnectionRespondMessage;
import common.ServerWithFrontEnd.isPrimaryMessage;
import common.ThreadSafeObjectWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A helper class to let the server communicate with the frontend
 */
public class FrontEndCommunicator extends Thread {
	// Connection information to the frontend
    private final String hostName;
    private final int hostPort;
    
    private final int serverPort; // Port for the server
    
    private ObjectInputStream input; 
    
    // The type of the server, if it is primary or backup
    private Type type;
    // If the server is a backup, keep track of the primary address and port
    private InetAddress primaryAddress;
    private int primaryPort;

    /**
     * Creats a fec instance
     * @param _serverPort for the server
     * @param _hostName for the frontend
     * @param _hostPort for the frontend
     */
    public FrontEndCommunicator(int _serverPort, String _hostName, int _hostPort) {
        serverPort = _serverPort;
        hostName = _hostName;
        hostPort = _hostPort;
    }

    /**
     * Connects and listens to the frontend
     */
    public void run() {
        while (true) {
            try {
            	// Try to connect to the frontend and send a server request message
                System.out.println("Trying to send server request message");
                Socket socket = new Socket(InetAddress.getByName(hostName), hostPort);
                socket.setSoTimeout(5000);
                input = new ObjectInputStream(socket.getInputStream());
                ThreadSafeObjectWriter output = new ThreadSafeObjectWriter(new ObjectOutputStream(socket.getOutputStream()));

                output.writeObject(new ConnectionRequestMessage(serverPort, type == Type.PRIMARY));
                System.out.println("Sent server request message");

                // Wait for a connection respond message, restart connection if any other message is received
                Object obj = input.readObject();
                if (obj instanceof ConnectionRespondMessage) {
                    ConnectionRespondMessage crm = (ConnectionRespondMessage) obj;
                    primaryAddress = crm.getPrimaryAddress();
                    primaryPort = crm.getPrimaryPort();
                    type = (crm.isPrimary() ? Type.PRIMARY : Type.BACKUP);
                    System.out.println("Frontend responded with a ConnectionRespondMessage");
                    
                    // The connection has been setup
                    
                    // Start the ping thread
                    Pingu pingRunnable = new Pingu(output);
                    Thread pingThread = new Thread(pingRunnable);
                    pingThread.start();

                    // Listen for messages
                    listenToFrontEnd();
                    
                    // Stop the ping thread
                    try {
                        pingRunnable.shutdown();
                        pingThread.interrupt();
                        pingThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                socket.close();

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Connection refused. Trying again in 1 second");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException sleep) {
                }

            }
        }
    }

    /**
     * Listen for messages from the front end
     */
    private void listenToFrontEnd() {
        while (true) {
            try {
            	// Wait for objects
                Object obj = input.readObject();
                
                // Object is received, try to parse it
                if (obj instanceof isPrimaryMessage) {
                	// The server has received a message telling it that it is a primary server
                    System.out.println("Backup server evolves into...... PRIMARY SERVER!!!");
                    type = Type.PRIMARY;
                } else if (obj instanceof PingMessage) {
                    System.out.print(".");
                } else {
                    System.out.println("Can't parse message" + obj.getClass());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Can't parse message");
            } catch (IOException e) {
                System.out.println("Connection with frontend failed.");
                return;
            }
        }
    }

    /**
     * Return the type of the server
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the address of the primary server
     */
    public InetAddress getPrimaryAddress() {
        return primaryAddress;
    }

    /**
     * Returns the port of the primary server
     */
    public int getPrimaryPort() {
        return primaryPort;
    }

    public enum Type {PRIMARY, BACKUP}
}
