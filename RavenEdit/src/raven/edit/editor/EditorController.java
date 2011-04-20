package raven.edit.editor;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import raven.game.RavenMap;
import raven.utils.MapSerializer;

public class EditorController implements EditorViewDelegate {
	JFileChooser fileChooser;
	EditorView view;
	RavenMap currentLevel;
	boolean isDirty;
	
	private class LevelFilter extends FileFilter {

		@Override
		public boolean accept(File file) {
			if (file.isDirectory()) {
				return false;
			}
			
			return file.getName().endsWith(".raven");
		}

		@Override
		public String getDescription() {
			return "Raven levels (*.raven)";
		}
	}
	
	public EditorController() {		
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new LevelFilter());
		
		currentLevel = new RavenMap();
		
		view = new EditorView(currentLevel);
		view.setDelegate(this);
		view.create();		
		
		isDirty = false;
	}

	@Override
	public boolean doNewLevel() {
		if (!doSaveIfDirty())
			return false;
		
		changeLevel(new RavenMap());
		
		return true;
	}
	
	@Override
	public boolean doOpen() {
		// If there have been changes, ask about saving them.
		if (!doSaveIfDirty())
			return false;
		
		int result = fileChooser.showOpenDialog((Component)view);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			RavenMap level;
			try {
				level = new RavenMap();
				level.loadMap(fileChooser.getSelectedFile().getPath());
				changeLevel(level);
				view.updateStatus("Opened level " + level.getPath());
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(view,
						"File " + fileChooser.getSelectedFile().getPath() + " was not found!",
						"File not found", 
						JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(view,
						"Unable to open " + fileChooser.getSelectedFile().getPath() + "\n" + e.getLocalizedMessage(),
						"Unable to open file", 
						JOptionPane.ERROR_MESSAGE);
			}
		}
		
		return true;
	}
	
	/**
	 * Returns true if the save was successful.
	 */
	public boolean doSaveIfDirty() {
		if (isDirty) {
			int confirm = JOptionPane.showConfirmDialog(view,
					"Level has been modified. Would you like to save the changes?", "Confirm save...",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirm == JOptionPane.YES_OPTION) {
				doSave();
			} else if (confirm == JOptionPane.CANCEL_OPTION) {
				return false;
			} // else ignore changes
		}
		
		return true;
	}
	
	@Override
	public boolean doSave() {
		// Ask where to save the file if it doesn't have a filename yet
		if (currentLevel.getPath() == null) {
			fileChooser.setSelectedFile(new File(currentLevel.getName() + ".raven"));
			int result = fileChooser.showSaveDialog((Component)view);
			
			if (result == JFileChooser.APPROVE_OPTION) {
				currentLevel.setPath(fileChooser.getSelectedFile().getPath());
			} else {
				return false;
			}
		}
		isDirty = false;

		// Write it
		try {
			MapSerializer.serializeMapToPath(currentLevel, currentLevel.getPath());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(view,
					"Unable to save " + fileChooser.getSelectedFile().getPath() + "\n" + e.getLocalizedMessage(),
					"Unable to save file", 
					JOptionPane.ERROR_MESSAGE);
		}
		
		view.updateStatus("Level saved to " + currentLevel.getPath());
		
		return true;
	}


	@Override
	public void changeLevel(RavenMap level) {
		isDirty = false;
		currentLevel = level;
		view.setLevel(currentLevel);	
	}


	@Override
	public EditorView getView() {
		return view;
	}


	@Override
	public void makeDirty() {
		isDirty = true;		
	}

}
