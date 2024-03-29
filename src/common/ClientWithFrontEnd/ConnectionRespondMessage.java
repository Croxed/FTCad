package common.ClientWithFrontEnd;

import common.Message;

import java.net.InetAddress;

/**
 * Class for ConnectionRespondMessage from FrontEnd to Client
 */
public class ConnectionRespondMessage extends Message {
    private InetAddress address;
    private int port;

    public ConnectionRespondMessage(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
