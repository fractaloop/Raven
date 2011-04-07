/**
 * 
 */
package raven.goals.fuzzy;

import raven.utils.Cloner;

/**
 * @author chester
 *
 */
public class FuzzySetRightShoulder extends FuzzySet {

	private double peak, right, left;
	private static final long serialVersionUID = -3563266193186247032L;


	public FuzzySetRightShoulder(double peak, double leftO, double rightO) {
		super(((peak + rightO) + peak)/2);
		this.peak = peak;
		right = rightO;
		left = leftO;
	}

	/* (non-Javadoc)
	 * @see raven.goals.fuzzy.FuzzyTerm#Clone()
	 */
	@Override
	public FuzzyTerm Clone() {
		return Cloner.Clone(this);
	}
	
	public double CalculateDOM(double val)
	{
	  //test for the case where the left or right offsets are zero
	  //(to prevent divide by zero errors below)
	  if ( ((right == 0.0) && ((peak == val))) ||
	       ((left == 0.0) && ((peak == val))) )
	  {
	    return 1.0;
	  }
	  
	  //find DOM if left of center
	  else if ( (val <= peak) && (val > (peak - left)) )
	  {
	    double grad = 1.0 / left;

	    return grad * (val - (peak - left));
	  }
	  //find DOM if right of center and less than center + right offset
	  else if ( (val > peak) && (val <= (peak + right)) )
	  {
	    return 1.0;
	  }

	  else
	  {
	    return 0;
	  }
	}
}
