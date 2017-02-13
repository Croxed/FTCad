package common;

import java.net.InetAddress;

/**
 * Created by oscar on 2017-02-13.
 */
public class ClientWithFrontEndConnectionRespondMessage extends Message {
    private InetAddress address;
    private int port;
    public ClientWithFrontEndConnectionRespondMessage(InetAddress address, int port){
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
