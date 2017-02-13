package common;

/**
 * Created by oscar on 2017-02-13.
 */
public class ClientWithFrontEndConnectionRespondMessage extends Message {
    private String address;
    private int port;
    public ClientWithFrontEndConnectionRespondMessage(String address, int port){
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }
}
