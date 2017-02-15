package frontend;

import common.PingMessage;
import common.ServerWithFrontEnd.ConnectionRespondMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by oscar on 2017-02-13.
 */
public class ServerConnection implements Runnable {
    private Socket m_socket;
    private int m_portNr;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private volatile boolean isConnected;
    private FrontEnd m_fronEnd;

    public ServerConnection(FrontEnd frontEnd, Socket socket, ObjectOutputStream oStream, ObjectInputStream iStream, int portnr) {
        m_fronEnd = frontEnd;
        m_portNr = portnr;
        m_socket = socket;
        outputStream = oStream;
        inputStream = iStream;
        isConnected = true;
        try {
            System.out.println("Trying to send ConnectionRespondMessage");
            // TODO: always tells servers they are primary
            outputStream.writeObject(new ConnectionRespondMessage(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public InetAddress getAddress() {
        return m_socket != null ? m_socket.getInetAddress() : null;
    }

    public int getPort() {
        return m_portNr;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void run() {
        Thread pingThread = new Thread(new Pinger());
        pingThread.start();
        while (isConnected) {
            Object input;
            try {
                m_socket.setSoTimeout(5000);
                input = inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                isConnected = false;
            }
        }
        try{
            pingThread.interrupt();
            pingThread.join();
            m_socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class Pinger implements Runnable{
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    outputStream.writeObject(new PingMessage());
                    Thread.sleep(1000);
                } catch (IOException e) {
                    System.err.println("Could not ping");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
