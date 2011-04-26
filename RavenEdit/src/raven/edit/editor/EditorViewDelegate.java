package raven.edit.editor;

import java.awt.event.ComponentListener;

import raven.edit.graph.GraphBuilder;
import raven.edit.tools.EditorTool;
import raven.game.RavenMap;

public interface EditorViewDelegate extends ComponentListener {
	public EditorView getView();
	public RavenMap getLevel();

	public boolean doNewLevel();
	public boolean doSave();
	public boolean doOpen();
	
	public void changeLevel(RavenMap level);
	
	public void makeDirty();
	public void changeTool(EditorTool selectTool);
	
	// Menu items
	void toggleGrid();
	void toggleGridSnap();
	
	// Graph manipulation
	public GraphBuilder getGraphBuilder();
}
