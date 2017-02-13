package common;

import java.io.*;

public class MessageFactory {
	public static Message getMessage(DataInputStream di) throws IOException {
		int type = di.readInt();
		if (type == PingMessage.typeID) {
			return new PingMessage();
		} else {
			throw new IOException();
		}
	}
}
