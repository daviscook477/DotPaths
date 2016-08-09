package expands;

import java.awt.BasicStroke;
import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
//import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
//import javax.swing.JFrame;
//import javax.swing.JPanel;

public class ArtGif2 {
	
	// A list of cool settings follows:
	
	// 300, 300, 1000, N/A, true, false (rectangular)
	// 500, 300, 1000, N/A, true, true (ellipse)
	
	public static void main(String[] args) throws Exception {
		int width = 1000, height = 1000;
		int dots = 1000;
		int numImages = 1;
		String folderPath = "c:\\Users\\Davis\\computerArtGif2\\test6\\";
		File f = new File(folderPath);
		f.mkdirs();
		// Create a bunch of images
		for (int i = 0; i < numImages; i++) {
			ArtGif2 a = new ArtGif2(width, height, dots, getColorScheme(), true, false, new File(folderPath + "test" + i + ".gif"));
			/*ArrayList<Circle> circles = a.getCircles();
			BufferedImage bi = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig2 = bi.createGraphics();
			// Optional code makes the background non-transparent
			ig2.setColor(Color.WHITE);
			ig2.fillRect(0, 0, width * 2, height * 2);
			// Write to the image.
			for (Circle c : circles) {
				ig2.setColor(c.color);
				ig2.fill(new Ellipse2D.Float(c.x - c.r + width * 0.5f, c.y - c.r + height * 0.5f, 2 * c.r, 2 * c.r));
			}
			// Optional draw borders around each circle
			ig2.setColor(Color.BLACK);
			ig2.setStroke(new BasicStroke(3));
			for (Circle c : circles) {
				ig2.draw(new Ellipse2D.Float(c.x - c.r + width * 0.5f, c.y - c.r + height * 0.5f, 2 * c.r, 2 * c.r));
			}*/
		}
		
		/*JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jp = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2D = (Graphics2D) g;
				for (Circle c : circles) {
					g2D.setColor(c.color);
					g2D.fill(new Ellipse2D.Float(c.x - c.r + width * 0.5f, c.y - c.r + height * 0.5f, 2 * c.r, 2 * c.r));
				}
			}
		};
		jp.setPreferredSize(new Dimension(width * 2, height * 2));
		jf.add(jp);
		jf.pack();
		jf.setVisible(true);*/
	}

	private class Circle {
		public float x, y, r;
		public Color color;
		public Circle(float x, float y, float r, Color color) {
			this.x = x;
			this.y = y;
			this.r = r;
			this.color = color;
		}
	}
	
	private static Random random = new Random();
	
	// Code for generating a nice random color scheme
	
	public static Color randColor() {
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		return new Color(r,g,b);
	}
	
	public static final int NUM_COLORS = 20;
	
	public static Color[] getColorScheme() {
		Color[] colors = new Color[NUM_COLORS];
		Color c1 = randColor(); Color c2 = randColor();
		for (int i = 0; i < NUM_COLORS; i++) {
			float percent2 = i / ((float) NUM_COLORS);
			float percent1 = 1 - percent2;
			int newR = (int) (c1.getRed()*percent1 + c2.getRed()*percent2);
			int newG = (int) (c1.getGreen()*percent1 + c2.getGreen()*percent2);
			int newB = (int) (c1.getBlue()*percent1 + c2.getBlue()*percent2);
			Color cNew = new Color(newR, newG, newB);
			colors[i] = cNew;
		}
		return colors;
	}
	
	public static float dist(Point2D.Float p, Circle c) {
		return (float) Math.sqrt(((p.x - c.x) * (p.x - c.x)) + ((p.y - c.y) * (p.y - c.y)));
	}
	
	private ArrayList<Circle> circles;
	
	public static final float TOO_SMALL_RADII = 5.0f;
	public static final float CONTAINS_TOLERANCE = 30.0f;
	
	// Functions for generating the points in interesting patterns.
	
	// Rectangular.
	
	public static boolean rectangleValid(float width, float height, float x, float y) {
		if (x >= 0 && y >= 0 && x <= width && y <= height) {
			return true;
		}
		return false;
	}
	
	public static boolean rectangleContainsCircle(float width, float height, float x, float y, float r, float tolerance) {
		if (x - r <= (0 - tolerance) || x + r >= (width + tolerance) || y - r <= (0 - tolerance) || y + r >= (height + tolerance)) {
			return false;
		}
		return true;
	}
	
	public static ArrayList<Point2D.Float> rectanglePoints(float width, float height, int num) {
		ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>();
		for (int i = 0; i < num; i++) {
			float x = random.nextFloat() * width;
			float y = random.nextFloat() * height;
			// Don't bother to check for point validity.
			Point2D.Float point = new Point2D.Float(x, y);
			points.add(point);
		}
		return points;
	}
	
	// Ellipse.
	
