package common;

import java.io.IOException;

public class Pingu implements Runnable {
	private volatile boolean shutdown = false;
	private ThreadSafeObjectWriter tsow; 
	
	public void shutdown() {
		shutdown = true;
	}
	
	public Pingu(ThreadSafeObjectWriter _tsow) {
		tsow = _tsow;
	}

	@Override
	public void run() {
        while (!shutdown) {
            try {
                tsow.writeObject(new PingMessage());
                System.out.print("!");
            } catch (IOException e) {
                // Error when pinging, the connection is probably down, Pingu don't care
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Error when sleeping, thread was probably interrupted
            }
        }
    }

}
