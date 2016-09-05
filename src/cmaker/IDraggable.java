package cmaker;

/**
 * Draggable item with functions called based on what's happening during dragging
 * @author Davis
 *
 */
public interface IDraggable {

	/**
	 * Called to check if cursor is in bounds
	 * @param cursorX cursor x-coord
	 * @param cursorY cursor y-coord
	 * @return if the cursor is within this item
	 */
	boolean cursorInBounds(int cursorX, int cursorY);
	
	/**
	 * Called for when an item is being dragged
	 * @param cursorX the x-coord of the mouse
	 * @param cursorY the y-coord of the mouse
	 */
	void cursorAt(int cursorX, int cursorY);
	
	/**
	 * Called for when an item is picked up
	 * @param cursorX the x-coord of the mouse
	 * @param cursorY the y-coord of the mouse
	 */
	void pickedUp(int cursorX, int cursorY);
	
	/**
	 * Called for when the item is dropped down
	 * @param cursorX the x-coord of the mouse
	 * @param cursorY the y-coord of the mouse
	 */
	void dropped(int cursorX, int cursorY);
	
}
