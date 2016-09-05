package cmaker;

import java.awt.Color;

public class ExpansionPoint extends DraggableDrawableCircle {

	private InteractiveSpline sp;
	private ControlPoint cp;
	
	public ExpansionPoint(InteractiveSpline sp, ControlPoint cp) {
		super(0, 0, 5, Color.BLUE);
		int index = sp.cPoints.indexOf(cp);
		this.sp = sp;
		this.cp = cp;
		Point p1 = sp.points.get(index - 1);
		Point p2 = sp.points.get(index);
		Curve c = new Curve(p1, p2);
		float[] pos = c.pointAt(0.5f);
		x = pos[0];
		y = pos[1];
	}
	
	// Update the position of the expansion point
	public void updatePosition() {
		int index = sp.cPoints.indexOf(cp);
		Point p1 = sp.points.get(index - 1);
		Point p2 = sp.points.get(index);
		Curve c = new Curve(p1, p2);
		float[] pos = c.pointAt(0.5f);
		x = pos[0];
		y = pos[1];
	}
	
	@Override
	public void pickedUp(int cursorX, int cursorY) {
		int index = sp.cPoints.indexOf(cp);
		Point p1 = sp.points.get(index - 1);
		Point p2 = sp.points.get(index);
		Curve c = new Curve(p1, p2);
		float[] deriv = c.derivativeAt(0.5f);
		sp.addPoint(new Point(x, y, deriv[0], deriv[1]), index);
		sp.ePoints.remove(this);
	}
	
}
