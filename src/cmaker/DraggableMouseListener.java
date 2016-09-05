package cmaker;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * Mouse Listener designed to handle dragging splines around a screen
 * @author Davis
 *
 */
public class DraggableMouseListener implements MouseListener {

	private ArrayList<InteractiveSpline> splines;
	private IDraggable dragged;
	
	public DraggableMouseListener(ArrayList<InteractiveSpline> splines) {
		this.splines = splines;
	}
	
	public IDraggable getDragged() {
		return dragged;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (dragged != null) {
			dragged.dropped(x, y);
		}
		dragged = null;
	}

	// so when the mouse is pressed we need to check if it is pressed on any draggables
	// if so we need to designate the clicked draggable as being the object to be moved by
	// any following mouse movements
	@Override
	public void mousePressed(MouseEvent e) {
		// get mouse positions
		int x = e.getX();
		int y = e.getY();
		
		// loop to see what is being clicked
		dragLoop:
		for (InteractiveSpline spline : splines) { // check each of the splines
			if (spline.getVisibility()) {
				continue; // if the dots are being showed the thing is not supposed to be being edited
			}
			
			for (IDraggable draggable : spline.getDraggables()) { // check each draggable within the spline
				if (draggable.cursorInBounds(x, y)) { // when we're in bounds
					dragged = draggable; // designate this draggable as the one being dragged
					draggable.pickedUp(x, y); // tell the draggable that it was picked up
					break dragLoop;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// get mouse positions
		int x = e.getX();
		int y = e.getY();
		
		if (dragged != null) {
			dragged.dropped(x, y); // tell the draggable that it was dropped
		}
		dragged = null; // designate there being no item being dragged anymore
	}
	
}
