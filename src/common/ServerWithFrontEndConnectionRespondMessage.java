package common;

/**
 * Created by oscar on 2017-02-13.
 */
public class ServerWithFrontEndConnectionRespondMessage extends Message {
	boolean primary;
    public ServerWithFrontEndConnectionRespondMessage(boolean _primary){
    	primary = _primary;
    }
    public boolean isPrimary() {
    	return primary;
    }
}
