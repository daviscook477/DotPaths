package cmaker;

import java.util.Random;

/**
 * Randomly generates splines using some weird method
 * @author Davis
 *
 */
public class SplineGenerator {

	// the random number generator
	private static final Random random = new Random();

	// the parameters that define how the generator works
	public static final float SPACING_MIN = 90.0f;
	public static final float SPACING_MAX = 230.0f;
	public static final float DERIVATIVE_MIN = 150.0f;
	public static final float DERIVATIVE_MAX = 200.0f;
	public static final float ANGLE_DELTA_POINT = 30; // Change in angle per point in degrees
	public static final float ANGLE_DELTA_DERIVATIVE = 30; // Change in derivative angle per point in degrees 
	
	// generates a new point to append to an existing spline
	public static Point generatePoint(int width, int height, InteractiveSpline spline, float scale) {
		if (spline == null) { // If no points already exist just find a random one
			float x = random.nextFloat() * width;
			float y = random.nextFloat() * height;
			float theta = (float) (random.nextFloat() * Math.PI * 2);
			float r = random.nextFloat() * (DERIVATIVE_MAX - DERIVATIVE_MIN) + DERIVATIVE_MIN;
			float dx = (float) (Math.cos(theta) * r);
			float dy = (float) (Math.sin(theta) * r);
			return new Point(x, y, dx, dy);
		}
		boolean done = false;
		float r = 0;
		while (!done) {
			r = ((float) Math.sqrt(random.nextFloat()));
			if (r > SPACING_MIN / SPACING_MAX) {
				done = true;
			}
			r *= SPACING_MAX;
		}
		// Scale r
		//r *= scale;
		// Generate the point at an angle within the allowed delta
		Point last = spline.points.get(spline.points.size() - 1);
		float theta = (random.nextFloat() * 2 - 1) * ANGLE_DELTA_POINT;
		float thetaRad = (float) ((theta * Math.PI / 180) + Math.atan2(last.dy, last.dx));
		float x = (float) (Math.cos(thetaRad) * r + last.x);
		float y = (float) (Math.sin(thetaRad) * r + last.y);
		// Generate a derivative at the point
		theta = (random.nextFloat() * 2 - 1) * ANGLE_DELTA_DERIVATIVE;
		thetaRad = (float) ((theta * Math.PI / 180) + Math.atan2(last.dy, last.dx));
		r = random.nextFloat() * (DERIVATIVE_MAX - DERIVATIVE_MIN) + DERIVATIVE_MIN;
		float dx = (float) (Math.cos(thetaRad) * r);
		float dy = (float) (Math.sin(thetaRad) * r);
		return new Point(x, y, dx, dy);
	}
	
	// Builds a spline using width and height constraints
	public static InteractiveSpline buildSpline(int width, int height, float scale, int numPoints, boolean center) {
		return buildSpline(width, height, generatePoint(width, height, null, scale), scale, numPoints, center);
	}
	
	// Creates a new spline centered in the middle of the window based on the provided spline
	private static InteractiveSpline splineCenteredInTheMiddleOfTheWindow(int width, int height, InteractiveSpline spline) {
		// set default min and max coordinates that nicely center it if there aren't any really big outliers in the actual data set
		float minx = (width*3/2) * 2, miny = (height*3/2) * 2, maxx = 0 - (width*3/2), maxy = 0 - (height*3/2);
		// determine the min and max coordinates of the points in the splines
		for (Point p : spline.points) {
			if (p.x < minx) {
				minx = p.x;
			}
			if (p.x > maxx) {
				maxx = p.x;
			}
			if (p.y < miny) {
				miny = p.y;
			}
			if (p.y > maxy) {
				maxy = p.y;
			}
		}
		int xmod, ymod; // values by which to modify the spline's points in the x and y directions
		xmod = (int) ((0 - minx) + (width - (maxx - minx)) / 2);
		ymod = (int) ((0 - miny) + (height - (maxy - miny)) / 2);
		InteractiveSpline newSpline = new InteractiveSpline();
		for (Point p : spline.points) { // modify all the points
			newSpline.addPoint(new Point (p.x+xmod, p.y+ymod, p.dx, p.dy));
		}
		newSpline.pSet.updatePoints(newSpline);
		return newSpline;
	}
	
	// Build a spline starting at a specific points with width and height constraints
	public static InteractiveSpline buildSpline(int width, int height, Point start, float scale, int numPoints, boolean center) {
		InteractiveSpline spline = new InteractiveSpline();
		spline.addPoint(start);
		// Create three points within the rectangle that are reasonably spaced
		for (int i = 1; i < numPoints; i++) {
			spline.addPoint(generatePoint(width, height, spline, scale));
		}
	
		if (center) { // if the spline should be centered, center it
			return splineCenteredInTheMiddleOfTheWindow(width, height, spline);
		}
		return spline;
	}
	
}
