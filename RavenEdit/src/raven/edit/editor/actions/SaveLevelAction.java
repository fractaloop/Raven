package raven.edit.editor.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;

import raven.edit.editor.EditorViewDelegate;

public class SaveLevelAction extends AbstractAction {
	private EditorViewDelegate delegate;
	
	public SaveLevelAction(String text, ImageIcon icon, String description, Integer mnemonic, EditorViewDelegate delegate) {
		super(text, icon);
		this.delegate = delegate;
		
		putValue(SHORT_DESCRIPTION, description);
		putValue(MNEMONIC_KEY, mnemonic);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		delegate.doSave();
	}
}
