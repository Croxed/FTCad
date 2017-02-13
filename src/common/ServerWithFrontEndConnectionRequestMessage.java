package common;

/**
 * Created by oscar on 2017-02-13.
 */
public class ServerWithFrontEndConnectionRequestMessage extends Message {
    private static final long serialVersionUID = 1;
    private int portNr;
    public ServerWithFrontEndConnectionRequestMessage(int serverPort){
        portNr = serverPort;
    }
}
