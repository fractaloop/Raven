/**
 * 
 */
package raven.goals.fuzzy;

import raven.utils.Cloner;

/**
 * @author chester
 *
 */
public class FuzzySetTriangle extends FuzzySet {

	private double midPoint, leftOffset, rightOffset;	
	private static final long serialVersionUID = 2416795701517500624L;

	public FuzzySetTriangle(double mid, double left, double right) {
		super(mid);
		midPoint = mid;
		leftOffset = left;
		rightOffset = right;
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
		  //test for the case where the triangle's left or right offsets are zero
		  //(to prevent divide by zero errors below)
		  if ( ((rightOffset == 0.0) && (midPoint == val)) ||
		       ((leftOffset == 0.0) && (midPoint == val)) )
		  {
		    return 1.0;
		  }

		  //find DOM if left of center
		  if ( (val <= midPoint) && (val >= (midPoint - leftOffset)) )
		  {
		    double grad = 1.0 / leftOffset;

		    return grad * (val - (midPoint - leftOffset));
		  }
		  //find DOM if right of center
		  else if ( (val > midPoint) && (val < (midPoint + rightOffset)) )
		  {
		    double grad = 1.0 / -rightOffset;

		    return grad * (val - midPoint) + 1.0;
		  }
		  //out of range of this FLV, return zero
		  else
		  {
		    return 0.0;
		  }
		}
}
