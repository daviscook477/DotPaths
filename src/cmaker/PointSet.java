package cmaker;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Random;

public class PointSet {

	public PointSet() {
		points = new ArrayList<Point2D.Double>();
		circles = new ArrayList<SimpleCircle>();
	}
	
	public static final float POINTS_PER_AREA =  3.0f / 160;
	private static final Random random = new Random();
	
	public ArrayList<Point2D.Double> points;
	public ArrayList<SimpleCircle> circles;
	
	public void deletePoints() {
		points = new ArrayList<Point2D.Double>();
		circles = new ArrayList<SimpleCircle>();
	}
	
	public static final int MIN_RADIUS = 5;
	public static final int MAX_RADIUS = 35;
	
	public static final int NUM_COLORS = 20;
	public static final int MAX_PASSES = 10;
	
	public Color baseColor = Application.randColor();
	public Color secondaryColor = Application.randColor();
	
	public static float dist(Point2D.Double p, SimpleCircle c) {
		return (float) Math.sqrt(((p.x - c.x) * (p.x - c.x)) + ((p.y - c.y) * (p.y - c.y)));
	}
	
	private double polygonArea(Polygon pg) {
		double sum = 0.0;
        for (int i = 0; i < pg.npoints; i++) {
            sum = sum + (pg.xpoints[i] * pg.ypoints[i+1>=pg.npoints?0:i+1]) - (pg.ypoints[i] * pg.xpoints[i+1>=pg.npoints?0:i+1]);
        }
        return 0.5 * sum;
	}
	
	public void updatePoints(InteractiveSpline sp) {
		// Create the points
		ArrayList<PartitionPoint> pPoints = sp.pPoints;
		points = new ArrayList<Point2D.Double>();
		if (pPoints.size() >= 1) {
			PartitionPoint last = pPoints.get(0);
			for (int i = 1; i < pPoints.size(); i++) {
				PartitionPoint current = pPoints.get(i);
				Polygon pg = new Polygon();
				pg.addPoint((int) last.x2, (int) last.y2);
				pg.addPoint((int) (2*last.p.x - last.x2), (int) (2*last.p.y - last.y2));
				pg.addPoint((int) (2*current.p.x - current.x2), (int) (2*current.p.y - current.y2));
				pg.addPoint((int) (current.x2), (int) (current.y2));
				Rectangle2D bounds = pg.getBounds2D();
				double area = polygonArea(pg);
				int numPoints = (int) (POINTS_PER_AREA * area);
				boolean done = false;
				int pointsFinished = 0;
				while (!done) {
					double x = random.nextFloat() * bounds.getWidth() + bounds.getX();
					double y = random.nextFloat() * bounds.getHeight() + bounds.getY();
					Point2D.Double p = new Point2D.Double(x, y);
					if (pg.contains(p)) {
						points.add(p);
						pointsFinished++;
					}
					if (pointsFinished >= numPoints) {
						done = true;
					}
				}
				last = current;
			}
		}
		if (points.size() >= 1) {
			// Make the circles
			Color[] colorScheme = Application.getColorScheme(NUM_COLORS);
			boolean done = false;
			int colorIndex = 0; // Index for which color in the gradient has been assigned already.
			while (!done) {
				int index = random.nextInt(points.size());
				Point2D.Double p1 = points.get(index);
				boolean rGood = false;
				float radius = 0;
				int passes = 0;
				SimpleCircle c = null;
				while (!rGood) {
					radius = random.nextFloat() * (MAX_RADIUS - MIN_RADIUS) + MIN_RADIUS;
					boolean passed = true;
					// Determine if the new circle would overlap with existing ones
					for (SimpleCircle check : circles) {
						if (dist(p1, check) < radius + check.r) {
							passed = false;
							break;
						}
					}
					passes++;
					if (passed) {
						c = new SimpleCircle((float) p1.x, (float) p1.y, radius, colorScheme[colorIndex]);
						colorIndex++;
						if (colorIndex >= NUM_COLORS) {
							colorIndex = 0;
						}
						circles.add(c);
						// Iterate over the points still existing and remove ones within the new circle
						for (int i = 0; i < points.size(); i++) {
							if (dist(points.get(i), c) <= radius) {
								points.remove(i);
								i--;
							}
						}
						break;
					}
					if (passes > MAX_PASSES) {
						points.remove(index);
						break;
					}
				}
				if (points.size() <= 0) {
					done = true;
				}
			}
		}
	}
	
}
