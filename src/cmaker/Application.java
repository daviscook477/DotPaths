package cmaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Application {
	
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		Application a = new Application();
	}
	
	public static final int APP_WIDTH = 1920, APP_HEIGHT = 1080;
	public static final Dimension APP_DIMENSIONS = new Dimension(APP_WIDTH, APP_HEIGHT);

	private ArrayList<InteractiveSpline> splines;
	
	private DraggableMouseListener draggableMouseListener;	
	
	private JPanel jp; // the JPanel being used for drawing the splines
	
	// thread used to run the app logic that handles updating dragged points
	private Runnable appLogic = new Runnable() {

		@Override
		public void run() {
			while (true) {
				if (draggableMouseListener.getDragged() != null) { // if there is something being dragged
					java.awt.Point mousePos = null;
					try {
						mousePos = jp.getMousePosition().getLocation();
					} catch (Exception e) {
						// Avoid exception that happens due to mousePosition going null
					}
					if (mousePos != null && draggableMouseListener.getDragged() != null) {
						draggableMouseListener.getDragged().cursorAt(mousePos.x, mousePos.y); // tell the dragged item where the cursor is
						jp.repaint(); // repaint the splines to reflect the changes position
					}
				}
				// Be lame and just use sleep
				try {
					Thread.sleep(10); // 100 Hz
				} catch (InterruptedException e) {
					e.printStackTrace(); // Also lame and not caring
				} 
			}
			
		}
		
	};
	
	private static Random random = new Random();
	
	public static Color randColor() {
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		return new Color(r,g,b);
	}
	
	private static Color c1 = randColor();
	private static Color c2 = randColor();
	
	public static Color[] getColorScheme(int numColors) {
		Color[] colors = new Color[numColors];
		//c1 = randColor();
		//c2 = randColor();
		for (int i = 0; i < numColors; i++) {
			float percent2 = i / ((float) numColors);
			float percent1 = 1 - percent2;
			int newR = (int) (c1.getRed()*percent1 + c2.getRed()*percent2);
			int newG = (int) (c1.getGreen()*percent1 + c2.getGreen()*percent2);
			int newB = (int) (c1.getBlue()*percent1 + c2.getBlue()*percent2);
			Color cNew = new Color(newR, newG, newB);
			colors[i] = cNew;
		}
		return colors;
	}
	
	public static Color[] getColorScheme(int numColors, Color base, Color second) {
		Color[] colors = new Color[numColors];
		c1 = base;
		c2 = second;
		for (int i = 0; i < numColors; i++) {
			float percent2 = i / ((float) numColors);
			float percent1 = 1 - percent2;
			int newR = (int) (c1.getRed()*percent1 + c2.getRed()*percent2);
			int newG = (int) (c1.getGreen()*percent1 + c2.getGreen()*percent2);
			int newB = (int) (c1.getBlue()*percent1 + c2.getBlue()*percent2);
			Color cNew = new Color(newR, newG, newB);
			colors[i] = cNew;
		}
		return colors;
	}
	
	private JButton buildDotToggleButton() {
		JButton dotToggle = new JButton("Toggle Dots");
		dotToggle.addActionListener(new ActionListener() {
	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (InteractiveSpline spline : splines) {
					spline.setVisibility(!spline.getVisibility());
				}
				jp.repaint();
			}
			
		});
		return dotToggle;
	}
	
	private JLabel lRed, lBlue, lGreen, lRed2, lBlue2, lGreen2;
	private JTextField red, blue, green, red2, blue2, green2;
	
	private JButton buildRecolorButton(JFrame jf) {
		ActionListener recolor = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				c2 = new Color(Integer.parseInt(red2.getText()), Integer.parseInt(green2.getText()), Integer.parseInt(blue2.getText()));
				c1 = new Color(Integer.parseInt(red.getText()), Integer.parseInt(green.getText()), Integer.parseInt(blue.getText()));
				// reset the points in the spline which causes the circles to be regenerated using the application colors - this is soooo lame
				for (InteractiveSpline spline : splines) {
					spline.pSet.deletePoints();
					spline.pSet.updatePoints(spline);
				}
				jf.repaint();
			}
		};
		JButton recolorButton = new JButton("Recolor");
		recolorButton.addActionListener(recolor);
		return recolorButton;
	}
	
	private void buildUI(JFrame jf, JPanel jp, ArrayList<InteractiveSpline> splines) {	
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jp.setPreferredSize(APP_DIMENSIONS);
		jf.add(jp, BorderLayout.CENTER);
		
		// create a JPanel to contain the dots and color changing UI
		JPanel container = new JPanel();
	
		container.add(buildDotToggleButton(), BorderLayout.NORTH);
		
		lRed = new JLabel("Red: ");
		lBlue = new JLabel("Blue: ");
		lGreen = new JLabel("Green: ");
		red = new JTextField(3);
		blue = new JTextField(3);
		green = new JTextField(3);
		red.setText("" + c1.getRed());
		blue.setText("" + c1.getBlue());
		green.setText("" + c1.getGreen());
		lRed2 = new JLabel("Red 2: ");
		lBlue2 = new JLabel("Blue 2: ");
		lGreen2 = new JLabel("Green 2: ");
		red2 = new JTextField(3);
		blue2 = new JTextField(3);
		green2 = new JTextField(3);
		red2.setText("" + c2.getRed());
		green2.setText("" + c2.getGreen());
		blue2.setText("" + c2.getBlue());
		container.add(lRed, BorderLayout.NORTH);
		container.add(red, BorderLayout.NORTH);
		container.add(lBlue, BorderLayout.NORTH);
		container.add(blue, BorderLayout.NORTH);
		container.add(lGreen, BorderLayout.NORTH);
		container.add(green, BorderLayout.NORTH);
		JPanel colorShower = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2037153880602923073L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(Integer.parseInt(red.getText()), Integer.parseInt(green.getText()), Integer.parseInt(blue.getText())));
				g.fillRect(0, 0, 20, 20);
			}
		};
		JPanel colorShower2 = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -8028032260358219261L;

			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(Integer.parseInt(red2.getText()), Integer.parseInt(green2.getText()), Integer.parseInt(blue2.getText())));
				g.fillRect(0, 0, 20, 20);
			}
		};
		container.add(colorShower);
		container.add(lRed2, BorderLayout.NORTH);
		container.add(red2, BorderLayout.NORTH);
		container.add(lBlue2, BorderLayout.NORTH);
		container.add(blue2, BorderLayout.NORTH);
		container.add(lGreen2, BorderLayout.NORTH);
		container.add(green2, BorderLayout.NORTH);
		container.add(colorShower2);
		
		container.add(buildRecolorButton(jf));
		
		// create the dragging mouse listener and add it
		draggableMouseListener = new DraggableMouseListener(splines);
		jp.addMouseListener(draggableMouseListener);
		
		// add the UI container to the JFrame
		jf.add(container, BorderLayout.NORTH);
		
		// pack it and make it visible
		jf.pack();
		jf.setVisible(true);
	}
	
	/**
	 * Constructs a new application
	 * @throws IOException
	 */
	public Application() throws IOException {
		splines = new ArrayList<InteractiveSpline>();
		
		// build the UI
		JFrame jf = new JFrame();
		jp = new SplineDrawingPanel(splines);
		buildUI(jf, jp, splines);
		
		// set up example spline
		InteractiveSpline example = SplineGenerator.buildSpline(APP_DIMENSIONS.width, APP_DIMENSIONS.height, 1.0f, 4, true);
		splines.add(example);
	
		// start the application logic
		new Thread(appLogic).start();
		
		// this is a terrible artifact of poor coding practices
		example.pSet.updatePoints(example);
		
		// redraw the jPanel with the example spline
		jp.repaint();
		
		// the following code is commented out b/c it used to just make images of randomly generated splines
		// it is kept for the reference of adding screen caps
		
		// THIS CODE IS USED FOR MAKING IMAGES
		/*int numImages = 20;
		String folderPath = "c:\\Users\\Davis\\computerArt\\curveSolid\\";
		File f = new File(folderPath);
		f.mkdirs();
		// Create a bunch of images
		for (int i = 0; i < numImages; i++) {
			BufferedImage bi = new BufferedImage(START_WIDTH, START_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			Graphics2D ig2 = bi.createGraphics();
			// Optional code makes the background non-transparent
			ig2.setColor(Color.WHITE);
			ig2.fillRect(0, 0, START_WIDTH, START_HEIGHT);
			ArrayList<InteractiveSpline> drawSplines = SplineGenerator.buildComplexSpline(START_WIDTH, START_HEIGHT, 1.0f, 7, 3, 0.5f, true);
			for (InteractiveSpline is : drawSplines) {
				is.showDots = true;
				is.draw(ig2);
			}
			ImageIO.write(bi, "PNG", new File(folderPath + "dots" + i + ".png"));
			bi = new BufferedImage(START_WIDTH, START_HEIGHT, BufferedImage.TYPE_INT_ARGB);
			ig2 = bi.createGraphics();
			// Optional code makes the background non-transparent
			ig2.setColor(Color.WHITE);
			ig2.fillRect(0, 0, START_WIDTH, START_HEIGHT);
			for (InteractiveSpline is : drawSplines) {
				is.showDots = false;
				is.draw(ig2);
			}
			ImageIO.write(bi, "PNG", new File(folderPath + "bones" + i + ".png"));
		}*/
	}
	
}
