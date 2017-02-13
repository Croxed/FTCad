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
        new Cad();
    }
    
    private Cad() {
    	mConnection = new ServerConnection("localhost", 1325);
    	new Thread(mConnection).start();
    }
}
