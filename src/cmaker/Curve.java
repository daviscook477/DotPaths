package cmaker;

public class Curve {

	// Note: changing the points requires that recalculate be called
	public Point p1, p2;
	
	// Coefficients of t for solving x(t) and y(t) where t is in [0, 1]
	private float ax, ay, bx, by, cx, cy, dx, dy;
	
	public Curve(Point p1, Point p2) {
		// Save points
		this.p1 = p1;
		this.p2 = p2;
		// X calculations
		ax = p2.dx + p1.dx + 2*p1.x - 2*p2.x;
		bx = 3*p2.x - 2*p1.dx - 3*p1.x - p2.dx;
		cx = p1.dx;
		dx = p1.x;
		// Y calculations
		ay = p2.dy + p1.dy + 2*p1.y - 2*p2.y;
		by = 3*p2.y - 2*p1.dy - 3*p1.y - p2.dy;
		cy = p1.dy;
		dy = p1.y;
	}
	
	/**
	 * Recalculates the coefficients used to determine the points and derivatives at specific times
	 * Used for when the start and end point are changed
	 */
	public void recalculate() {
		ax = p2.dx + p1.dx + 2*p1.x - 2*p2.x;
		bx = 3*p2.x - 2*p1.dx - 3*p1.x - p2.dx;
		cx = p1.dx;
		dx = p1.x;
		// Y calculations
		ay = p2.dy + p1.dy + 2*p1.y - 2*p2.y;
		by = 3*p2.y - 2*p1.dy - 3*p1.y - p2.dy;
		cy = p1.dy;
		dy = p1.y;
	}
	
	
	/**
	 * Determines the point at a specific time
	 * @param t time in [0, 1] represents position along curve
	 * @return the position of the point at the specified time
	 */
	public float[] pointAt(float t) {
		float xf, yf;
		xf = t*(t*(ax*t+bx)+cx)+dx; // ax*t^3+bx*t^2+cx*t+dx
		yf = t*(t*(ay*t+by)+cy)+dy;
		return new float[] {xf, yf};
	}
	
	/**
	 * Determines the derivative at a specific time
	 * @param t time in {0, 1} represents a position along the curve
	 * @return the derivative of the curve at that time
	 */
	public float[] derivativeAt(float t) {
		float xf, yf;
		xf = t*(3*ax*t+2*bx)+cx; // 3*ax*t*t+2*bx*t+cx
		yf = t*(3*ay*t+2*by)+cy;
		return new float[] {xf, yf};
	}
	
	public float speedAt(float t) {
		float[] deriv = derivativeAt(t);
		return (float) Math.sqrt(deriv[0] * deriv[0] + deriv[1] * deriv[1]); 
	}
	
}