	public static boolean ellipseValid(float width, float height, float x, float y) {
		float testValX = (x - width * 0.5f) * (x - width * 0.5f) / ((width * 0.5f) * (width * 0.5f));
		float testValY = (y - height * 0.5f) * (y - height * 0.5f) / ((height * 0.5f) * (height * 0.5f));
		if (testValX + testValY <= 1) {
			return true;
		}
		return false;
	}
	
	public static boolean ellipseContainsCircle(float width, float height, float x, float y, float r, float tolerance) {
		if (!ellipseValid(width, height, x + r, y)) {
			return false;
		}
		if (!ellipseValid(width, height, x - r, y)) {
			return false;
		}
		if (!ellipseValid(width, height, x, y + r)) {
			return false;
		}
		if (!ellipseValid(width, height, x, y - r)) {
			return false;
		}
		return true;
	}
	
	public ArrayList<Point2D.Float> ellipsePoints(float width, float height, int num) {
		ArrayList<Point2D.Float> points = new ArrayList<Point2D.Float>();
		while (points.size() < num) {
			float x = random.nextFloat() * width;
			float y = random.nextFloat() * height;
			if (ellipseValid(width, height, x, y)) {
				Point2D.Float point = new Point2D.Float(x, y);
				points.add(point);
			}
		}
		return points;
	}
	
	public ArtGif2(int width, int height, int num, Color[] colorScheme, boolean tooSmall, boolean withinBounds, File write) throws Exception {
		// Get ready to make a cool gif - ART STUFF
		List<GifFrame> gifFrames = new ArrayList<GifFrame>();
		OutputStream output = new FileOutputStream(write);
		int transparantColor = 0xFF00FF; // purple
		BufferedImage bi = new BufferedImage(width * 2, height * 2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D ig2 = bi.createGraphics();
		ig2.setColor(Color.WHITE);
		ig2.fillRect(0, 0, width * 2, height * 2);
		
		BufferedImage gif = ImageUtil.convertRGBAToGIF(bi, transparantColor);

	    // every frame takes 100ms
	    long delay = 100;

	    // make transparent pixels not 'shine through'
	    String disposal = GifFrame.RESTORE_TO_PREVIOUS;

	    // add frame to sequence
	    gifFrames.add(new GifFrame(gif, delay, disposal));
	    
		// Calculate the maximum radius of any circle to be half the diagonal of the rectangle defined by width and height.
		float maxRadius = (float) Math.sqrt((width * width) + (height * height)) * 0.25f;
		// Create randomly distributed points within the rectangle defined by width and height.
		ArrayList<Point2D.Float> points = rectanglePoints(width, height, num);
		// Randomly select points and expand them into circles, removing any points that would be enclosed by the circle.
		boolean done = false;
		int colorIndex = 0; // Index for which color in the gradient has been assigned already.
		circles = new ArrayList<Circle>();
		int indexCounter = 1;
		while (!done) {
			System.out.println("Image " + indexCounter);
			indexCounter++;
			int index = random.nextInt(points.size());
			Point2D.Float p1 = points.get(index);
			boolean rGood = false;
			float radius = 0;
			while (!rGood) {
				radius = random.nextFloat() * maxRadius;
				boolean passed = true;
				for (Circle check : circles) {
					if (dist(p1, check) < radius + check.r) {
						passed = false;
						break;
					}
				}
				// Optional check to remove circles that pass outside of the boundary - NVM this is hard.
				if (withinBounds) {
					if (!rectangleContainsCircle(width, height, p1.x, p1.y, radius, CONTAINS_TOLERANCE)) {
						passed = false;
					}
				}
				if (passed) {
					rGood = true;
				}
			}
			// Determine if the new circle would overlap with existing ones
			Circle c = new Circle(p1.x, p1.y, radius, colorScheme[colorIndex]);
			colorIndex++;
			if (colorIndex >= NUM_COLORS) {
				colorIndex = 0;
			}
			// Optional check to remove circles with really small radii
			if (c.r > TOO_SMALL_RADII || (!tooSmall && c.r <= TOO_SMALL_RADII)) {
				circles.add(c);
				// Draw the circle - ART STUFF
				ig2.setColor(c.color);
				ig2.fill(new Ellipse2D.Float(c.x - c.r + width * 0.5f, c.y - c.r + height * 0.5f, 2 * c.r, 2 * c.r));
				gif = ImageUtil.convertRGBAToGIF(bi, transparantColor);
			    // add frame to sequence
			    gifFrames.add(new GifFrame(gif, delay, disposal));
			}
		
			// Iterate over the points still existing and remove ones within the new circle
			for (int i = 0; i < points.size(); i++) {
				if (dist(points.get(i), c) <= radius) {
					points.remove(i);
					i--;
				}
			}
			if (points.size() <= 0) {
				done = true;
			}
		}
		// save the gif
		int loopCount = 0; // loop indefinitely
		ImageUtil.saveAnimatedGIF(output, gifFrames, loopCount);
	}
	
	public ArrayList<Circle> getCircles() {
		return circles;
	}
	
}
