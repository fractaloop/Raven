package raven.ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;

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
    
	private GameCanvas() {
    	// Don't redraw on requests
    	setIgnoreRepaint(true);
    	
		// Ask for input
		setFocusable(true);
		requestFocus();
	}
	
	///////////////////
	// Painting stuff
	public static void startDrawing() {
		GameCanvas.getInstance().create();
	}
	
	protected void create() {
		if (getBufferStrategy() == null) {
			// Double buffered for pretty rendering
	    	createBufferStrategy(2);
	    	setBounds(0, 0, WIDTH, HEIGHT);
		}
		if (g2d != null) {
			g2d.dispose();
			System.err.println("Warning: GameCanvas received request to start drawing while already drawing.");
		}
		
		g2d = (Graphics2D)getBufferStrategy().getDrawGraphics();
		g2d.setColor(Color.WHITE);
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}
	
	public static void stopDrawing() {
		GameCanvas.getInstance().finish();
	}
	
	protected void finish() {
		if (g2d != null) {
			g2d.dispose();
			getBufferStrategy().show();
			g2d = null;
		}
	}
}
