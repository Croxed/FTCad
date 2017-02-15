package common.ServerWithFrontEnd;

import common.Message;

/**
 * Created by oscar on 2017-02-13.
 */
public class ConnectionRespondMessage extends Message {
	boolean primary;
    public ConnectionRespondMessage(boolean _primary){
    	primary = _primary;
    }
    public boolean isPrimary() {
    	return primary;
    }
}
