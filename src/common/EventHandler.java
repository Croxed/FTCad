package common;

import DCAD.GObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.UUID;

public class EventHandler implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ArrayList<Object> events = new ArrayList<Object>();

    public void addEvents(EventHandler eh) {
        events.addAll(eh.events);
    }

    public void addEvent(Object o) {
        events.add(o);
    }

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
