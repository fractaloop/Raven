/**
 * 
 */
package raven.goals.fuzzy;

/**
 * @author chester
 *
 */
public class FuzzySetLeftShoulder extends FuzzySet {

	private double peak, right, left;
	private static final long serialVersionUID = -5897724469588203295L;

	public FuzzySetLeftShoulder(double peak, double left, double right) {
		super(((peak + left) + peak)/2);
		this.peak = peak;
		this.right = right;
		this.left = left;
	}

	/* (non-Javadoc)
	 * @see raven.goals.fuzzy.FuzzyTerm#Clone()
	 */
	@Override
	public FuzzyTerm Clone() {
		// TODO Auto-generated method stub
		return null;
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

		  //find DOM if right of center
		  else if ( (val >= peak) && (val < (peak + right)) )
		  {
		    double grad = 1.0 / -right;

		    return grad * (val - peak) + 1.0;
		  }

		  //find DOM if left of center
		  else if ( (val < peak) && (val >= (peak - left)) )
		  {
		    return 1.0;
		  }

		  //out of range of this FLV, return zero
		  else
		  {
		    return 0.0;
		  }

		}
}
