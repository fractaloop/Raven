package raven.edit.editor;

import raven.edit.tools.EditorTool;
import raven.game.RavenMap;

public interface EditorViewDelegate {
	public EditorView getView();
	public boolean doNewLevel();
	public boolean doSave();
	public boolean doOpen();
	public void changeLevel(RavenMap level);
	
	public void makeDirty();
	public void changeTool(EditorTool selectTool);
}
