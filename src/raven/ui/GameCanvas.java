package raven.ui;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import raven.math.Vector2D;

@SuppressWarnings("serial")
public class GameCanvas extends Canvas {
	// Singleton canvas, just like the original.
	private static class GameCanvasHolder {
		public static final GameCanvas INSTANCE = new GameCanvas();
	}

	public static GameCanvas getInstance() {
		return GameCanvasHolder.INSTANCE;
	}
	
	private Graphics2D g2d;
	
	private Color pen, brush;
	
	private float stroke;
	
	private GameCanvas() {
    	// Don't redraw on requests
    	setIgnoreRepaint(true);
    	
		// Ask for input
		setFocusable(true);
		requestFocus();
		
		brush = null;
		pen = Color.BLACK;
		stroke = 1;
	}
	
	///////////////////////
	// Drawing start/stop
	public static void startDrawing(int width, int height) {
		getInstance().create(width, height);
	}
	
	protected void create(int width, int height) {
    	// Don't redraw on requests
    	setIgnoreRepaint(true);
    	
		// Ask for input
		setFocusable(true);
		requestFocus();
		if (getBufferStrategy() == null) {
			// Double buffered for pretty rendering
	    	createBufferStrategy(2);
	    	setBounds(0, 0, width, height);
		}
		if (g2d != null) {
			g2d.dispose();
			System.err.println("Warning: GameCanvas received request to start drawing while already drawing.");
		}
		
		g2d = (Graphics2D)getBufferStrategy().getDrawGraphics();
		// Shiny gfx
		RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2d.setRenderingHints(renderHints);

		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}
	
	public static void stopDrawing() {
		GameCanvas.getInstance().finish();
	}
	
	protected void finish() {
		if (g2d != null) {
			getBufferStrategy().show();
			g2d.dispose();
			g2d = null;
		}
	}
	
	////////////////////
	// Color selection
	// Pens are the lines, brushes are the fills.
	
	protected static void lineColor(Color color) {
		getInstance().pen = color;
	}
	
	protected static void lineWidth(int width) {
		getInstance().stroke = width;
	}
	
	protected static void fillWith(Color color) {
		getInstance().brush = color;		
	}
	
	public static void blackPen() { lineColor(Color.BLACK); lineWidth(1); }
	public static void whitePen() { lineColor(Color.WHITE); lineWidth(1); }
	public static void redPen() { lineColor(Color.RED); lineWidth(1); }
	public static void greenPen() { lineColor(Color.GREEN); lineWidth(1); }
	public static void bluePen() { lineColor(Color.BLUE); lineWidth(1); }
	public static void greyPen() { lineColor(Color.GRAY); lineWidth(1); }
	public static void pinkPen() { lineColor(Color.PINK); lineWidth(1); }
	public static void yellowPen() { lineColor(Color.YELLOW); lineWidth(1); }
	public static void orangePen() { lineColor(Color.ORANGE); lineWidth(1); }
	public static void purplePen() { lineColor(new Color(255, 0, 170)); lineWidth(1); }
	public static void brownPen() { lineColor(new Color(133, 90, 0)); lineWidth(1); }
	
	public static void darkGreenPen() { lineColor(new Color(0, 100, 0)); lineWidth(1); }
	public static void lightBluePen() { lineColor(new Color(0, 255, 255)); lineWidth(1); }
	public static void lightGreyPen() { lineColor(Color.LIGHT_GRAY); lineWidth(1); }
	public static void lightPinkPen() { lineColor(new Color(255, 230, 230)); lineWidth(1); }
	
	public static void thickBlackPen() { lineColor(Color.BLACK); lineWidth(2); }
	public static void thickWhitePen() { lineColor(Color.RED); lineWidth(2); }
	public static void thickRedPen() { lineColor(Color.RED); lineWidth(2); }
	public static void thickGreenPen() { lineColor(Color.GREEN); lineWidth(2); }
	public static void thickBluePen() { lineColor(Color.BLUE); lineWidth(2); }
	public static void thickGreyPen() { lineColor(Color.LIGHT_GRAY); lineWidth(2); }
	
	public static void blackBrush() { fillWith(Color.BLACK); }
	public static void whiteBrush() { fillWith(Color.WHITE); }
	public static void hollowBrush() { fillWith(null); }
	public static void greenBrush() { fillWith(Color.GREEN); }
	public static void redBrush() { fillWith(Color.RED); }
	public static void blueBrush() { fillWith(Color.BLUE); }
	public static void greyBrush() { fillWith(Color.GRAY); }
	public static void brownBrush() { fillWith(new Color(133, 90, 0)); }
	public static void yellowBrush() { fillWith(Color.YELLOW); }
	public static void lightBlueBrush() { fillWith(new Color(0, 255, 255)); }
	public static void darkGreenBrush() { fillWith(new Color(0, 100, 0)); }
	public static void orangeBrush() { fillWith(Color.ORANGE); }

	////////////////////////
	// Rendering utilities
	
	// Text manipulation
	
