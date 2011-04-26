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

public class GrowFromSeedAction extends AbstractAction {
	
	private EditorViewDelegate delegate;
	
	public GrowFromSeedAction(String text, ImageIcon icon, String description, Integer mnemonic, EditorViewDelegate delegate) {
		super(text, icon);
		
		this.delegate = delegate;
		
		putValue(SHORT_DESCRIPTION, description);
		putValue(MNEMONIC_KEY, mnemonic);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Flood fill new graph nodes if there is only 1 node in the graph.
		System.err.println("Grow from seed unimplemented!");
		delegate.getView().repaint();
	}
}
