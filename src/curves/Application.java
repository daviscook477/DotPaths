package curves;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Application {
	
	public static void main(String[] args) {
		Generator g = new Generator();
		int width = g.width();
		int height = g.height();
		JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		@SuppressWarnings("serial")
		JPanel jp = new JPanel() {
			@Override
			public void paintComponent(Graphics graph) {
				super.paintComponent(graph);
				Graphics2D g2D = (Graphics2D) graph;
				g.draw(g2D);
			}
		};
		jp.setPreferredSize(new Dimension(width * 2, height * 2));
		jf.add(jp);
		jf.pack();
		jf.setVisible(true);
		MouseListener ml = new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
	
}
