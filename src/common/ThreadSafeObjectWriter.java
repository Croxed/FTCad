package common;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ThreadSafeObjectWriter {
	ObjectOutputStream oos;
/** 
 * Makes the sent object safe for threading
 * @param _oos
 */
	public ThreadSafeObjectWriter(ObjectOutputStream _oos) {
		oos = _oos;
	}
	
	public synchronized void writeObject(Object o) throws IOException {
		oos.writeObject(o);
	}
}
