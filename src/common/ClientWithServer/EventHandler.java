package common.ClientWithServer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * This Class handles all the events for the client that comes from the server
 * through a Arraylist of objects
 *
 */
public class EventHandler implements Serializable {

    private static final long serialVersionUID = 1L;
    private ArrayList<Object> events = new ArrayList<Object>();

    /**
     * Adds all the events into the array
     * @param addEvents
     */
    public void addEvents(EventHandler eh) {
        events.addAll(eh.events);
    }
    /**
     * Adds a single event into the array
     * @param addEvent
     */
    public void addEvent(Object o) {
        events.add(o);
    }
    /**
     * Updates the the Arraylist with what objects are needed to be drawn 
     * and what objects are needed to get deleted.
     * 
     * Then it returns an array of the updated arraylist
     * @return getExistingGObjects
     */
    
    public Collection<GObject> getExistingGObjects() {
        LinkedHashMap<UUID, GObject> results = new LinkedHashMap<UUID, GObject>();

        for (Object event : events) {
            if (event instanceof GObject) {
                GObject gob = (GObject) event;
                results.put(gob.getUID(), gob);
            } else if (event instanceof DeleteEventMessage) {
                results.remove(((DeleteEventMessage) event).getUID());
            }
        }

        return results.values();
    }

    public int numEvents() {
        return events.size();
    }
}
