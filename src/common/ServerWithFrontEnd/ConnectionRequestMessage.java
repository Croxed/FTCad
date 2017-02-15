package common.ServerWithFrontEnd;

import common.Message;

/**
 * Created by oscar on 2017-02-13.
 */
public class ConnectionRequestMessage extends Message {
    private static final long serialVersionUID = 1;
    private int portNr;
    public ConnectionRequestMessage(int serverPort){
        portNr = serverPort;
    }
}
