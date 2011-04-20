package raven.edit.editor.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;
import javax.swing.JFileChooser.*;
import javax.swing.filechooser.FileFilter;

import raven.edit.editor.EditorView;
import raven.edit.editor.EditorViewDelegate;

public class OpenLevelAction extends AbstractAction {
	private EditorViewDelegate delegate;
	
	public OpenLevelAction(String text, ImageIcon icon, String description, Integer mnemonic, EditorViewDelegate delegate) {
		super(text, icon);
		this.delegate = delegate;
		
		putValue(SHORT_DESCRIPTION, description);
		putValue(MNEMONIC_KEY, mnemonic);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		delegate.doOpen();
	}
}
