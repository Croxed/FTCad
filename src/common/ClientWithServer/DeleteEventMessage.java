package common.ClientWithServer;

import common.Message;

import java.util.UUID;

/**
 * This Class handles the DeleteEventMessage which sets the requested objects UUID that will be removed
 */
public class DeleteEventMessage extends Message {
    private static final long serialVersionUID = 1L;

    private UUID mUID;

    public DeleteEventMessage(UUID uid) {
        mUID = uid;
    }

    public UUID getUID() {
        return mUID;
    }
}
