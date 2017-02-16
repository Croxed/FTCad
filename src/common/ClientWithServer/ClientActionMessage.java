package common.ClientWithServer;

import DCAD.GObject;
import common.Message;

public class ClientActionMessage extends Message {

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
