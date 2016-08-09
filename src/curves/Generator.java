package curves;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

// This class generates a path to populate
public class Generator {

	// Base spline
	
	private Spline s;
	
	public static final int NUM_POINTS = 3;
	public static final float SPACING_FACTOR_MIN = 0.33f;
	public static final float SPACING_FACTOR_MAX = 0.55f;
	public static final float DERIVATIVE_MIN = 150.0f;
	public static final float DERIVATIVE_MAX = 200.0f;
	public static final float ANGLE_DELTA_POINT = 60; // Change in angle per point in degrees
	public static final float ANGLE_DELTA_DERIVATIVE = 60; // Change in derivative angle per point in degrees
	
	// Partitions
	
	private Point[] partitions;
	
	public static final int PARTITIONS = 6;
	
	// Splits
	
	private ArrayList<Spline> splits;
	
	public static final float SPLIT_CHANCE = 0.33f;
	public static final float SPLIT_SCALE = 0.5f;
	public static final int NUM_POINTS_SPLIT = 3;
	
	public static float dist(float x, float y, Point p) {
		float dx = x - p.x;
		float dy = y - p.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}
	
	public static final Random random = new Random();

	public Point generatePoint(int width, int height, Spline spline, float scale) {
		if (spline == null) { // If no points already exist just find a random one
			float x = random.nextFloat() * width();
			float y = random.nextFloat() * height();
			float theta = (float) (random.nextFloat() * Math.PI * 2);
			float r = random.nextFloat() * (DERIVATIVE_MAX - DERIVATIVE_MIN) + DERIVATIVE_MIN;
			float dx = (float) (Math.cos(theta) * r);
			float dy = (float) (Math.sin(theta) * r);
			return new Point(x, y, dx, dy);
		}
		int max = Math.max(width, height);
		boolean done = false;
		float r = 0;
		while (!done) {
			r = ((float) Math.sqrt(random.nextFloat()));
			if (r > SPACING_FACTOR_MIN && r < SPACING_FACTOR_MAX) {
				done = true;
			}
			r *= max;
		}
		// Scale r
		r *= scale;
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
		float dx = (float) (Math.cos(thetaRad) * r) * scale;
		float dy = (float) (Math.sin(thetaRad) * r) * scale;
		return new Point(x, y, dx, dy);
	}
	
	// Builds a spline using width and height constraints
	public Spline buildSpline(int width, int height, float scale, int numPoints) {
		return buildSpline(width, height, generatePoint(width, height, null, scale), scale, numPoints);
	}
	
	// Build a spline starting at a specific points with width and height constraints
	public Spline buildSpline(int width, int height, Point start, float scale, int numPoints) {
		Spline spline = new Spline();
		spline.addPoint(start);
		// Create three points within the rectangle that are reasonably spaced
		for (int i = 1; i < numPoints; i++) {
			spline.addPoint(generatePoint(width, height, spline, scale));
		}
		return spline;
	}
	
	private int xmod, ymod;
	
