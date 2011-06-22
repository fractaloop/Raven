/**
 * 
 */
package raven.math;


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
		return Math.random() * ((end - start) + start);
	}
}
