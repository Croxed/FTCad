package common.ClientWithServer;

import common.Message;

public class EventRequestMessage extends Message {

    private static final long serialVersionUID = 1L;

    private int currentEventCount;

    public EventRequestMessage(int _currentEventCount) {
        currentEventCount = _currentEventCount;
    }

    public int getCurrentEventCount() {
        return currentEventCount;
    }
}
