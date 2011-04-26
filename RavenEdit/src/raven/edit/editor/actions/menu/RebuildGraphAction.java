package raven.edit.editor.actions.menu;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import raven.game.RavenMap;
import raven.game.navigation.NavGraphNode;
import raven.edit.editor.EditorView;
import raven.edit.editor.EditorViewDelegate;
import raven.edit.tools.GraphTool;

public class RebuildGraphAction extends AbstractAction {
	
	private EditorViewDelegate delegate;
	
	public RebuildGraphAction(String text, ImageIcon icon, String description, Integer mnemonic, EditorViewDelegate delegate) {
		super(text, icon);
		
		this.delegate = delegate;
		
		putValue(SHORT_DESCRIPTION, description);
		putValue(MNEMONIC_KEY, mnemonic);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Force a graph rebuild.
		// This appears to only erase all the edges and then connect every
		// node to any node that is within the given max edge length
		System.err.println("Graph rebuild unimplemented!");
		delegate.getView().repaint();
	}
}
