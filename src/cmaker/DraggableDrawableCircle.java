package cmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class DraggableDrawableCircle implements IDraggable, IDrawable {

	public float x, y, r;
	public Color color;
	
	public DraggableDrawableCircle(float x, float y, float r, Color color) {
		this.x = x;
		this.y = y;
		this.r = r;
		this.color = color;
	}
	
	@Override
	public void preDraw(Graphics2D g2D) {
		
	}
	
	@Override
	public void draw(Graphics2D g2D) {
		g2D.setColor(color);
		g2D.fill(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));
	}

	@Override
	public void postDraw(Graphics2D g2D) {
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(2.0f));
		g2D.draw(new Ellipse2D.Double(x - r, y - r, r * 2, r * 2));
	}

	@Override
	public boolean cursorInBounds(int cursorX, int cursorY) {
		float dx = x - cursorX;
		float dy = y - cursorY;
		if ((dx * dx + dy * dy) <= r * r) {
			return true;
		}
		return false;
	}
	
	public float startX, startY;
	public int cursorStartX, cursorStartY;

	@Override
	public void pickedUp(int cursorX, int cursorY) {
		startX = x;
		startY = y;
		cursorStartX = cursorX;
		cursorStartY = cursorY;
	}

	@Override
	public void dropped(int cursorX, int cursorY) {
		
	}

	@Override
	public void cursorAt(int cursorX, int cursorY) {
		x = cursorX - cursorStartX + startX;
		y = cursorY - cursorStartY + startY;
	}
	
}
