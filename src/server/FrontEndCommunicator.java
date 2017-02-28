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

public class FrontEndCommunicator extends Thread {
    private final String hostName;
    private final int hostPort;
    private Server server;
    private Socket socket;
    private ObjectInputStream input;
    private ThreadSafeObjectWriter output;
    private InetAddress primaryAddress;
    private int primaryPort;
    private Type type;

    /**
     * Connects and listens to frontend
     */
    public FrontEndCommunicator(Server _server, String _hostName, int _hostPort) {
        server = _server;
        hostName = _hostName;
        hostPort = _hostPort;
    }

    private boolean isConnected() {
        return (socket != null && socket.isConnected());
    }

    /**
     * Connects and listens to frontend
     */
    public void run() {
        while (true) {
            try {
                System.out.println("Trying to send server request message");
                socket = new Socket(java.net.InetAddress.getByName(hostName), hostPort);
                socket.setSoTimeout(5000);
                input = new ObjectInputStream(socket.getInputStream());
                output = new ThreadSafeObjectWriter(new ObjectOutputStream(socket.getOutputStream()));

                output.writeObject(new ConnectionRequestMessage(server.getPort(), type == Type.PRIMARY));
                System.out.println("Sent server request message");

                Object obj = input.readObject();
                if (obj instanceof ConnectionRespondMessage) {
                    ConnectionRespondMessage crm = (ConnectionRespondMessage) obj;
                    primaryAddress = crm.getPrimaryAddress();
                    primaryPort = crm.getPrimaryPort();
                    type = (crm.isPrimary() ? Type.PRIMARY : Type.BACKUP);
                    System.out.println("Frontend responded with a ConnectionRespondMessage");
                }

                Pingu pingRunnable = new Pingu(output);
                Thread pingThread = new Thread(pingRunnable);
                pingThread.start();

                listenToFrontEnd();

                try {
                    pingRunnable.shutdown();
                    pingThread.interrupt();
                    pingThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
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
                Object obj = input.readObject();

                if (obj instanceof isPrimaryMessage) {
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
     * Get which type of server it is
     */
    public Type getType() {
        return type;
    }

    public InetAddress getPrimaryAddress() {
        return primaryAddress;
    }

    public int getPrimaryPort() {
        return primaryPort;
    }

    public enum Type {PRIMARY, BACKUP}
}
