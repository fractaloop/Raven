package raven.utils;

public class Regulator {

	/** updatePeriod in nanoseconds */
	private long updatePeriod;
	
	/** time of next next update using System.nanoTime() */
	private long nextUpdateTime;
	
	public Regulator(double updatesPerSecondRequested) {
		// The original implementation had it randomly wait 1 second too
		nextUpdateTime = System.nanoTime() + (long)(Math.random() * 1e9);
		
		if (updatesPerSecondRequested > 0) {
			updatePeriod = (long)(1e9 / updatesPerSecondRequested);
		} else if (updatesPerSecondRequested < 0) {
			updatePeriod = -1;
		}
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
		
		long currentTime = System.nanoTime();
		
		// the number of milliseconds the update period can vary per required
		// update-step. This is here to make sure any multiple clients of this
		// class have their updates spread evenly
		final long updatePeriodVariator = 10000000; // 10ms in nanoseconds
		
		if (currentTime >= nextUpdateTime) {
			// Offset is randomly between -1.0 and 1.0
			double offset = Math.random() * 2.0 - 1.0;
			nextUpdateTime = currentTime + updatePeriod + (long)(offset * updatePeriodVariator);
			
			return true;
		}
		
		return false;
	}
}
