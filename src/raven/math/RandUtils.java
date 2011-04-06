/**
 * 
 */
package raven.math;

import java.util.Random;

/**
 * @author chester
 *
 */
public class RandUtils {

	/**
	 * Generates a random double from the start to the end provided, exclusive.
	 * @param start
	 * @param end
	 * @return
	 */
	public static double RandInRange(double start, double end)
	{
		Random rand = new Random(System.nanoTime());
		return rand.nextDouble() * end;
	}
}
