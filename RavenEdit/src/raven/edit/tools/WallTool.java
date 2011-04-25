package raven.edit.tools;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import raven.edit.editor.ViewportDelegate;
import raven.game.RavenMap;
import raven.math.Vector2D;
import raven.math.Wall2D;

public class WallTool extends EditorTool {

	private Font font;

	private float snapDistance = 10;
	private boolean isSnapped;
	private boolean isDrawing;
	private boolean isOrtho;

	private ArrayList<Vector2D> drawingPoints;
	
	private Point mouseLocation;
	private Vector2D levelCursor;

	public WallTool(ViewportDelegate delegate) {
		super(delegate);
		
		levelCursor = null;
		
		font = new Font("SansSerif", Font.PLAIN, 12);
	}
	
	// EditorTool needs 

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Line2D line;
		
		// Render any line we are drawing
		g2d.setColor(Color.LIGHT_GRAY);
		if (isDrawing) {
			for (int i = 0; i < drawingPoints.size() - 1; i++) {
				line = new Line2D.Float(levelToView(drawingPoints.get(i)), levelToView(drawingPoints.get(i+1)));
				g2d.draw(line);
			}
			// Draw the line from the last point to the current mouse position
			line = new Line2D.Float(levelToView(drawingPoints.get(drawingPoints.size()-1)), levelToView(levelCursor));
			g2d.draw(line);
		}
		
		// Render the level cursor
		if (levelCursor != null) {
			Ellipse2D highlight = new Ellipse2D.Double(levelToView(levelCursor).getX() - snapDistance, levelToView(levelCursor).getY() - snapDistance, 2 * snapDistance, 2 * snapDistance);
			g2d.setColor(Color.ORANGE);
			g2d.draw(highlight);
		}
		
		// Give some coordinates in the upper right
		if (levelCursor != null) {
			FontMetrics fm = g2d.getFontMetrics(font);
			String coordString = new String("(" + levelCursor.x + ", " + levelCursor.y + ")");
			int width = fm.stringWidth(coordString);
			g2d.setFont(font);
			g2d.setColor(Color.BLACK);
			g2d.drawString(coordString, 0, 10);//getWidth() - width - 10, getHeight() + 10);
		}
	}
	
	///////////////////
	// Drawing points
	
	protected void addDrawingPoint(Vector2D point) {
		if (drawingPoints == null)
			drawingPoints = new ArrayList<Vector2D>();
		
		drawingPoints.add(point);
		delegate.updateStatus("Added point at (" + point.x + ", " + point.y + ")");
		
		// Always end drawing if you are snapping and endpoint 
		if (isSnapped && drawingPoints.size() > 1)
			commitDrawing();
		else if (drawingPoints.size() > 1) commitDrawing();
	}
	
	protected void commitDrawing() {
		isDrawing = false;
		isSnapped = false;
		
		if (drawingPoints == null)
			return;
		
		delegate.addWalls(drawingPoints.toArray(new Vector2D[drawingPoints.size()]));
		delegate.updateStatus("Added " + (drawingPoints.size() - 1) + " new walls.");
		
		drawingPoints = null;
		
		updateCursorSnap();
		
		viewport.repaint();
	}
	
	private void cancelDrawing() {
		isDrawing = false;
		drawingPoints = null;
		
		delegate.updateStatus("Canceled drawing.");
	}
	

	protected void updateCursorSnap() {
		// Don't bother if we're not even in the level
		if (levelCursor == null) {
			isSnapped = false;
			return;
		}
		
		// Try to snap to a wall endpoint
		Vector2D nearestPoint = null;
		double closestSq = Double.MAX_VALUE; 
		for (Wall2D wall : level.getWalls()) {
			double dist = wall.from().distanceSq(levelCursor);
			if (dist < closestSq && dist < snapDistance*snapDistance) {
				nearestPoint = wall.from();
				closestSq = dist;
			}
			dist = wall.to().distanceSq(levelCursor);
			if (dist < closestSq && dist < snapDistance*snapDistance) {
				nearestPoint = wall.to();
				closestSq = dist;
			}
		}

		// If the current segment we're drawing is within snap range and if
		// there are enough segments to make a triangle, then always snap.
		if (isDrawing && drawingPoints.size() > 2
			&& levelToView(levelCursor).distance(levelToView(drawingPoints.get(0))) < snapDistance) {
			nearestPoint = drawingPoints.get(0);
		}
		if (nearestPoint != null) {
			isSnapped = true;
			levelCursor = nearestPoint;
		} else {
			isSnapped = false;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE:
			if (isDrawing && levelCursor != null) {
				addDrawingPoint(levelCursor);
			}
			break;
		case KeyEvent.VK_ESCAPE:
			if (isDrawing) {
				cancelDrawing();
			}
			break;
		case KeyEvent.VK_BACK_SPACE:
			if (isDrawing) {
				drawingPoints.remove(drawingPoints.size() - 1);
				if (drawingPoints.size() == 0) {
					cancelDrawing();
				}
			}
			break;
		case KeyEvent.VK_ENTER:
			if (isDrawing) {
				commitDrawing();
			}
		}

		updateCursorSnap();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		viewport.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		viewport.setCursor(Cursor.getDefaultCursor());
		levelCursor = null;
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		mouseLocation = e.getPoint();
		levelCursor = viewToLevel(mouseLocation);
		updateCursorSnap();
		
		if (e.isPopupTrigger()) {
			JMenuItem item = new JMenuItem("Not implemented yet...");
			JPopupMenu popup = new JPopupMenu();
			popup.add(item);
			popup.show(e.getComponent(), e.getX(), e.getY());
		} else if (e.getButton() == MouseEvent.BUTTON1) {
			isDrawing = true;
			addDrawingPoint(levelCursor);
		}

		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Check if we're selecting a nearby point
		mouseLocation = e.getPoint();
		levelCursor = viewToLevel(mouseLocation);			

		updateCursorSnap();
		
		viewport.repaint();
		
		e.consume();
	}
}
