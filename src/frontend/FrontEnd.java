package frontend;

/**
 * Created by oscar on 2017-02-13.
 */
public class FrontEnd {
    public FrontEnd() {

    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: java Server portnumber");
            System.exit(-1);
        }
        try {
            // Start the server and listen for connections
            System.out.println(Integer.parseInt(args[0]));
        } catch (NumberFormatException e) {
            System.err.println("Error: Port number must be an integer." + e);
            System.exit(-1);
        }
    }
}
