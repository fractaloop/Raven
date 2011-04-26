package raven.edit.editor.actions.menu;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import raven.game.RavenMap;
import raven.game.navigation.NavGraphNode;
import raven.script.RavenScript;
import raven.edit.editor.EditorView;
import raven.edit.editor.EditorViewDelegate;
import raven.edit.tools.GraphTool;

public class SetMaxGraphEdgeLengthAction extends AbstractAction {
	
	private EditorViewDelegate delegate;
	
	private double value;
	
	public SetMaxGraphEdgeLengthAction(EditorViewDelegate delegate) {
		this("Default", null, "Use a default node length.", null, delegate);
		
		this.value = RavenScript.getDouble("DefaultMaxEdgeLength");
	}
	
	public SetMaxGraphEdgeLengthAction(double value, EditorViewDelegate delegate) {
		this(Double.toString(value), null, Double.toString(value), null, delegate);
		
		this.value = value;
	}
	
	public SetMaxGraphEdgeLengthAction(String text, ImageIcon icon, String description, Integer mnemonic, EditorViewDelegate delegate) {
		super(text, icon);
		
		this.delegate = delegate;
		
		putValue(SHORT_DESCRIPTION, description);
		putValue(MNEMONIC_KEY, mnemonic);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		delegate.getGraphBuilder().setMaxEdgeLength(value);
		delegate.getView().repaint();
	}
}
