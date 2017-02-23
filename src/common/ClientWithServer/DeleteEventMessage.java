package common.ClientWithServer;

import common.Message;

import java.util.UUID;

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
