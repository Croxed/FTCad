package server;

import common.ClientWithServer.GObject;
import common.ClientWithServer.DeleteEventMessage;
import common.PingMessage;
import common.Pingu;
import common.ThreadSafeObjectWriter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Class to handle connections to a client
 * Listens from messages from clients and sends them to the Server object
 * Can also send any serializable object to the clients
 */
public class ClientConnection extends Thread {
    private final Server server;
    private final Socket socket;
    private final ObjectInputStream input;
    private final ThreadSafeObjectWriter output;
    
    /**
     * Creates a new client connection to the supplied socket
     * @param _server the server instance to send any received events to
     * @param _socket the client socket
     * @throws IOException if input and output streams can't be created
     */
    public ClientConnection(Server _server, Socket _socket) throws IOException {
        server = _server;
        socket = _socket;
        socket.setSoTimeout(5000);

        input = new ObjectInputStream(socket.getInputStream());
        output = new ThreadSafeObjectWriter(new ObjectOutputStream(socket.getOutputStream()));
    }

    /**
     * Thread main loop, used to listen to messages from the client
     */
    public void run() {
    	// Tell the server that the client is connected
        server.addClient(this);
        
        System.out.println("client connected " + socket.getInetAddress().toString() + ":" + socket.getPort());
        
        // Start a ping thread
        Pingu pingRunnable = new Pingu(output);
        Thread pingThread = new Thread(pingRunnable);
        pingThread.start();
        
        while (true) {
            try {
                Object inData = input.readObject();
                // A message has arrived, try to parse and handle it
                if (inData instanceof PingMessage) {
                    System.out.print(".");
                } else if (inData instanceof DeleteEventMessage || inData instanceof GObject) {
                	// The message is a delete or draw event, send to server
                    System.out.println(inData.toString() + " received, sending it to all clients.");
                    server.addEvent(inData);
                } else {
                    System.out.println("Object not recognized. " + inData.toString());
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Object not recognized");
                e.printStackTrace();
            } catch (IOException e) {
            	
                // Can't receive on socket, client has disconnected
                System.out.println("client disconnected " + socket.getInetAddress().toString() + ":" + socket.getPort());
                // Shut down ping thread
                try {
                	pingRunnable.shutdown();
                    pingThread.interrupt();
                    pingThread.join();
                } catch (InterruptedException e2) {
                    e.printStackTrace();
                }
                try {
                    socket.close();
                } catch (IOException e2) { }
                
                return;
            }
        }
    }

    /**
     * Send an object to the client
     * @param the object to send
     */
    public void send(Object o) {
        try {
            output.writeObject(o);
        } catch (IOException e) {
            System.err.println("Could not send object");
        }
    }
}
