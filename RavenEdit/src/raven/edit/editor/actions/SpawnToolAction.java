package raven.edit.editor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import raven.edit.editor.EditorViewDelegate;
import raven.edit.tools.DoorTool;
import raven.edit.tools.SpawnTool;

public class SpawnToolAction extends AbstractAction {

	private EditorViewDelegate delegate;

	public SpawnToolAction(String text, ImageIcon icon, String description,
			Integer mnemonic, EditorViewDelegate delegate) {
		super(text, icon);
		
		this.delegate = delegate;
		
		putValue(SHORT_DESCRIPTION, description);
		putValue(MNEMONIC_KEY, mnemonic);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		delegate.changeTool(new SpawnTool(delegate.getView()));
	}

}
