package cmaker;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class DirectionPoint extends Circle {

	private InteractiveSpline sp;
	private ControlPoint cp;
	
	public DirectionPoint(InteractiveSpline sp, ControlPoint cp) {
		super(0, 0, 5, Color.RED);
		Point p1 = sp.points.get(sp.cPoints.indexOf(cp));
		this.x = p1.x + p1.dx * InteractiveSpline.ARROW_SCALE;
		this.y = p1.y + p1.dy * InteractiveSpline.ARROW_SCALE;
		this.sp = sp;
		this.cp = cp;
	}
	
	// Update the position of the direction point using the control point
	public void updatePosition(ControlPoint cp) {
		Point p = sp.points.get(sp.cPoints.indexOf(cp));
		x = cp.x + p.dx / DERIVATIVE_SCALE;
		y = cp.y + p.dy / DERIVATIVE_SCALE;
	}
	
	@Override
	public void draw(Graphics2D g2D) {
		super.draw(g2D);
		Point p1 = sp.points.get(sp.cPoints.indexOf(cp));
		g2D.draw(new Line2D.Float(new Point2D.Float(p1.x, p1.y), new Point2D.Float(x, y)));
	}
	
	@Override
	public void pickedUp(int cursorX, int cursorY) {
		super.pickedUp(cursorX, cursorY);
		sp.pSet.deletePoints();
	}
	
	@Override
	public void dropped(int cursorX, int cursorY) {
		super.dropped(cursorX, cursorY);
		sp.pSet.updatePoints(sp);
	}
	
	public static float DERIVATIVE_SCALE = 10.0f;
	
	@Override
	public void cursorAt(int cursorX, int cursorY) {
		super.cursorAt(cursorX, cursorY);
		// Determine the current magnitude of the derivative
		Point p = sp.points.get(sp.cPoints.indexOf(cp));
		// Determine the displacement of the cursor with respect to the point
		float dx = cursorX - cursorStartX + startX - p.x;
		float dy = cursorY - cursorStartY + startY - p.y;
		// Set the new derivatives
		float dxNew = dx * DERIVATIVE_SCALE;
		float dyNew = dy * DERIVATIVE_SCALE;
		p.dx = dxNew;
		p.dy = dyNew;
		sp.updatePartitions(this);
		int index = sp.points.indexOf(p);
		if (index < 1) {
			ExpansionPoint ep1 = sp.ePoints.get(index);
			ep1.updatePosition();
		} else if (index == sp.points.size() - 1) {
			ExpansionPoint ep0 = sp.ePoints.get(index - 1);
			ep0.updatePosition();
		} else {
			ExpansionPoint ep0 = sp.ePoints.get(index - 1);
			ExpansionPoint ep1 = sp.ePoints.get(index);
			ep0.updatePosition();
			ep1.updatePosition();
		}
	}

}
