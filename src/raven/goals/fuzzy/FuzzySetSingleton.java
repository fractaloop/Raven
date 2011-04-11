/**
 * 
 */
package raven.goals.fuzzy;

import raven.utils.Cloner;

/**
 * @author chester
 *
 */
public class FuzzySetSingleton extends FuzzySet {

	private double midPoint, leftOffset, rightOffset;
	private static final long serialVersionUID = 6467990518325560801L;

	public FuzzySetSingleton(double mid, double left, double right) {
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
	
	public double CalculateDOM(double val){
		  if ( (val >= midPoint-leftOffset) &&
			       (val <= midPoint+rightOffset) )
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
