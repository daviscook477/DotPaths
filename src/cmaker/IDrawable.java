package cmaker;

import java.awt.Graphics2D;

/**
 * Drawable item done with 3 steps
 * @author Davis
 *
 */
public interface IDrawable {

	void preDraw(Graphics2D g2D); // done first
	void draw(Graphics2D g2D); // done next
	void postDraw(Graphics2D g2D); // done last
	
}
