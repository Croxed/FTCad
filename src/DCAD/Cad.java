/**
 *
 * @author brom
 */

package DCAD;

public class Cad {
	
    static private GUI gui = new GUI(750, 600);
    private ServerConnection mConnection;
    
    public static void main(String[] args) {
        gui.addToListener();
        new Cad(args);
    }
    
    /**
     * Creates connection to the server.
     * @param args
     */
    private Cad(String[] args) {
    	if(args.length < 1){
    		mConnection = new ServerConnection(args[0], Integer.parseInt(args[1]));
        	new Thread(mConnection).start();
    	} else {
    		System.err.println("Need arguments <Frontend address> <Frontend port>");
    		System.exit(-1);
    	}
    }
}
