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

public class ClientConnection extends Thread {
    private final Server server;
    private final Socket socket;

    private final ObjectInputStream input;
    private final ThreadSafeObjectWriter output;
    
    /**
     * Creates a new client connection
     *
     * @throws IOException if input and output streams can't be created
     */
    public ClientConnection(Server _server, Socket _socket) throws IOException {
        server = _server;
        socket = _socket;

        input = new ObjectInputStream(socket.getInputStream());
        output = new ThreadSafeObjectWriter(new ObjectOutputStream(socket.getOutputStream()));
    }

    /**
     * Thread main loop, used to listen to messages
     */
    public void run() {
        server.addClient(this);
        System.out.println("client connected " + socket.getInetAddress().toString() + ":" + socket.getPort());
        
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

    public void send(Object o) {
        try {
            output.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
