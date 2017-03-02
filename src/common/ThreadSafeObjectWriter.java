package common;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ThreadSafeObjectWriter {
    ObjectOutputStream oos;

    /**
     * Makes the sent object safe for threading
     *
     * @param _oos The ObjectOutputStream to use
     */
    public ThreadSafeObjectWriter(ObjectOutputStream _oos) {
        oos = _oos;
    }

    /**
     * Write object to stream
     * @param o Object to write
     * @throws IOException if connection or write failed
     */
    public synchronized void writeObject(Object o) throws IOException {
        oos.writeObject(o);
    }
}
