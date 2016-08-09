package cmaker;

public interface IDraggable {

	boolean cursorInBounds(int cursorX, int cursorY);
	void cursorAt(int cursorX, int cursorY);
	void pickedUp(int cursorX, int cursorY);
	void dropped(int cursorX, int cursorY);
	
}
