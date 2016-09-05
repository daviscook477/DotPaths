package cmaker;

/**
 * It's a 2D point along a mathematical curve
 * Which means aside from having an x and y coordinate, it also has a dx and dy for the derivative at the point
 * @author Davis
 *
 */
public class Point {

	// these variables are public b/c this class is only intended to hold data
	public float x, y; // This point's location
	public float dx, dy; // The derivative with respect to time at this point
	
	/**
	 * Constructs a new point
	 * @param x x-coord
	 * @param y y-coord
	 * @param dx x derivative w/ respect to t
	 * @param dy y derivative w/ respect to t
	 */
	public Point (float x, float y, float dx, float dy) {
		// set properties
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
}
