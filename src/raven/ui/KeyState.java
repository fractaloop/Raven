package raven.ui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.TreeSet;

public class KeyState implements KeyListener {
	private TreeSet<Integer> keys;
	
	public KeyState() {
		keys = new TreeSet<Integer>();
	}
	
	public boolean isKeyPressed(int key) {
		return keys.contains(key);
	}

	@Override
	public void keyPressed(KeyEvent event) {
		keys.add(event.getKeyCode());
		
	}

	@Override
	public void keyReleased(KeyEvent event) {
		keys.remove(event.getKeyCode());
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}
}
