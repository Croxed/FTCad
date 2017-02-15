package common.ClientWithFrontEnd;

import common.Message;

import java.net.InetAddress;

/**
 * Created by oscar on 2017-02-13.
 */
public class ConnectionRespondMessage extends Message {
    private InetAddress address;
    private int port;
    public ConnectionRespondMessage(InetAddress address, int port){
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
