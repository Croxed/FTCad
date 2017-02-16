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
	
	/**
	 * Constructs the ClientActionMessage, setting its ActionType and embedded GObject.
	 * @param actionType the ClientActionMessage.ActionType of the message 
	 * @param gObject the object to be sent to the server
	 */
	public ClientActionMessage(ActionType actionType, GObject gObject) {
		mActionType = actionType;
		mGObject = gObject;
	}
	
	/**
	 * Returns the ActionType of the message.
	 * @return
	 */
	public ActionType getActionType() {
		return mActionType;
	}
	
	/**
	 * Returns the GObject of the message.
	 * @return
	 */
	public GObject getGObject() {
		return mGObject;
	}

}
