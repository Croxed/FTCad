package common.ServerWithFrontEnd;

import common.Message;

public class ConnectionRequestMessage extends Message {
    private static final long serialVersionUID = 1;
    private int portNr;
    public ConnectionRequestMessage(int serverPort){
        portNr = serverPort;
    }

    public int getPortNr() {
        return portNr;
    }
}
