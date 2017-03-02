/**
 * @author brom
 */

package client;

public class Cad {

    static private GUI gui = new GUI(750, 600);

    /**
     * Constructs the Cad object. Creates connection to the server.
     * @param args the connection details from main()
     * It also starts a thread of this clients ServerConnection
     */
    private Cad(String[] args) {
        if (args.length > 1) {
            ConnectionHandler connectionHandler = new ConnectionHandler(gui, args[0], Integer.parseInt(args[1]));
            gui.setServerConnection(connectionHandler);
            new Thread(connectionHandler).start();
        } else {
            System.err.println("Need arguments <Frontend address> <Frontend port>");
            System.exit(-1);
        }
    }

    /**
     * Runs the client project.
     * @param args the connection details
     */
    public static void main(String[] args) {
        gui.addToListener();
        new Cad(args);
    }
}
