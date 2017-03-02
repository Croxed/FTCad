/**
 * @author brom
 */

package common.ClientWithServer;


import java.awt.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * This class represents a graphical object that is drawn in the GUI window. 
 * This class should be sent through the void to the server. 
 * @author brom
 */
public class GObject implements Serializable {

    /**
     * This is always very necessary.
     */
    private static final long serialVersionUID = 1L;
    private common.ClientWithServer.Shape s;
    private Color c;
    private int x, y, width, height;
    private UUID mUID;
    // Note that the x and y coordinates are relative to the top left corner of the
    // graphics context in which the object is to be drawn - NOT the top left corner
    // of the GUI window.

    public GObject(common.ClientWithServer.Shape s, Color c, int x, int y, int width, int height) {
        this.s = s;
        this.c = c;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.mUID = java.util.UUID.randomUUID();
    }

    public UUID getUID() {
        return mUID;
    }

    public void setCoordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public common.ClientWithServer.Shape getShape() {
        return s;
    }

    public void setShape(common.ClientWithServer.Shape s) {
        this.s = s;
    }

    public Color getColor() {
        return c;
    }

    public void setColor(Color c) {
        this.c = c;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void draw(Graphics g) {
        g.setColor(c);
        int drawX = x, drawY = y, drawWidth = width, drawHeight = height;

        // Convert coordinates and dimensions if objects are not drawn from top left corner to
        // bottom right.
        if (width < 0) {
            drawX = x + width;
            drawWidth = -width;
        }

        if (height < 0) {
            drawY = y + height;
            drawHeight = -height;
        }

        // Use string comparison to allow comparison of shapes even if the objects
        // have different nodes of origin

        if (s.toString().compareTo(common.ClientWithServer.Shape.OVAL.toString()) == 0) {
            g.drawOval(drawX, drawY, drawWidth, drawHeight);
        } else if (s.toString().compareTo(common.ClientWithServer.Shape.RECTANGLE.toString()) == 0) {
            g.drawRect(drawX, drawY, drawWidth, drawHeight);
        } else if (s.toString().compareTo(common.ClientWithServer.Shape.LINE.toString()) == 0) {
            g.drawLine(x, y, x + width, y + height);
        } else if (s.toString().compareTo(common.ClientWithServer.Shape.FILLED_RECTANGLE.toString()) == 0) {
            g.fillRect(drawX, drawY, drawWidth, drawHeight);
        } else if (s.toString().compareTo(common.ClientWithServer.Shape.FILLED_OVAL.toString()) == 0) {
            g.fillOval(drawX, drawY, drawWidth, drawHeight);
        }
    }
}
