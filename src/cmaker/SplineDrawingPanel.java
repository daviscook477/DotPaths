package cmaker;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

/**
 * JPanel that draws some splines
 * @author Davis
 *
 */
public class SplineDrawingPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6603283898136039697L;

	private ArrayList<InteractiveSpline> splines;
	
	public SplineDrawingPanel(ArrayList<InteractiveSpline> splines) {
		this.splines = splines;
	}
	
	@Override
	public void paintComponent(Graphics graph) {
		// clear previous paint
		super.paintComponent(graph);
		Graphics2D g2D = (Graphics2D) graph;
		// perform 3 stage drawing
		for (InteractiveSpline spline : splines) {
			spline.preDraw(g2D);
		}
		for (InteractiveSpline spline : splines) {
			spline.draw(g2D);
		}
		for (InteractiveSpline spline : splines) {
			spline.postDraw(g2D);
		}
	}
}
