package raven.edit.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
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
											new Integer(KeyEvent.VK_K),
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
		
		// Icons only if there is one
		for (int i = 0; i < buttons.size(); i++) {
			JButton button = buttons.get(i);
			System.out.print(i);
			if (button == null) {
				toolbar.addSeparator();
			} else {
				if (button.getIcon() != null) {
					System.out.println("\tAdding " + button.getText() + ".");
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
