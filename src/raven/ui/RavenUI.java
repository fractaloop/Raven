package raven.ui;

import java.awt.CheckboxMenuItem;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import raven.game.RavenGame;
import raven.game.RavenObject;
import raven.game.RavenUserOptions;
import raven.math.Vector2D;
import raven.utils.Log;
import raven.utils.Log.Level;

public class RavenUI extends JFrame implements KeyListener, MouseListener, ComponentListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3435740439713124161L;

	private int width = 700;
	private int height = 700;
	private int framerate = 60;

	private RavenGame game;
	private KeyState keys;
	
	private Action loadLevelAction;
	
	public RavenUI(RavenGame game) {
		super("Raven");
		
		Log.info("gui", "Initializing...");
		// Ensure we have the right size
		this.game = game;
		width = game.getMap().getSizeX();
    	height = game.getMap().getSizeY();
		
    	Log.info("raven", "Map dimensions: " + width + " x " + height);
    	
		// Get the frame's content and use it for the game
    	JPanel panel = (JPanel)this.getContentPane();
    	panel.setPreferredSize(new Dimension(width, height));
    	panel.setLayout(null);

    	// Setup our canvas and add it
    	GameCanvas.getInstance().addKeyListener(this);
    	GameCanvas.getInstance().addMouseListener(this);
    	GameCanvas.getInstance().addComponentListener(this);
    	panel.add(GameCanvas.getInstance());
    	
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
    	requestFocus();
    	
    	// Set up the menus
    	createMenu();

    	// All done. Show it!
    	this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	this.pack();
    	this.setResizable(false);
    	this.setTitle("Raven - " + game.getMap().getName());
    	this.setVisible(true);
	}
	
	private void createMenu() {
		JMenu menu, subMenu;
		JMenuItem menuItem;
		JCheckBoxMenuItem checkedMenuItem;
		
		// Create the menu bar
		JMenuBar menuBar = new JMenuBar();
		
		// Game
		menu = new JMenu("Game");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);
		
		// Load map
		menuItem = new JMenuItem("Load map");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.META_MASK));
		menuItem.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				String filename = chooseMapFile();
				if (filename != null)
					game.switchToMap(filename);
			}
		});
		menu.add(menuItem);
		// Add bot
		menuItem = new JMenuItem("Add bot");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
		menuItem.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				game.changeBotCount(1);
			}
		});
		menu.add(menuItem);
		// Remove bot
		menuItem = new JMenuItem("Load map");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
		menuItem.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				game.removeBot();
				
			}
		});
		menu.add(menuItem);
		// Toggle pause
		checkedMenuItem = new JCheckBoxMenuItem("Pause game");
		checkedMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0));
		checkedMenuItem.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				((JCheckBoxMenuItem)e.getSource()).setSelected(game.togglePause());
			}
		});
		menu.add(checkedMenuItem);
		
		// Navigation
		menu = new JMenu("Navigation");
		menu.setMnemonic(KeyEvent.VK_N);
		menuBar.add(menu);
		// Show graph
		checkedMenuItem = new JCheckBoxMenuItem("Show graph");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showGraph"));
		menu.add(checkedMenuItem);
		// Display indices
		checkedMenuItem = new JCheckBoxMenuItem("Show indices");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showNodeIndices"));
		menu.add(checkedMenuItem);
		// Smooth paths (quick)
		checkedMenuItem = new JCheckBoxMenuItem("Smooth paths (quick)");
		checkedMenuItem.addActionListener(BuildToggleUserAction("smoothPathsQuick"));
		menu.add(checkedMenuItem);
		// Smooth paths (precise)
		checkedMenuItem = new JCheckBoxMenuItem("Smooth paths (precise)");
		checkedMenuItem.addActionListener(BuildToggleUserAction("smoothPathsPrecise"));
		menu.add(checkedMenuItem);

		// General bot info
		menu = new JMenu("General bot info");
		menu.setMnemonic(KeyEvent.VK_G);
		menuBar.add(menu);
		// Show IDs
		checkedMenuItem = new JCheckBoxMenuItem("Show bot IDs");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showBotIDs"));
		menu.add(checkedMenuItem);
		// Show health
		checkedMenuItem = new JCheckBoxMenuItem("Show health");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showBotHealth"));
		menu.add(checkedMenuItem);
		// Show scores
		checkedMenuItem = new JCheckBoxMenuItem("Show score");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showScore"));
		menu.add(checkedMenuItem);
		
		// Selected bot info
		menu = new JMenu("Selected bot info");
		menu.setMnemonic(KeyEvent.VK_B);
		menuBar.add(menu);
		// Show target (boxed in red)
		checkedMenuItem = new JCheckBoxMenuItem("Show target (boxed in red)");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showTargetOfSelectedBot"));
		menu.add(checkedMenuItem);
		// Show sensed opponents (boxed in orange)
		checkedMenuItem = new JCheckBoxMenuItem("Show sensed opponents (boxed in orange)");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showOpponentsSensedBySelectedBot"));
		menu.add(checkedMenuItem);
		// Only show opponents in vision
		checkedMenuItem = new JCheckBoxMenuItem("Only show opponents in vision");
		checkedMenuItem.addActionListener(BuildToggleUserAction("onlyShowBotsInTargetsFOV"));
		menu.add(checkedMenuItem);
		// Show goal queue
		checkedMenuItem = new JCheckBoxMenuItem("Show goal queue");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showGoalsOfSelectedBot"));
		menu.add(checkedMenuItem);
		// Show path
		checkedMenuItem = new JCheckBoxMenuItem("Show path");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showPathOfSelectedBot"));
		menu.add(checkedMenuItem);
		// Show path
		checkedMenuItem = new JCheckBoxMenuItem("Show feelers");
		checkedMenuItem.addActionListener(BuildToggleUserAction("showFeelersOfSelectedBot"));
		menu.add(checkedMenuItem);
		
		this.setJMenuBar(menuBar);
	}

	private AbstractAction BuildToggleUserAction(final String option) {
		return new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Field field = RavenUserOptions.class.getField(option);
					field.setBoolean(field, !field.getBoolean(field));
				} catch (ClassCastException ex) {
					Log.error("gui", "Ooops! Option " + option + " is not a toggle!");
				} catch (SecurityException e1) {
					Log.error("gui", "Security exception when changing option " + option + ".");
				} catch (NoSuchFieldException e1) {
					Log.error("gui", "Option " + option + " does not exist.");
				} catch (IllegalArgumentException ex) {
					Log.error("gui", "Illegal argument exception changing option " + option + ".");
				} catch (IllegalAccessException ex) {
					Log.error("gui", "Access exception changing option " + option + ".");
				} 
				
			}
		};
	}
	

	/** Open a Swing file chooser to pick a Raven map */
	private String chooseMapFile() {
		JFileChooser chooser = new JFileChooser(new File("./maps"));
		chooser.setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".raven");
			}

			@Override
			public String getDescription() {
				return "Raven levels (*.raven)";
			}
		});
		int chooseResult = chooser.showOpenDialog(null);
		
		if (chooseResult == JFileChooser.APPROVE_OPTION) {
			return chooser.getSelectedFile().getPath();
		} else {
			return null;
		}
	}

	// Component overrides so we can track window size
	@Override public void componentHidden(ComponentEvent e) { }
	@Override public void componentMoved(ComponentEvent e) { }
	@Override public void componentShown(ComponentEvent e) { }

	@Override
	public void componentResized(ComponentEvent e) {
		width = getContentPane().getWidth();
		height = getContentPane().getHeight();
	}

	//////////////////////////////////////////////////////////////////////////
	// Input handling
	
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
		if (SwingUtilities.isLeftMouseButton(event))
			game.clickLeftMouseButton(new Vector2D(event.getPoint().x, event.getPoint().y));
		else if (SwingUtilities.isRightMouseButton(event))
			game.clickRightMouseButton(new Vector2D(event.getPoint().x, event.getPoint().y), (event.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0 ? true : false);		
	}

	@Override
	public void mouseReleased(MouseEvent event) {}

	@Override
	public void keyReleased(KeyEvent event) {}

	@Override
	public void keyPressed(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.VK_ESCAPE:
			Log.info("raven", "Exiting...");
			this.setVisible(false);
			System.exit(0);
			break;
		case KeyEvent.VK_1:
			game.changeWeaponOfPossessedBot(RavenObject.BLASTER);
			break;
		case KeyEvent.VK_2:
			game.changeWeaponOfPossessedBot(RavenObject.SHOTGUN);
			break;
		case KeyEvent.VK_3:
			game.changeWeaponOfPossessedBot(RavenObject.ROCKET_LAUNCHER);
			break;
		case KeyEvent.VK_4:
			game.changeWeaponOfPossessedBot(RavenObject.RAIL_GUN);
			break;
		case KeyEvent.VK_X:
			game.exorciseAnyPossessedBot();
			break;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}