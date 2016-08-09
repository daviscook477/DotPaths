package cmaker;

import java.awt.Graphics2D;

public interface IDrawable {

	void preDraw(Graphics2D g2D);
	void draw(Graphics2D g2D);
	void postDraw(Graphics2D g2D);
	
}
