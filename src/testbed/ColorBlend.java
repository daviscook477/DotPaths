package testbed;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ColorBlend {

	private static Random random = new Random();
	
	public static Color randColor() {
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		return new Color(r,g,b);
	}
	
	public static void main(String[] args) {
		int width = 1000; int height = 600;
		int partitions = 1000;
		Color c1 = randColor(); 
		Color c2 = randColor();
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel jp = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2D = (Graphics2D) g;
				for (int i = 0; i < partitions; i++) {
					float percent2 = i / ((float) partitions);
					float percent1 = 1 - percent2;
					int newR = (int) (c1.getRed()*percent1 + c2.getRed()*percent2);
					int newG = (int) (c1.getGreen()*percent1 + c2.getGreen()*percent2);
					int newB = (int) (c1.getBlue()*percent1 + c2.getBlue()*percent2);
					Color cNew = new Color(newR, newG, newB);
					g2D.setColor(cNew);
					g2D.fill(new Rectangle2D.Float(width * percent2, 0, width / ((float)partitions), height));
				}
			}
		};
		jp.setPreferredSize(new Dimension(width, height));
		jf.add(jp);
		jf.pack();
		jf.setVisible(true);
	}
	
}
