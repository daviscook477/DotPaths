package cmaker;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

// wtf does this do
public class SplineGenerator {

	public static final Random random = new Random();

	public static final float SPACING_MIN = 90.0f;
	public static final float SPACING_MAX = 230.0f;
	public static final float DERIVATIVE_MIN = 150.0f;
	public static final float DERIVATIVE_MAX = 200.0f;
	public static final float ANGLE_DELTA_POINT = 30; // Change in angle per point in degrees
	public static final float ANGLE_DELTA_DERIVATIVE = 30; // Change in derivative angle per point in degrees 
	
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
	
	// Build a spline starting at a specific points with width and height constraints
	public static InteractiveSpline buildSpline(int width, int height, Point start, float scale, int numPoints, boolean center) {
		InteractiveSpline spline = new InteractiveSpline();
		spline.addPoint(start);
		// Create three points within the rectangle that are reasonably spaced
		for (int i = 1; i < numPoints; i++) {
			spline.addPoint(generatePoint(width, height, spline, scale));
		}
		// Modify the points such that they nicely fit within bounds
		float minx = (width*3/2) * 2, miny = (height*3/2) * 2, maxx = 0 - (width*3/2), maxy = 0 - (height*3/2);
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
		// This isn't used ATM because I have no splits existing
		/*for (InteractiveSpline spline : splits) {
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
		}*/
		int xmod, ymod;
		if (center) {
			xmod = (int) ((0 - minx) + (width - (maxx - minx)) / 2);
			ymod = (int) ((0 - miny) + (height - (maxy - miny)) / 2);
		} else {
			xmod = 0;
			ymod = 0;
		}
		InteractiveSpline newSpline = new InteractiveSpline();
		for (Point p : spline.points) {
			newSpline.addPoint(new Point (p.x+xmod, p.y+ymod, p.dx, p.dy));
		}
		newSpline.pSet.updatePoints(newSpline);
		return newSpline;
	}
	
	public static ArrayList<InteractiveSpline> buildComplexSpline(int width, int height, float scale, int numPoints, int numSplits, float splitScale, boolean center) {
		InteractiveSpline baseSpline = buildSpline(width, height, generatePoint(width, height, null, scale), 1.0f, numPoints, false);
		ArrayList<InteractiveSpline> splits = new ArrayList<InteractiveSpline>(numSplits);
		float[] splitPos = new float[numSplits];
		float minSeparation = 1.0f / numSplits / 2;
		// Get some reasonably spread out values of t in [0, 1] for where the splits will occur
		for (int i = 0; i < numSplits; i++) {
			float t;
			boolean doneT = false;
			while (!doneT) {
				t = random.nextFloat();
				boolean good = true;
				for (int j = 0; j < i; j++) {
					if (Math.abs(t - splitPos[j]) < minSeparation) {
						good = false;
					}
				}
				if (good) {
					splitPos[i] = t;
					doneT = true;
				}
			}
		}
		// Spread the range out to cover the indices of the control point array on the base spline
		float[] splitPosSpread = new float[numSplits];
		for (int i = 0; i < numSplits; i++) {
			splitPosSpread[i] = splitPos[i] * (numPoints - 1);
		}
		// Create the split splines
		for (int i = 0; i < numSplits; i++) {
			int curveNum = (int) splitPosSpread[i]; // Truncate the value of t to get what curve we're looking at
			Point p1 = baseSpline.points.get(curveNum);
			Point p2 = baseSpline.points.get(curveNum + 1);
			Curve c = new Curve(p1, p2);
			float tRanged = splitPosSpread[i] - curveNum; // Put t back in the range [0, 1)
			float[] pos = c.pointAt(tRanged);
			float[] deriv = c.derivativeAt(tRanged);
			Point start;
			if (random.nextBoolean()) {
				start = new Point(pos[0], pos[1], 0 - deriv[1], deriv[0]);
			} else {
				start = new Point(pos[0], pos[1], deriv[1], 0 - deriv[0]);
			}
			splits.add(buildSpline(width, height, start, splitScale, (int) (numPoints * splitScale), false));
		}
		// Determine the max and the min across all of the things
		splits.add(0, baseSpline);
		float minx = (width*3/2) * 2, miny = (height*3/2) * 2, maxx = 0 - (width*3/2), maxy = 0 - (height*3/2);
		for (InteractiveSpline spline : splits) {
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
		}
		int xmod, ymod;
		if (center) {
			xmod = (int) ((0 - minx) + (width - (maxx - minx)) / 2);
			ymod = (int) ((0 - miny) + (height - (maxy - miny)) / 2);
		} else {
			xmod = 0;
			ymod = 0;
		}
		Color baseColor = Application.randColor();
		Color secondaryColor = Application.randColor();
		ArrayList<InteractiveSpline> newSplits = new ArrayList<InteractiveSpline>(splits.size());
		for (InteractiveSpline is : splits) {
			InteractiveSpline newSpline = new InteractiveSpline();
			for (Point p : is.points) {
				newSpline.addPoint(new Point (p.x+xmod, p.y+ymod, p.dx, p.dy));
			}
			newSpline.pSet.baseColor = baseColor;
			newSpline.pSet.secondaryColor = secondaryColor;
			newSpline.pSet.updatePoints(newSpline);
			newSplits.add(newSpline);
		}
		return newSplits;
	}
	
}
