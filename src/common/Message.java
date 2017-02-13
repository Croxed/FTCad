package common;

import java.io.*;

public interface Message {
	public void serializeToStream(DataOutputStream out) throws IOException;
}
