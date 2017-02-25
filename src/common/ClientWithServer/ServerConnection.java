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

public class ServerConnection {
    private Socket mSocket;
    private ThreadSafeObjectWriter mOutput;
    private ObjectInputStream mIStream;
    private EventReceiver mEr;

    public ServerConnection(EventReceiver er, InetAddress serverAddress, int serverPort) throws IOException {
        mEr = er;
        mSocket = new Socket(serverAddress, serverPort);
        mOutput = new ThreadSafeObjectWriter(new ObjectOutputStream(mSocket.getOutputStream()));
        mIStream = new ObjectInputStream(mSocket.getInputStream());
    }

    public void run() {

        System.out.println("Starting Pinger");

        Pingu pingRunnable = new Pingu(mOutput);
        Thread pingThread = new Thread(pingRunnable);
        pingThread.start();

        listenForServerActions();


        System.out.println("Connection Finished, stopping pinger and closing socket");

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
     */
    private void listenForServerActions() {
        System.out.println("Listening for server actions");
        while (true) {
            try {
                // Wait for object
                Object input = mIStream.readObject();
                // Handle the input and put to / update the GUI.
                if (input instanceof EventHandler) {
                    EventHandler eh = (EventHandler) input;
                    // Add the shape to the GUI's list of objects. But need a reference to the GUI first.
                    mEr.addEvents(eh);
                } else if (input instanceof PingMessage) {
                    System.out.print(".");
                } else {
                    System.err.println("Got some unknown shit from the server");
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                return;
            }
        }
    }

    public void write(Object obj) {
        try {
            mOutput.writeObject(obj);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("Socket is closed. You're probably not connected yet...");
        }
    }
}
