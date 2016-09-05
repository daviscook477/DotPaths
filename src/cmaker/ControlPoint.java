package cmaker;

import java.awt.Color;

public class ControlPoint extends DraggableDrawableCircle {

	private InteractiveSpline sp;
	private Point p;
	
	public ControlPoint(InteractiveSpline sp, Point p) {
		super(p.x, p.y, 10, Color.GRAY);
		this.sp = sp;
		this.p = p;
		sp.pSet.updatePoints(sp); // This goes here b/c it crashes in expansion point
		// NOTE having this here causes unnecessary computing when building the original spline
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
	
	@Override
	public void cursorAt(int cursorX, int cursorY) {
		super.cursorAt(cursorX, cursorY);
		// Modify the spline's point's locations along with this circle
		p.x = x;
		p.y = y;
		// Then modify the location of the neighboring direction point
		int index = sp.points.indexOf(p);
		DirectionPoint dp = sp.dPoints.get(index);
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
		dp.updatePosition(this);
		sp.updatePartitions(this);
	}
	
}
