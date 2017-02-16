package common.ClientWithServer;

import DCAD.GObject;
import common.Message;

/**
 * This class represents a create or delete action taken by the user, that is to be sent to the server. 
 * @author mattiasolsson
 *
 */
public class ClientActionMessage extends Message {

	/**
	 * This is always very important. 
	 */
	private static final long serialVersionUID = 1L;

	public enum ActionType {CREATE, DELETE};
	private ActionType mActionType;
	private GObject mGObject;
	
	public ClientActionMessage(ActionType actionType, GObject gObject) {
		mActionType = actionType;
		mGObject = gObject;
	}
	
	public ActionType getActionType() {
		return mActionType;
	}
	
	public GObject getGObject() {
		return mGObject;
	}

}
