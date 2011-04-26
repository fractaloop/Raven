package raven.edit.tools;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;

import javax.swing.event.MouseInputListener;

import raven.edit.editor.Viewport;
import raven.edit.editor.ViewportDelegate;
import raven.game.RavenMap;
import raven.math.Vector2D;

public abstract class EditorTool implements MouseInputListener, MouseWheelListener, KeyListener, ComponentListener {
	
	protected ViewportDelegate delegate;
	protected Viewport viewport;
	
	protected RavenMap level;
	
	
	public EditorTool(ViewportDelegate delegate) {
		this.level = delegate.getLevel();
		this.delegate = delegate;
		
		this.viewport = delegate.getViewport();
		
		// Remove any previous tools that were listening
		for (KeyListener listener : viewport.getListeners(KeyListener.class))
			if (listener instanceof EditorTool)
				viewport.removeKeyListener(listener);
		for (MouseListener listener : viewport.getListeners(MouseListener.class))
			if (listener instanceof EditorTool)
				viewport.removeMouseListener(listener);
		for (MouseMotionListener listener : viewport.getListeners(MouseMotionListener.class))
			if (listener instanceof EditorTool)
				viewport.removeMouseMotionListener(listener);
		for (MouseWheelListener listener : viewport.getListeners(MouseWheelListener.class))
			if (listener instanceof EditorTool)
				viewport.removeMouseWheelListener(listener);
		
		// Add this new tool
		viewport.addComponentListener(this);
		viewport.addKeyListener(this);
		viewport.addMouseListener(this);
		viewport.addMouseMotionListener(this);
		viewport.addMouseWheelListener(this);
		
		viewport.repaint();
	}	
	
	public abstract void paintComponent(Graphics g);
		
	/////////////////////////
	// Coordinate transform
	
	public Vector2D viewToLevel(Point viewCoords) {
		Vector2D result = new Vector2D(viewCoords.x, viewCoords.y);
//		result.add(viewport.getLevelPosition());
		return result;		
	}
	
	public Point2D levelToView(Vector2D worldCoords) {
		Vector2D result = new Vector2D(worldCoords);
//		result.sub(viewport.getLevelPosition());
		
		return new Point2D.Float((float)result.x, (float)result.y);
	}
	
	///////////////////
	// Input handlers

	public void keyPressed(KeyEvent e) { }
	public void keyReleased(KeyEvent e) { }
	public void keyTyped(KeyEvent e) { }
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) { }
	public void mouseMoved(MouseEvent e) { }
	public void mouseWheelMoved(MouseWheelEvent e) { }
	public void componentHidden(ComponentEvent e) { }
	public void componentMoved(ComponentEvent e) { viewport.repaint(); }
	public void componentResized(ComponentEvent e) { viewport.repaint(); }
	public void componentShown(ComponentEvent e) { viewport.repaint(); }
}
