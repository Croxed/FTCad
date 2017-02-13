package common;

import java.io.*;

public class PingMessage implements Message {
	public static final int typeID = 1;

	public PingMessage() {
		
	}

	@Override
	public void serializeToStream(DataOutputStream out) throws IOException {
		out.writeInt(typeID);
	}
}
