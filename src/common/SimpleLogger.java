package common;

public class SimpleLogger {
	private static boolean lastLogWasPing = false;
	
	/**
	 * When pings are received and sent successfully it will write out a log
	 * If ! ping is sent, if . ping is received
	 */
	public static synchronized void sentPing() {
		lastLogWasPing = true;
		System.out.print("!");
	}
	
	public static synchronized void receivePing() {
		lastLogWasPing = true;
		System.out.print(".");
	}
	
	public static synchronized void print(String s) {
		if (lastLogWasPing) {
			lastLogWasPing = false;
			System.out.println("");
		}
		System.out.println(s);
	}
	
	public static synchronized void error(String s) {
		if (lastLogWasPing) {
			lastLogWasPing = false;
			System.out.println("");
		}
		System.err.println(s);
	}
}
