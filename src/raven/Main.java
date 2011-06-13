package raven;

import java.awt.Dimension;

import raven.game.RavenGame;
import raven.ui.GameCanvas;
import raven.ui.RavenUI;
import raven.utils.Log;
import raven.utils.Log.Level;
import raven.utils.MapLoadedException;

public class Main {
	private static RavenUI ui;
	private static RavenGame game;
	
    public static void main(String args[]) {
    	Log.setLevel(Level.DEBUG);
    	
    	game = new RavenGame();
    	ui = new RavenUI(game);
    	
    	gameLoop();
	}
    
	//////////////////////////////////////////////////////////////////////////
	// Game simulation

	private static void gameLoop() {
    	
    	Log.info("raven", "Starting game...");
    	
    	long lastTime = System.nanoTime();
    	
    	while (true) {
    		// TODO Resize UI if the map changes!
    		boolean loadedMap = false;

    		long currentTime = System.nanoTime();

    		try{
    		game.update((currentTime - lastTime) * 1.0e-9);
    		}
    		catch (MapLoadedException e){
    			loadedMap = true;
    			ui.dispose();
    	    	ui = new RavenUI(game);

    		}
    		// Always dispose the canvas
    		try {
    			GameCanvas.startDrawing(game.getMap().getSizeX(), game.getMap().getSizeY(), loadedMap);
    			loadedMap = false;
    			game.render();
    		} finally {
    			GameCanvas.stopDrawing();
    		}
    		
    		long millisToNextUpdate = Math.max(0, (1000 / 60) - ((System.nanoTime() - currentTime) / 1000000));
			lastTime = currentTime;
			try {
				Thread.sleep(millisToNextUpdate);
			} catch (InterruptedException e) {
				break;
			}
    	}
    }


}
