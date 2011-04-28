package raven.utils;

public class Regulator {

	/** updatePeriod in seconds */
	private double updatePeriod;
	
	/** time remaining until next update */
	private double nextUpdateTime;
	
	public Regulator(double updatesPerSecondRequested) {
		// The original implementation had it randomly wait 1 second too
		nextUpdateTime = Math.random();
		
		if (updatesPerSecondRequested > 0) {
			updatePeriod = 1 / updatesPerSecondRequested;
		} else if (updatesPerSecondRequested < 0) {
			updatePeriod = -1;
		}
	}
	
	public void update(double delta) {
		nextUpdateTime -= delta;
	}
	
	public boolean isReady() {
		// if a regulator is instantiated with a zero freq then it goes into
		// stealth mode (doesn't regulate)
		if (updatePeriod == 0.0) {
			return true;
		}
		
		// if the regulator is instantiated with a negative freq then it will
		// never allow the code to flow
		if (updatePeriod < 0.0) {
			return false;
		}
		
		// the number of milliseconds the update period can vary per required
		// update-step. This is here to make sure any multiple clients of this
		// class have their updates spread evenly
		final double updatePeriodVariator = 0.010; // 10 mS
		
		if (nextUpdateTime <= 0) {
			// Offset is randomly between -1.0 and 1.0
			double offset = Math.random() * 2.0 - 1.0;
			nextUpdateTime = updatePeriod + offset * updatePeriodVariator;
			
			return true;
		}
		
		return false;
	}
}
