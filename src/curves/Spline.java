package curves;

import java.util.ArrayList;

public class Spline {

	// Don't modify these using anything other than the methods in the Spline Object
	public ArrayList<Point> points; // The points that make up the spline currently
	public ArrayList<Curve> connects; // The connecting curves between the points
	
	public Spline() {
		this.points = new ArrayList<Point>();
		connects = new ArrayList<Curve>();
	}
	
	public Spline(ArrayList<Point> points) {
		this.points = points;
		int numCurves = points.size() - 1;
		connects = new ArrayList<Curve>(numCurves);
		// If there are sufficient points to start making connecting curves
		if (points.size() > 1) {
			Point pLast = points.get(0);
			for (int i = 1; i < numCurves; i++) {
				Point pCurrent = points.get(i);
				Curve c = new Curve(pLast, pCurrent);
				pLast = pCurrent;
				connects.add(c);
			}
		}
	}
	
	/**
	 * Appends a point to the end of the spline
	 * @param p the point being appended
	 */
	public void addPoint(Point p) {
		int lastIndex = points.size() - 1;
		if (lastIndex >= 0) {
			Point last = points.get(points.size() - 1);
			points.add(p);
			Curve c = new Curve(last, p);
			connects.add(c);
		} else {
			points.add(p);
		}
	}
	
	/**
	 * Adds a point in the middle of the curve
	 * @param p the new point to be added
	 * @param index the index at which the point will be inserted (the item currently at that index will be shifted behind it)
	 */
	public void addPoint(Point p, int index) {
		if (index >= points.size()) { // If it is being added to the end of the list
			addPoint(p);
			return;
		}
		points.add(index, p);
		if (index - 1 < 0) { // Special case for if the first index to change is < 0 i.e. we're adding to the start of the list
			Curve c = connects.get(0);
			Point end = c.p1;
			Curve cNew = new Curve(p, end);
			connects.add(0, cNew);
		} else {
			Curve c = connects.remove(index - 1);
			Point start = c.p1;
			Point end = c.p2;
			Curve cNew1 = new Curve(start, p);
			Curve cNew2 = new Curve(p, end);
			connects.add(index - 1, cNew1);
			connects.add(index, cNew2);
		}
	}
	
	public void removePoint(Point p) {
		
	}
	
	public void removePoint(int index) {
		
	}
	
}
