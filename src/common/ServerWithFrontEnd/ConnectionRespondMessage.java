package common.ServerWithFrontEnd;

import common.Message;

import java.net.InetAddress;

/**
 *Respond message to server with portnumber and primary status
 */
public class ConnectionRespondMessage extends Message {
    boolean primary;
    InetAddress primaryAddress;
    int primaryPort;

    public ConnectionRespondMessage(boolean _primary, InetAddress _primaryAddress, int _primaryPort) {
        primary = _primary;
        primaryAddress = _primaryAddress;
        primaryPort = _primaryPort;
    }

    public InetAddress getPrimaryAddress() {
        return primaryAddress;
    }

    public int getPrimaryPort() {
        return primaryPort;
    }

    public boolean isPrimary() {
        return primary;
    }
}
