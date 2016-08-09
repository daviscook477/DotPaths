package cmaker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
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
	
	public static final int START_WIDTH = 1500, START_HEIGHT = 1000;
	
	private ArrayList<IDraggable> drags;
	private ArrayList<IDraggableSet> dragSets;
	private ArrayList<IDrawable> draws;

	private IDraggable dragged = null;
	
	private MouseListener mouseList = new MouseListener() {
		
		@Override
		public void mouseClicked(MouseEvent e) {
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			if (dragged != null) {
				dragged.dropped(x, y);
			}
			dragged = null;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			int x = e.getX();
			int y = e.getY();
			boolean found = false;
			for (IDraggable id : drags) {
				if (id.cursorInBounds(x, y)) {
					dragged = id;
					id.pickedUp(x, y);
					found = true;
					break;
				}
			}
			if (!found) {
				for (IDraggableSet ids : dragSets) {
					for (IDraggable id : ids.getDraggables()) {
						if (id.cursorInBounds(x, y)) {
							dragged = id;
							id.pickedUp(x, y);
							break;
						}
					}
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			int x = e.getXOnScreen();
			int y = e.getYOnScreen();
			if (dragged != null) {
				dragged.dropped(x, y);
				jp.repaint();
			}
			dragged = null;
		}
		
	};
	
	private Runnable appLogic = new Runnable() {

		@Override
		public void run() {
			while (true) {
				if (dragged != null) {
					java.awt.Point mousePos = null;
					try {
						mousePos = jp.getMousePosition().getLocation();
					} catch (Exception e) {
						// Avoid exception that happens due to mousePosition going null
					}
					if (mousePos != null && dragged != null) {
						dragged.cursorAt(mousePos.x, mousePos.y);
						jp.repaint();
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
	
	@SuppressWarnings("serial")
	private JPanel jp = new JPanel() {
		@Override
		public void paintComponent(Graphics graph) {
			super.paintComponent(graph);
			Graphics2D g2D = (Graphics2D) graph;
			for (IDrawable id : draws) {
				id.preDraw(g2D);
			}
			for (IDrawable id : draws) {
				id.draw(g2D);
			}
			for (IDrawable id : draws) {
				id.postDraw(g2D);
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
	
	private InteractiveSpline spline;
	private ArrayList<InteractiveSpline> splines;
	
	public Application() throws IOException {
		drags = new ArrayList<IDraggable>();
		dragSets = new ArrayList<IDraggableSet>();
		draws = new ArrayList<IDrawable>();
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jp.setPreferredSize(new Dimension(START_WIDTH, START_HEIGHT));
		jf.add(jp, BorderLayout.CENTER);
		JPanel container = new JPanel();
		JButton dotToggle = new JButton("Toggle Dots");
		dotToggle.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				spline.showDots = !spline.showDots;
				//for (InteractiveSpline is : splines) {
				//	is.showDots = !is.showDots;
				//}
				jp.repaint();
			}
			
		});
		container.add(dotToggle, BorderLayout.NORTH);
		JLabel lRed = new JLabel("Red: ");
		JLabel lBlue = new JLabel("Blue: ");
		JLabel lGreen = new JLabel("Green: ");
		JTextField red = new JTextField(3);
		JTextField blue = new JTextField(3);
		JTextField green = new JTextField(3);
		red.setText("" + c1.getRed());
		green.setText("" + c1.getGreen());
		blue.setText("" + c1.getBlue());
		JLabel lRed2 = new JLabel("Red 2: ");
		JLabel lBlue2 = new JLabel("Blue 2: ");
		JLabel lGreen2 = new JLabel("Green 2: ");
		JTextField red2 = new JTextField(3);
		JTextField blue2 = new JTextField(3);
		JTextField green2 = new JTextField(3);
		red.setText("" + c1.getRed());
		green.setText("" + c1.getGreen());
		blue.setText("" + c1.getBlue());
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
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.setColor(new Color(Integer.parseInt(red.getText()), Integer.parseInt(green.getText()), Integer.parseInt(blue.getText())));
				g.fillRect(0, 0, 20, 20);
			}
		};
		JPanel colorShower2 = new JPanel() {
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
		spline = SplineGenerator.buildSpline(START_WIDTH, START_HEIGHT, 1.0f, 4, true);
		ActionListener recolor = new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				c2 = new Color(Integer.parseInt(red2.getText()), Integer.parseInt(green2.getText()), Integer.parseInt(blue2.getText()));
				c1 = new Color(Integer.parseInt(red.getText()), Integer.parseInt(green.getText()), Integer.parseInt(blue.getText()));
				spline.pSet.deletePoints();
				spline.pSet.updatePoints(spline);
				jf.repaint();
			}
		};
		JButton recolorButton = new JButton("Recolor");
		recolorButton.addActionListener(recolor);
		container.add(recolorButton);
		jf.add(container, BorderLayout.NORTH);
		jf.pack();
		jp.addMouseListener(mouseList);
		jf.setVisible(true);
		new Thread(appLogic).start();
		/*spline = new InteractiveSpline();
		Point p0 = new Point(200, 200, 300, 0);
		Point p1 = new Point(400, 200, 300, 0);
		Point p2 = new Point(600, 200, 300, 0);
		spline.addPoint(p0);
		spline.addPoint(p1);
		spline.addPoint(p2);*/
		
		//splines = SplineGenerator.buildComplexSpline(START_WIDTH, START_HEIGHT, 1.0f, 7, 3, 0.5f, true);
		dragSets.add(spline);
		draws.add(spline);
		/*for (InteractiveSpline is : splines) {
			dragSets.add(is);
			draws.add(is);
			is.pSet.updatePoints(is);
		}*/
		spline.pSet.updatePoints(spline);
		jp.repaint();
		
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
