package common.ServerWithFrontEnd;

import common.Message;
 
/**
 *Requests message connection for portnumber and primary status
 */
public class ConnectionRequestMessage extends Message {
    private static final long serialVersionUID = 1;
    private int portNr;
    private boolean wasPrimary;

    public ConnectionRequestMessage(int serverPort, boolean _wasPrimary) {
        portNr = serverPort;
        wasPrimary = _wasPrimary;
    }

    public boolean wasPrimary() {
        return wasPrimary;
    }

    public int getPortNr() {
        return portNr;
    }
}
