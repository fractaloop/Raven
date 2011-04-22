package raven.edit.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.vecmath.Vector2f;

import raven.edit.editor.actions.*;
import raven.edit.tools.EditorTool;
import raven.game.RavenMap;
import raven.math.Vector2D;
import raven.math.Wall2D;

public class EditorView extends JFrame implements ViewportDelegate {
	private RavenMap level;
	private EditorViewDelegate delegate;
	private Viewport viewport;
	private JLabel statusBar;
	
	// Actions
	private NewLevelAction newLevelAction;
	private OpenLevelAction openLevelAction;
	private SaveLevelAction saveLevelAction;
	private SelectToolAction selectToolAction;
	private WallToolAction wallToolAction;
	private DoorToolAction doorToolAction;
	private HealthToolAction healthToolAction;
	private GraphToolAction graphToolAction;
	private RocketToolAction rocketToolAction;
	private ShotgunToolAction shotgunToolAction;
	private RailgunToolAction railgunToolAction;
	
	public EditorView(RavenMap theLevel) {
		level = theLevel;
	}
	
	public void create() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());
		
		// First lets make the actions we'll want to use
		createActions();
		// Menus
		createMenu();
		// Lets add a toolbar
		createToolbar();
		// Add another toolbar for the inspector
		createInspector();
		// Add a statusbar
		statusBar = new JLabel("Initialized.");
		statusBar.setPreferredSize(new Dimension(100, 16));
		this.add(statusBar, BorderLayout.SOUTH);
		
		// Add a viewport
		viewport = new Viewport(level);
		viewport.setDelegate(this);
		this.add(viewport);
		
		this.pack();
        this.setTitle("Editing"); // TODO add names to maps!
        this.setVisible(true);
	}
	
	private void createActions() {
		newLevelAction = new NewLevelAction("New",
											new ImageIcon("images/new.png"),
											"Create an empty level.",
											new Integer(KeyEvent.VK_N),
											delegate);
		openLevelAction = new OpenLevelAction("Open",
											new ImageIcon("images/open.png"),
											"Open an existing level.",
											new Integer(KeyEvent.VK_O),
											delegate);
		saveLevelAction = new SaveLevelAction("Save",
											new ImageIcon("images/save.png"),
											"Save the current level.",
											new Integer(KeyEvent.VK_S),
											delegate);
		
		selectToolAction = new SelectToolAction("Select",
											new ImageIcon("images/select.png"),
											"Select objects within the level.",
											new Integer(KeyEvent.VK_1),
											delegate);
		wallToolAction = new WallToolAction("Add walls",
											new ImageIcon("images/wall.png"),
											"Draw new walls into the level.",
											new Integer(KeyEvent.VK_2),
											delegate);
		
		doorToolAction = new DoorToolAction("Add doors",
											new ImageIcon("images/door.png"),
											"Draw new doors into the level.",
											new Integer(KeyEvent.VK_3),
											delegate);
		graphToolAction = new GraphToolAction("Add graph node",
											new ImageIcon("images/graph.png"),
											"Insert a new graph node into the navigation network.",
											new Integer(KeyEvent.VK_4),
											delegate);
		healthToolAction = new HealthToolAction("Add a health spanwer.",
											new ImageIcon("images/health.png"),
											"Add locations where health will respawn.",
											new Integer(KeyEvent.VK_5),
											delegate);
		rocketToolAction = new RocketToolAction("Add a rocket launcher spanwer",
											new ImageIcon("images/rocket.png"),
											"Add locations where rocket launchers will respawn.",
											new Integer(KeyEvent.VK_6),
											delegate);
		shotgunToolAction = new ShotgunToolAction("Add a shotgun spawner",
											new ImageIcon("images/shotgun.png"),
											"Add locations where shotguns will respawn.",
											new Integer(KeyEvent.VK_7),
											delegate);
		railgunToolAction = new RailgunToolAction("Add a railgun spawner",
											new ImageIcon("images/railgun.png"),
											"Add locations where railguns will respawn.",
											new Integer(KeyEvent.VK_8),
											delegate);
		
		
	}
	
	private void createMenu() {
		// TODO Auto-generated method stub
		
	}
	
	private void createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);

		// And add some buttons
		ArrayList<JButton> buttons = new ArrayList<JButton>();
		buttons.add(new JButton(newLevelAction));
		buttons.add(new JButton(openLevelAction));
		buttons.add(new JButton(saveLevelAction));
		buttons.add(null);
		buttons.add(new JButton(selectToolAction));
		buttons.add(new JButton(graphToolAction));
		buttons.add(null);
		buttons.add(new JButton(wallToolAction));
		buttons.add(new JButton(doorToolAction));
		buttons.add(null);
		buttons.add(new JButton(healthToolAction));
		buttons.add(new JButton(rocketToolAction));
		buttons.add(new JButton(shotgunToolAction));
		buttons.add(new JButton(railgunToolAction));
		
		// Icons only if there is one
		for (int i = 0; i < buttons.size(); i++) {
			JButton button = buttons.get(i);
			if (button == null) {
				toolbar.addSeparator();
			} else {
				if (button.getIcon() != null) {
					button.setText("");
				}
				toolbar.add(button);
			}
		}

		this.add(toolbar, BorderLayout.NORTH);
	}

	private void createInspector() {
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(true);
		// And add some buttons
		JTextField textField = new JTextField(8);
		toolbar.add(textField);
		this.add(toolbar, BorderLayout.EAST);
	}

	public void setDelegate(EditorViewDelegate delegate) {
		this.delegate = delegate;
	}

	////////////////
	// Accessors
	
	public synchronized RavenMap getLevel() {
		return level;
	}

	public synchronized void setLevel(RavenMap level) {
		// TODO update the viewport's level!
		this.level = level;
		viewport.setLevel(level);
	}

	//////////////////////
	// Viewport delegate
	@Override
	public void updateStatus(String status) {
		statusBar.setText(status);		
	}

	@Override
	public void addWalls(Vector2D[] segments) {
		for (int i = 0; i < segments.length - 1; i++) {
			level.addWall(segments[i], segments[i+1]);
		}
		
		delegate.makeDirty();
	}

	public void setTool(EditorTool newTool) {
		
	}

}
