package raven.edit.editor.actions.menu;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

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
		if (delegate.getLevel().getNavGraph().numNodes() == 1) {
			delegate.getGraphBuilder().growFromSeed();
			delegate.getView().updateStatus("Graph grew to " + delegate.getLevel().getNavGraph().numNodes() + " nodes with " + delegate.getLevel().getNavGraph().numEdges() + " edges.");
			delegate.getView().repaint();
		} else if (delegate.getLevel().getNavGraph().numNodes() > 1) {
			JOptionPane.showMessageDialog(delegate.getView(),
										"There are too many graph nodes in this map.\nTo regrow a new graph, reduce the graph to only 1 node.",
										"Error: Too many graph nodes",
										JOptionPane.ERROR_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(delegate.getView(),
					"There are too few graph nodes in this map.\nTo regrow a new graph, increase the graph to only 1 node.",
					"Error: Too few graph nodes",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
