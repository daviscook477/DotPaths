package curves;

public class Point {

	public float x, y; // This point's location
	public float dx, dy; // The derivative with respect to time at this point
	
	public Point (float x, float y, float dx, float dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
}
