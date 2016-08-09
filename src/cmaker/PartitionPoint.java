package cmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

public class PartitionPoint implements IDrawable, IDraggable {

	public static final float CIRCLE_RADIUS = 5.0f;
	public static final float DEFAULT_RADIUS = 30.0f;
	
	public Point p;
	public InteractiveSpline sp;
	public float radius;
	
	public PartitionPoint(InteractiveSpline sp, Point p) {
		this.p = p;
		this.sp = sp;
		this.radius = DEFAULT_RADIUS;
		double theta = Math.atan2(p.dy, p.dx);
		x2 = (float) (p.x - radius * Math.sin(theta));
		y2 = (float) (p.y + radius * Math.cos(theta));
	}
	
	public PartitionPoint(InteractiveSpline sp, Point p, float radius) {
		this.p = p;
		this.sp = sp;
		this.radius = radius;
		double theta = Math.atan2(p.dy, p.dx);
		x2 = (float) (p.x - radius * Math.sin(theta));
		y2 = (float) (p.y + radius * Math.cos(theta));
	}
	
	public float x2, y2;

	@Override
	public boolean cursorInBounds(int cursorX, int cursorY) {
		double theta = Math.atan2(p.dy, p.dx);
		x2 = (float) (p.x - radius * Math.sin(theta));
		y2 = (float) (p.y + radius * Math.cos(theta));
		float dx = x2 - cursorX;
		float dy = y2 - cursorY;
		if ((dx * dx + dy * dy) <= CIRCLE_RADIUS * CIRCLE_RADIUS) {
			System.out.println("in");
			return true;
		}
		return false;
	}

	@Override
	public void cursorAt(int cursorX, int cursorY) {
		float vx = cursorX - cursorStartX;
		float vy = cursorY - cursorStartY;
		double theta = Math.atan2(p.dy, p.dx);
		double dot = vx * (0 - Math.sin(theta)) + vy * Math.cos(theta);
		radius = (float) (startR + dot);
		x2 = (float) (p.x - radius * Math.sin(theta));
		y2 = (float) (p.y + radius * Math.cos(theta));
	}

	public float startR;
	public int cursorStartX, cursorStartY;

	@Override
	public void pickedUp(int cursorX, int cursorY) {
		startR = radius;
		cursorStartX = cursorX;
		cursorStartY = cursorY;
		sp.pSet.deletePoints();
	}
	
	
	@Override
	public void dropped(int cursorX, int cursorY) {
		sp.pSet.updatePoints(sp);
	}

	@Override
	public void preDraw(Graphics2D g2D) {
		
	}

	public static final Color PURPLE = new Color(160, 32, 240);
	
	@Override
	public void draw(Graphics2D g2D) {
		g2D.setColor(PURPLE);
		g2D.draw(new Line2D.Double(p.x, p.y, x2, y2));
		g2D.draw(new Line2D.Double(p.x, p.y, 2*p.x - x2, 2*p.y - y2));
		g2D.fill(new Ellipse2D.Double(x2 - CIRCLE_RADIUS, y2 - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2));
	}

	@Override
	public void postDraw(Graphics2D g2D) {
		g2D.setColor(Color.BLACK);
		g2D.setStroke(new BasicStroke(2.0f));
		g2D.fill(new Ellipse2D.Double(x2 - CIRCLE_RADIUS, y2 - CIRCLE_RADIUS, CIRCLE_RADIUS * 2, CIRCLE_RADIUS * 2));
	}

}
