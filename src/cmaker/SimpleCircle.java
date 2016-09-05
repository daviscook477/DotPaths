package cmaker;

import java.awt.Color;

/**
 * Struct for storing a circle's properties
 * @author Davis
 *
 */
public class SimpleCircle {
	
	// public b/c no need for getters or setters
	public float x, y, r;
	public Color color;
	
	/**
	 * Construct a circle
	 * @param x x-coord
	 * @param y y-coord
	 * @param r radius
	 * @param color color
	 */
	public SimpleCircle(float x, float y, float r, Color color) {
		// set properties
		this.x = x;
		this.y = y;
		this.r = r;
		this.color = color;
	}

}
