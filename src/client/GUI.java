/**
 *
 * @author brom
 */

package client;


import common.ClientWithServer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GUI extends JFrame implements
    WindowListener,ActionListener,MouseListener,MouseMotionListener, EventReceiver {

	private static final long serialVersionUID = 1L;
	JButton ovalButton = new JButton("Oval");
    JButton rectangleButton = new JButton("Rect");
    JButton lineButton = new JButton("Line");
    JButton filledOvalButton = new JButton("Filled oval");
    JButton filledRectangleButton = new JButton("Filled Rect");
    JButton redButton = new JButton("Red");
    JButton blueButton = new JButton("Blue");
    JButton greenButton = new JButton("Green");
    JButton whiteButton = new JButton("White");
    JButton pinkButton = new JButton("Pink");

    private GObject template = new GObject(common.ClientWithServer.Shape.OVAL, Color.RED, 363, 65, 25, 25);
    private GObject current = null;

    //private LinkedList<GObject> objectList = new LinkedList<GObject>();
    
    private ConnectionHandler mServerConnection;
    
    private EventHandler eh = new EventHandler();
	
    public GUI(int xpos, int ypos) {
        setSize(xpos,ypos);
    	setTitle("FTCAD");

		Container pane = getContentPane();
		pane.setBackground(Color.BLACK);
	
		pane.add(ovalButton);
		pane.add(rectangleButton);
		pane.add(lineButton);
		pane.add(filledOvalButton);
		pane.add(filledRectangleButton);
		pane.add(redButton);
		pane.add(blueButton);
		pane.add(greenButton);
		pane.add(whiteButton);
		pane.add(pinkButton);
	
		pane.setLayout(new FlowLayout());
		setVisible(true);
    }

    public void addToListener() {
		addWindowListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);

        ovalButton.addActionListener(this);
		rectangleButton.addActionListener(this);
		lineButton.addActionListener(this);
		filledOvalButton.addActionListener(this);
		filledRectangleButton.addActionListener(this);
		redButton.addActionListener(this);
		blueButton.addActionListener(this);
		greenButton.addActionListener(this);
		whiteButton.addActionListener(this);
		pinkButton.addActionListener(this);

    }

    // WindowListener methods
    public void windowActivated(WindowEvent e) {repaint();}
    public void windowClosed(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {System.exit(0);}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {repaint();}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {repaint();}

    // MouseListener methods
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1) {
		    if(e.getX() > 0 && e.getY() > 91) {
		    	current = new GObject(template.getShape(), template.getColor(), e.getX(), e.getY(), 0, 0);
		    }
		    else current = null;
		}
		repaint();
    }

    /**
     * Handles the event when the user clicks the mouse. 
     * Action is taken if it is the third mousebutton, then the object is deleted and the deletion event it sent to the server. 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
		// User clicks the right mouse button:
		// undo an operation by removing the most recently added object.
		if(e.getButton() == MouseEvent.BUTTON3) {
			
			// Get a reference to the removed object 
		    //GObject removedObject = objectList.removeLast();
		    
		    // Send the action to the server
			GObject lastElement = null;
			for (GObject el : eh.getExistingGObjects()) {
				lastElement = el;
			}
			if (lastElement != null) {
				mServerConnection.sendDeleteObject(lastElement);
			}
		}
		repaint();
    }

    /**
     * Handles the event when the user releases a mouse button. 
     * Action is taken if a "current" object is in focus, then a creation event is sent to the server. 
     */
    @Override
    public void mouseReleased(MouseEvent e) {
		if(current != null) {
			// Add a new 
		    //objectList.addLast(current);
		    mServerConnection.sendCreateObject(current);
		    current = null;
		}
		repaint();
    }

    // MouseMotionListener methods
    public void mouseMoved(MouseEvent e) {}

    public void mouseDragged(MouseEvent e) {
		if(current != null && e.getX() > 0 && e.getY() > 91) {
		    current.setDimensions(e.getX() - current.getX(), e.getY() - current.getY());
		}
		repaint();
    }

    // ActionListener methods
    public void actionPerformed(ActionEvent e) {
		if(e.getSource() == ovalButton) {
		    template.setShape(common.ClientWithServer.Shape.OVAL);
		}
		else if(e.getSource() == rectangleButton) {
		    template.setShape(common.ClientWithServer.Shape.RECTANGLE);
		}
		else if(e.getSource() == lineButton) {
		    template.setShape(common.ClientWithServer.Shape.LINE);
		}
		else if(e.getSource() == filledOvalButton) {
		    template.setShape(common.ClientWithServer.Shape.FILLED_OVAL);
		}
		else if(e.getSource() == filledRectangleButton) {
		    template.setShape(common.ClientWithServer.Shape.FILLED_RECTANGLE);
		}
		else if(e.getSource() == redButton) {
		    template.setColor(Color.RED);
		}
		else if(e.getSource() == blueButton) {
		    template.setColor(Color.BLUE);
		}
		else if(e.getSource() == greenButton) {
		    template.setColor(Color.GREEN);
		}
		else if(e.getSource() == whiteButton) {
		    template.setColor(Color.WHITE);
		}
		else if(e.getSource() == pinkButton) {
		    template.setColor(Color.PINK);
		}
		repaint();
    }

    public void update(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 62, getSize().width, getSize().height - 62);
	
		template.draw(g);
		
		for (GObject obj : eh.getExistingGObjects()) {
			obj.draw(g);
		}
	
		//for(ListIterator<GObject> itr = objectList.listIterator(); itr.hasNext();) {
		//	    itr.next().draw(g);
		//}
		
		if(current != null) {
		    current.draw(g);
		}
    }
	
	public void paint(Graphics g) {
    	BufferedImage bf = new BufferedImage( this.getWidth(),this.getHeight(), BufferedImage.TYPE_INT_RGB);
		super.paint(bf.getGraphics()); // The superclass (JFrame) paint function draws the GUI components.
		update(bf.getGraphics());
		g.drawImage(bf,0,0,null);
    }
	
	public void setServerConnection(ConnectionHandler serverConnection) {
		mServerConnection = serverConnection;
	}
	
	/**
	 * Add a GObject Shape to the GUI.
	 * @param gObject
	 */
	
	public synchronized void addEvents(EventHandler extraEh) {
		eh.addEvents(extraEh);
		repaint();
	}

	public synchronized void removeEvents() {
		eh = new EventHandler();
		repaint();
	}
}
