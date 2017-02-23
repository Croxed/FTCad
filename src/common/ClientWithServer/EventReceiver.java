package common.ClientWithServer;

import common.ClientWithServer.EventHandler;

public interface EventReceiver {
    public void addEvents(EventHandler extraEh);
}