	public Generator() {
		// Create basic spline
		s = buildSpline(width(), height(), 1.0f, NUM_POINTS);
		// Make partitions
		/*partitions = new Point[PARTITIONS + 1]; // Make partitions only where points don't already exist
		float dt = ((float) NUM_POINTS - 1) / PARTITIONS;
		float t = 0.0f;
		for (int i = 0; i < partitions.length; i++) {
			int curveNum = (int) t;
			Curve c;
			if (curveNum >= NUM_POINTS - 1) {
				c = s.connects.get(s.connects.size() - 1);
				curveNum--;
			} else {
				c = s.connects.get(curveNum);
			}
			float[] pos = c.pointAt(t - curveNum);
			float[] deriv = c.derivativeAt(t - curveNum);
			partitions[i] = new Point(pos[0], pos[1], deriv[0], deriv[1]);
			t += dt;
		}*/
		// Determine where to make splits
		float t = 0.0f;
		splits = new ArrayList<Spline>((int) (PARTITIONS * SPLIT_CHANCE));
		for (int i = 0; i < PARTITIONS; i++) { // Check each partition
			// If we're making a split here
			if (random.nextFloat() <= SPLIT_CHANCE) {
				t = ((float) i) / PARTITIONS * (NUM_POINTS - 1); // I'm reusing a variable - sorry
				int curveNum = (int) t;
				Curve c;
				if (curveNum >= NUM_POINTS - 1) {
					c = s.connects.get(s.connects.size() - 1);
				} else {
					c = s.connects.get(curveNum);
				}
				System.out.println(t - curveNum + (0.5f / PARTITIONS * NUM_POINTS));
				float[] pos = c.pointAt(t - curveNum + (0.5f / PARTITIONS * NUM_POINTS));
				float[] deriv = c.derivativeAt(t - curveNum + (0.5f / PARTITIONS * NUM_POINTS));
				if (random.nextBoolean()) {
					splits.add(buildSpline(width(), height(), new Point(pos[0], pos[1], 0 - deriv[1], deriv[0]), SPLIT_SCALE, NUM_POINTS_SPLIT));
				} else {
					splits.add(buildSpline(width(), height(), new Point(pos[0], pos[1], deriv[1], 0 - deriv[0]), SPLIT_SCALE, NUM_POINTS_SPLIT));
				}
			}
		}
		// Determine minimum and maximum x and y coordinates to center the image
		float minx = (width()*3/2) * 2, miny = (height()*3/2) * 2, maxx = 0 - (width()*3/2), maxy = 0 - (height()*3/2);
		for (Point p : s.points) {
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
		for (Spline spline : splits) {
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
		xmod = (int) (((width()*3/2) - maxx - minx) / 2);
		ymod = (int) (((height()*3/2) - maxy - miny) / 2);
		// Populate the area surrounding the spline with points
	}
	
	public ArrayList<Circle> populate(int numCircles) {
		// Populate the base curve
		ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>(numCircles);
		
		
		return null;
	}
	
	public int width() {
		return 600;
	}

	public int height() {
		return 600;
	}

	public static final float DS = 2; // 2px change in length per line segment drawn
	public static final float CIRCLE_SIZE = 5; // 5px radius circles
	public static final float ARROW_SCALE = 0.1f; // arrows are 5 times smaller than derivative
	
	public void drawCurves(Spline spline, Graphics2D g2D, int xmod, int ymod, Color cColor, Color pColor) {
		for (Curve c : spline.connects) {
			boolean done = false;
			float t = 0.0f;
			float[] lastPos = c.pointAt(t);
			while (!done) {
				if (t >= 1.0f) {
					done = true;
					continue;
				}
				t += DS / (c.speedAt(t));
				float[] pos = c.pointAt(t);
				Point2D.Float lastPosP, posP;
				lastPosP = new Point2D.Float(lastPos[0]+xmod, lastPos[1]+ymod);
				posP = new Point2D.Float(pos[0]+xmod, pos[1]+ymod);
				g2D.setColor(cColor);
				g2D.draw(new Line2D.Float(lastPosP, posP));
				lastPos = pos;
			}
			// Draw points and derivatives
			Point p1 = c.p1;
			g2D.setColor(pColor);
			g2D.fill(new Ellipse2D.Float(p1.x - CIRCLE_SIZE+xmod, p1.y - CIRCLE_SIZE+ymod, 2 * CIRCLE_SIZE, 2 * CIRCLE_SIZE));
			Point p2 = c.p2;
			g2D.setColor(pColor);
			g2D.fill(new Ellipse2D.Float(p2.x - CIRCLE_SIZE+xmod, p2.y - CIRCLE_SIZE+ymod, 2 * CIRCLE_SIZE, 2 * CIRCLE_SIZE));
			g2D.setColor(Color.RED);
			g2D.draw(new Line2D.Float(new Point2D.Float(p1.x+xmod, p1.y+ymod), new Point2D.Float(p1.x+xmod + p1.dx * ARROW_SCALE, p1.y+ymod + p1.dy * ARROW_SCALE)));
			g2D.fill(new Ellipse2D.Float(p1.x+xmod + p1.dx * ARROW_SCALE - CIRCLE_SIZE, p1.y+ymod + p1.dy * ARROW_SCALE - CIRCLE_SIZE, 2 * CIRCLE_SIZE, 2 * CIRCLE_SIZE));
			g2D.draw(new Line2D.Float(new Point2D.Float(p1.x+xmod, p1.y+ymod), new Point2D.Float(p1.x+xmod + p1.dy * ARROW_SCALE, p1.y+ymod - p1.dx * ARROW_SCALE)));
			g2D.setColor(new Color(160, 32, 240)); // Purple
			g2D.draw(new Line2D.Float(new Point2D.Float(p1.x+xmod - p1.dy * ARROW_SCALE * 2, p1.y+ymod + p1.dx * ARROW_SCALE * 2), new Point2D.Float(p1.x+xmod, p1.y+ymod)));
			g2D.fill(new Ellipse2D.Float(p1.x+xmod - p1.dy * ARROW_SCALE * 2 - CIRCLE_SIZE, p1.y+ymod + p1.dx * ARROW_SCALE * 2 - CIRCLE_SIZE, 2 * CIRCLE_SIZE, 2 * CIRCLE_SIZE));
		}
	}
	
	public void draw(Graphics2D g2D) {
		// Draw a box
		g2D.drawRect(0, 0, (width()*3/2), (height()*3/2));
		// Draw partitions
		/*for (int i = 1; i < partitions.length - 1; i++) {
			Point p = partitions[i];
			g2D.setColor(Color.GRAY);
			g2D.fill(new Ellipse2D.Float(p.x+xmod - CIRCLE_SIZE, p.y+ymod - CIRCLE_SIZE, 2 * CIRCLE_SIZE, 2 * CIRCLE_SIZE));
			g2D.setColor(Color.RED);
			g2D.draw(new Line2D.Float(new Point2D.Float(p.x+xmod, p.y+ymod), new Point2D.Float(p.x+xmod + p.dx * ARROW_SCALE, p.y+ymod + p.dy * ARROW_SCALE)));
			g2D.fill(new Ellipse2D.Float(p.x+xmod + p.dx * ARROW_SCALE - CIRCLE_SIZE, p.y+ymod + p.dy * ARROW_SCALE - CIRCLE_SIZE, 2 * CIRCLE_SIZE, 2 * CIRCLE_SIZE));
			g2D.draw(new Line2D.Float(new Point2D.Float(p.x+xmod, p.y+ymod), new Point2D.Float(p.x+xmod + p.dy * ARROW_SCALE, p.y+ymod - p.dx * ARROW_SCALE)));
			g2D.setColor(new Color(160, 32, 240)); // Purple
			g2D.draw(new Line2D.Float(new Point2D.Float(p.x+xmod - p.dy * ARROW_SCALE * 2, p.y+ymod + p.dx * ARROW_SCALE * 2), new Point2D.Float(p.x+xmod, p.y+ymod)));
			g2D.fill(new Ellipse2D.Float(p.x+xmod - p.dy * ARROW_SCALE * 2 - CIRCLE_SIZE, p.y+ymod + p.dx * ARROW_SCALE * 2 - CIRCLE_SIZE, 2 * CIRCLE_SIZE, 2 * CIRCLE_SIZE));
		}*/
		// Draw base spline
		drawCurves(s, g2D, xmod, ymod, Color.black, Color.gray);
		// Draw splits
		for (Spline spline : splits) {
			drawCurves(spline, g2D, xmod, ymod, Color.green, Color.blue);
		}
	}

}
