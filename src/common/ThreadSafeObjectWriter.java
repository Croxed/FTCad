package common;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ThreadSafeObjectWriter {
	ObjectOutputStream oos;
	
	public ThreadSafeObjectWriter(ObjectOutputStream _oos) {
		oos = _oos;
	}
	
	public synchronized void writeObject(Object o) throws IOException {
		oos.writeObject(o);
	}
}
