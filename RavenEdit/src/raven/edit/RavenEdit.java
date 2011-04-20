package raven.edit;

import raven.edit.editor.EditorController;

public class RavenEdit {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EditorController();
            }
        });
	}
}
