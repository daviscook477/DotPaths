package cmaker;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class InteractiveSpline implements IDrawable {
	
	// Don't modify these using anything other than the methods in the Spline Object
	public ArrayList<Point> points; // The points that make up the spline currently
	public ArrayList<ControlPoint> cPoints; // The draggable point in the interface
	public ArrayList<DirectionPoint> dPoints; // The draggable point for changing direction in the interface
	public ArrayList<PartitionPoint> pPoints; // The draggable point for changing partition width in the inferface
	public ArrayList<ExpansionPoint> ePoints; // The clickable point for adding a new control point
	public PointSet pSet;
	
	private boolean visibility = false;
	
	public boolean getVisibility() {
		return visibility;
	}
	
	public void setVisibility(boolean visibility) {
		this.visibility = visibility;
	}
	
	public InteractiveSpline() {
		this.points = new ArrayList<Point>();
		cPoints = new ArrayList<ControlPoint>();
		dPoints = new ArrayList<DirectionPoint>();
		pPoints = new ArrayList<PartitionPoint>();
		ePoints = new ArrayList<ExpansionPoint>();
		pSet = new PointSet();
	}
	
	public static final int PARTITIONS_PER_CURVE = 8;
	
	/**
	 * Appends a point to the end of the spline
	 * @param p the point being appended
	 */
	public void addPoint(Point p) {
		points.add(p);
		ControlPoint cp = new ControlPoint(this, p);
		cPoints.add(cp);
		DirectionPoint dp = new DirectionPoint(this, cp);
		dPoints.add(dp);
		if (points.size() == 1) {
			PartitionPoint pPoint = new PartitionPoint(this, p);
			pPoints.add(pPoint);
			// Don't add the expansion point
		} else {
			for (int i = 1; i <= PARTITIONS_PER_CURVE; i++) {
				float t = i / ((float) PARTITIONS_PER_CURVE);
				Curve c = new Curve(points.get(points.size() - 2), p);
				float[] pos = c.pointAt(t);
				float[] derivative = c.derivativeAt(t);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.add(new PartitionPoint(this, pPartitionPoint));
			}
			// Add the expansion point behind this point
			ExpansionPoint ep0 = new ExpansionPoint(this, cp);
			ePoints.add(ep0);
			// TODO: might need to update partitions here
		}
	}
	
	/**
	 * Adds a point in the middle of the curve
	 * @param p the new point to be added
	 * @param index the index at which the point will be inserted (the item currently at that index will be shifted behind it)
	 */
	public void addPoint(Point p, int index) {
		points.add(index, p);
		ControlPoint cp = new ControlPoint(this, p);
		cPoints.add(index, cp);
		DirectionPoint dp = new DirectionPoint(this, cp);
		dPoints.add(index, dp);
		if (points.size() == 1) {
			PartitionPoint pPoint = new PartitionPoint(this, p);
			pPoints.add(pPoint);
			// Don't add expansion point
		} else {
			for (int i = 1; i < PARTITIONS_PER_CURVE; i++) {
				pPoints.remove(PARTITIONS_PER_CURVE * (index - 1) + 1); // remove points from the same spot because the indicies are updated after each removal
			}
			for (int i = 1; i < 2 * PARTITIONS_PER_CURVE; i++) {
				float t = i / PARTITIONS_PER_CURVE;
				Curve c = new Curve(points.get(index - 1), p);
				float[] pos = c.pointAt(t);
				float[] derivative = c.derivativeAt(t);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.add(PARTITIONS_PER_CURVE * (index - 1) + i, new PartitionPoint(this, pPartitionPoint));
			}
			ExpansionPoint ep0 = new ExpansionPoint(this, cp);
			ePoints.add(index, ep0);
			ExpansionPoint ep = new ExpansionPoint(this, cPoints.get(index + 1));
			ePoints.add(index + 1, ep);
			// Update the partitions because this fixes artifacts - I'm not entirely sure why this is necessary - sorry
			updatePartitions(cp);
			updatePartitions(cPoints.get(index + 1));
		}
	}
	
	
	// Updates the partitions affected by the changes made to the control point
	public void updatePartitions(ControlPoint cp) {
		int index = cPoints.indexOf(cp);
		if (index == 0) {
			Curve c = new Curve(points.get(0), points.get(1));
			for (int i = 0; i < PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set(i, new PartitionPoint(this, pPartitionPoint, pPoints.get(i).radius));
			}
		} else if (index == points.size() - 1) {
			Curve c = new Curve(points.get(index - 1), points.get(index));
			for (int i = 1; i <= PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set((index - 1) * PARTITIONS_PER_CURVE + i, new PartitionPoint(this, pPartitionPoint, pPoints.get((index - 1) * PARTITIONS_PER_CURVE + i).radius));
			}
		} else {
			Curve c = new Curve(points.get(index - 1), points.get(index));
			for (int i = 1; i <= PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set((index - 1) * PARTITIONS_PER_CURVE + i, new PartitionPoint(this, pPartitionPoint, pPoints.get((index - 1) * PARTITIONS_PER_CURVE + i).radius));
			}
			c = new Curve(points.get(index), points.get(index + 1));
			for (int i = 1; i <= PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set(index * PARTITIONS_PER_CURVE + i, new PartitionPoint(this, pPartitionPoint, pPoints.get(index * PARTITIONS_PER_CURVE + i).radius));
			}
		}
	}
	
	// Updates the partitions affected by the changes made to the direction point
	public void updatePartitions(DirectionPoint dp) {
		int index = dPoints.indexOf(dp);
		if (index == 0) {
			Curve c = new Curve(points.get(0), points.get(1));
			for (int i = 0; i < PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set(i, new PartitionPoint(this, pPartitionPoint, pPoints.get(i).radius));
			}
		} else if (index == points.size() - 1) {
			Curve c = new Curve(points.get(index - 1), points.get(index));
			for (int i = 1; i <= PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set((index - 1) * PARTITIONS_PER_CURVE + i, new PartitionPoint(this, pPartitionPoint, pPoints.get((index - 1) * PARTITIONS_PER_CURVE + i).radius));
			}
		} else {
			Curve c = new Curve(points.get(index - 1), points.get(index));
			for (int i = 1; i <= PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set((index - 1) * PARTITIONS_PER_CURVE + i, new PartitionPoint(this, pPartitionPoint, pPoints.get((index - 1) * PARTITIONS_PER_CURVE + i).radius));
			}
			c = new Curve(points.get(index), points.get(index + 1));
			for (int i = 1; i <= PARTITIONS_PER_CURVE; i++) {
				float[] pos = c.pointAt(i * 1.0f / PARTITIONS_PER_CURVE);
				float[] derivative = c.derivativeAt(i * 1.0f / PARTITIONS_PER_CURVE);
				Point pPartitionPoint = new Point(pos[0], pos[1], derivative[0], derivative[1]);
				pPoints.set(index * PARTITIONS_PER_CURVE + i, new PartitionPoint(this, pPartitionPoint, pPoints.get(index * PARTITIONS_PER_CURVE + i).radius));
			}
		}
	}
	
	public void removePoint(Point p) {
		
	}
	
	public void removePoint(int index) {
		
	}

	public ArrayList<IDraggable> getDraggables() {
		ArrayList<IDraggable> drags = new ArrayList<IDraggable>(cPoints.size() + dPoints.size());
		drags.addAll(cPoints);
		drags.addAll(dPoints);
		drags.addAll(pPoints);
		drags.addAll(ePoints);
		return drags;
	}

	@Override
	public void preDraw(Graphics2D g2D) {
		
	}
	
	public static final float DS = 2; // 2px change in length per line segment drawn
	public static final float CIRCLE_SIZE = 5; // 5px radius circles
	public static final float ARROW_SCALE = 0.1f; // arrows are 5 times smaller than derivative

	@Override
	public void draw(Graphics2D g2D) {
		if (visibility) {
			for (SimpleCircle sc : pSet.circles) {
				g2D.setColor(sc.color);
				g2D.fill(new Ellipse2D.Float(sc.x - sc.r, sc.y - sc.r, 2 * sc.r, 2 * sc.r));
			}
			// Optional draw borders around each circle
			g2D.setColor(Color.BLACK);
			g2D.setStroke(new BasicStroke(3));
			for (SimpleCircle sc : pSet.circles) {
				g2D.draw(new Ellipse2D.Float(sc.x - sc.r, sc.y - sc.r, 2 * sc.r, 2 * sc.r));
			}
		} else {
			// Spline itself
			for (int i = 0; i < points.size() - 1; i++/*Curve c : connects*/) {
				Curve c = new Curve(points.get(i), points.get(i + 1));
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
					lastPosP = new Point2D.Float(lastPos[0], lastPos[1]);
					posP = new Point2D.Float(pos[0], pos[1]);
					g2D.setColor(Color.BLACK);
					g2D.draw(new Line2D.Float(lastPosP, posP));
					lastPos = pos;
				}
			}
			// User interface elements
			for (IDrawable id : cPoints) {
				id.draw(g2D);
			}
			for (IDrawable id : dPoints) {
				id.draw(g2D);
			}
			for (IDrawable id : ePoints) {
				id.draw(g2D);
			}
			// UI Elements first because I'm doing a fill in this step
			PartitionPoint last = pPoints.get(0);
			int colorIndex = 0;
			g2D.setColor(PartitionPoint.PURPLE);
			for (PartitionPoint current : pPoints) {
				g2D.setStroke(new BasicStroke(3.0f));
				g2D.draw(new Line2D.Double(last.x2, last.y2, current.x2, current.y2));
				g2D.draw(new Line2D.Double(2*last.p.x - last.x2, 2*last.p.y - last.y2, 2*current.p.x - current.x2, 2*current.p.y - current.y2));
				
				current.draw(g2D);
				last = current;
				colorIndex++;
				if (colorIndex >= (points.size() - 1) * PARTITIONS_PER_CURVE + 1) {
					colorIndex = 0;
				}
			}
		}
	}

	@Override
	public void postDraw(Graphics2D g2D) {
		
	}
	
}
