package raven;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import raven.game.RavenGame;
import raven.game.RavenObject;
import raven.math.Vector2D;
import raven.ui.GameCanvas;
import raven.ui.KeyState;
import raven.utils.LogManager;
import raven.utils.LogManager.LogLevel;

public class Raven extends JFrame implements KeyListener, MouseListener, ComponentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3435740439713124161L;

	private static class RavenHolder {
		public static final Raven INSTANCE = new Raven();
	}

	public static Raven getInstance() {
		return RavenHolder.INSTANCE;
	}
	
	private int width = 700;
	private int height = 700;
	private int framerate = 60;
	
	private static RavenGame game;
	private KeyState keys;
	
	public static void start() {
    	game = new RavenGame();
    	
    	getInstance().gameLoop();
	}
	
	private Raven() {
		log(LogLevel.INFO, "Starting new Raven Game.");
		// Ensure we have the right size
    	width = game.getMap().getSizeX();
    	height = game.getMap().getSizeY();
		
    	log(LogLevel.INFO, "Game dimensions:\t" + width + " x " + height);
    	
		// Get the frame's content and use it for the game
    	JPanel panel = (JPanel)this.getContentPane();
    	panel.setPreferredSize(new Dimension(width, height));
    	panel.setLayout(null);
    	
    	// Setup our canvas and add it
    	GameCanvas.getInstance().addKeyListener(this);
    	GameCanvas.getInstance().addMouseListener(this);
    	GameCanvas.getInstance().addComponentListener(this);
    	panel.add(GameCanvas.getInstance());
    	
    	this.pack();
    	this.setResizable(false);
    	this.setTitle("Raven - " + game.getMap().getName());
    	this.setVisible(true);
    	
    	// Add a window listener so we can close the game if they close the
    	// window
    	this.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent e) {
    			System.exit(0);
    		}
    	});
    	
    	// Listen to input
    	addKeyListener(this);
    	addMouseListener(this);
    	// Force keyState to listen
    	keys = new KeyState();
    	addKeyListener(keys);
    }

	private void log(LogLevel l, String s) {
		LogManager.GetInstance().Log(l, s);
	}
    
    public void gameLoop() {
    	
    	log(LogLevel.INFO, "Starting Main Game Loop");
    	
    	long lastTime = System.nanoTime();
    	
    	while (true) {
    		long currentTime = System.nanoTime();

    		game.update((currentTime - lastTime) * 1.0e-9);
    		
    		// Always dispose the canvas
    		try {
    			GameCanvas.startDrawing(game.getMap().getSizeX(), game.getMap().getSizeY());
    			game.render();
    		} finally {
    			GameCanvas.stopDrawing();
    		}
    		
    		long millisToNextUpdate = Math.max(0, (1000 / framerate) - ((System.nanoTime() - currentTime) / 1000000));
			lastTime = currentTime;
			try {
				Thread.sleep(millisToNextUpdate);
			} catch (InterruptedException e) {
				break;
			}
    	}
    }

	///////////////////
	// Input handling
    
	public static boolean isKeyPressed(int key) {
		return getInstance().keys.isKeyPressed(key);
	}
	
	public static Vector2D getClientCursorPosition() {
		Point location = MouseInfo.getPointerInfo().getLocation();
		Point canvas = GameCanvas.getInstance().getLocationOnScreen();
		return new Vector2D(location.x - canvas.x, location.y - canvas.y);
	}
	
	@Override
	public void mouseClicked(MouseEvent event) {}

	@Override
	public void mouseEntered(MouseEvent event) {}

	@Override
	public void mouseExited(MouseEvent event) {}

	@Override
	public void mousePressed(MouseEvent event) {
		if (event.getButton() == MouseEvent.BUTTON1)
			game.clickLeftMouseButton(new Vector2D(event.getPoint().x, event.getPoint().y));
		else if (event.getButton() == MouseEvent.BUTTON2)
			game.clickRightMouseButton(new Vector2D(event.getPoint().x, event.getPoint().y));		
	}

	@Override
	public void mouseReleased(MouseEvent event) {}

	@Override
	public void keyPressed(KeyEvent event) {}

	@Override
	public void keyReleased(KeyEvent event) {}

	@Override
	public void keyTyped(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			log(LogLevel.INFO, "user pressed ESC to exit game.");
			this.setVisible(false);
			this.dispose();
			break;
		case KeyEvent.VK_P:
			log(LogLevel.INFO, "user pressed P to pause game.");
			game.togglePause();
			break;
		case KeyEvent.VK_1:
			log(LogLevel.INFO, "user pressed 1 to select Blaster on possessed bot.");
			game.changeWeaponOfPossessedBot(RavenObject.BLASTER);
			break;
		case KeyEvent.VK_2:
			log(LogLevel.INFO, "user pressed 2 to select Shotgun on possessed bot.");
			game.changeWeaponOfPossessedBot(RavenObject.SHOTGUN);
			break;
		case KeyEvent.VK_3:
			log(LogLevel.INFO, "user pressed 3 to select Rocket Launcher on possessed bot.");
			game.changeWeaponOfPossessedBot(RavenObject.ROCKET_LAUNCHER);
			break;
		case KeyEvent.VK_4:
			log(LogLevel.INFO, "user pressed 4 to select Railgun on possessed bot.");
			game.changeWeaponOfPossessedBot(RavenObject.RAIL_GUN);
			break;
		case KeyEvent.VK_X:
			log(LogLevel.INFO, "user pressed x to select remove control of any possessed bots.");
			game.exorciseAnyPossessedBot();
			break;
		case KeyEvent.VK_UP:
			log(LogLevel.INFO, "user pressed UP to add a new bot.");
			game.addBots(1);
			break;
		case KeyEvent.VK_DOWN:
			log(LogLevel.INFO, "user pressed DOWN to remove a bot.");
			game.removeBot();
			break;
		}
	}
	
	////////////////
	// Entry point
    
    public static void main(String args[]) {
        Raven.start();
    }

	@Override
	public void componentHidden(ComponentEvent e) { }

	@Override
	public void componentMoved(ComponentEvent e) { }

	@Override
	public void componentResized(ComponentEvent e) {
		width = getContentPane().getWidth();
		height = getContentPane().getHeight();
	}

	@Override
	public void componentShown(ComponentEvent e) { }
}
