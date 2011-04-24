package raven.edit.tools;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import raven.edit.editor.ViewportDelegate;
import raven.game.RavenBot;
import raven.game.RavenMap;
import raven.game.triggers.Trigger;
import raven.game.triggers.TriggerWeaponGiver;
import raven.math.Vector2D;


public class ShotgunTool extends EditorTool {

	private Vector2D shotSpawnPoint;
	
	public ShotgunTool(ViewportDelegate delegate) {
		super(delegate);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void paintComponent(Graphics g) {
		if(shotSpawnPoint != null) {
			
		}
	}
	
	public void mouseClicked( MouseEvent e) {
		shotSpawnPoint = viewToLevel(e.getPoint());
		delegate.addTrigger(new TriggerWeaponGiver(shotSpawnPoint, 5));
	}
}