	public static void textAtPos(int x, int y, String text) {
		getInstance().g2d.setPaint(getInstance().pen);
		getInstance().g2d.drawString(text, x, y);
	}
	
	public static void textAtPos(double x, double y, String text) {
		getInstance().g2d.setPaint(getInstance().pen);
		getInstance().g2d.drawString(text, (float)x, (float)y);
	}
	
	public static void textAtPos(Vector2D pos, String text) {
		textAtPos(pos.x, pos.y, text);
	}
	
	// Pixel manipulation
	
	public static void drawDot(Vector2D pos, Color color) {
		drawDot((int)pos.x, (int)pos.y, color);
	}
	
	public static void drawDot(int x, int y, Color color) {
		Graphics2D g = getInstance().g2d;
		Color oldColor = g.getColor();
		g.drawLine(x, y, x, y);
		g.setColor(oldColor);
	}
	
	// Line drawing
	
	public static void line(Vector2D from, Vector2D to) {
		line(from.x, from.y, to.x, to.y);
	}
	
	public static void line(double x1, double y1, double x2, double y2) {
		line((int)x1, (int)y1, (int)x2, (int)y2);
	}

	public static void line(int x1, int y1, int x2, int y2) {
		Graphics2D g = getInstance().g2d;
		g.setStroke(new BasicStroke(getInstance().stroke));
		g.setColor(getInstance().pen);
		g.drawLine(x1, y1, x2, y2);
	}
	
	public static void polyLine(final List<Vector2D> points) {
		if (points.size() < 2)
			return;
		
		for (int i = 0; i+1 < points.size(); i++) {
			line(points.get(i), points.get(i+1));
		}
	}
	
	public static void lineWithArrow(Vector2D from, Vector2D to, double size) {
		Vector2D norm = to.sub(from);
		norm.normalize();
		
		// Calculate where the arrow is attached
		Vector2D crossingPoint = to.sub(norm.mul(size));
		
		// Calculate the two extra points required to make the arrowhead
		Vector2D arrowPoint1 = crossingPoint.add(norm.perp().mul(0.4).mul(size));
		Vector2D arrowPoint2 = crossingPoint.sub(norm.perp().mul(0.4).mul(size));
		
		// Draw the line
		line(from, to);
		
		// Draw the arrowhead
		polyLine(new ArrayList<Vector2D>(Arrays.asList(arrowPoint1, arrowPoint2, to)));
	}
	
	public static void cross(Vector2D pos, int radius) {
		line(pos.x - radius, pos.y - radius, pos.x + radius, pos.y + radius);
		line(pos.x + radius, pos.y + radius, pos.x - radius, pos.y - radius);
	}
	
	// Geometry drawing
	
	public static void rect(double left, double top, double right, double bottom) {
		rect((int)left, (int)top, (int)right, (int)bottom);
	}
	
	public static void rect(int left, int top, int right, int bottom) {
		Graphics2D g = getInstance().g2d;
		g.setPaint(getInstance().brush);
		g.fillRect(left, top, right - left, bottom - top);
		g.setStroke(new BasicStroke(getInstance().stroke));
		g.setColor(getInstance().pen);
		g.drawRect(left, top, right - left, bottom - top);
	}
	
	public static void closedShape(List<Vector2D> points) {
		List<Vector2D> shape = new LinkedList<Vector2D>(points);
		shape.add(shape.get(0));
		polyLine(shape);
	}
	
	public static void filledRect(int left, int top, int right, int bottom){
		Graphics2D g = getInstance().g2d;
		g.setPaint(getInstance().brush);
		g.fillRect(left, top, right - left, bottom - top);
	}
	
	public static void circle(Vector2D pos, double radius) {
		circle(pos.x, pos.y, radius);
	}
	
	public static void filledCircle(Vector2D center, double rad) {
		Graphics2D g = getInstance().g2d;
		g.setPaint(getInstance().brush);
		getInstance().g2d.fillOval((int)(center.x-rad), (int)(center.y-rad), (int)(2*rad), (int)(2*rad));
		g.setStroke(new BasicStroke(getInstance().stroke));
		g.setColor(getInstance().pen);
		getInstance().g2d.drawOval((int)(center.x-rad), (int)(center.y-rad), (int)(2*rad), (int)(2*rad));
	}
	
	public static void filledCircle(double centerX, double centerY, double rad) {
		Graphics2D g = getInstance().g2d;
		g.setPaint(getInstance().brush);
		getInstance().g2d.fillOval((int)(centerX-rad), (int)(centerY-rad), (int)(2*rad), (int)(2*rad));
		g.setStroke(new BasicStroke(getInstance().stroke));
		g.setColor(getInstance().pen);
		getInstance().g2d.drawOval((int)(centerX-rad), (int)(centerY-rad), (int)(2*rad), (int)(2*rad));
	}
	
	public static void circle(double x, double y, double radius) {
		Graphics2D g = getInstance().g2d;
		g.setPaint(getInstance().brush);
		getInstance().g2d.draw(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
		g.setStroke(new BasicStroke(getInstance().stroke));
		g.setColor(getInstance().pen);
		getInstance().g2d.draw(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
	}
}
