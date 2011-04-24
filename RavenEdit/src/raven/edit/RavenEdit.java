package raven.edit;

import raven.edit.editor.EditorViewController;

public class RavenEdit {
	public static void main(String[] args) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new EditorViewController();
            }
        });
	}
}
