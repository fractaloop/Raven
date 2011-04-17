/**
 * 
 */
package raven.goals.fuzzy;


/**
 * @author chester
 *
 */
public abstract class FuzzySet implements java.io.Serializable, FuzzyTerm{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8717600597820031571L;

	/**
	 * Each FuzzySet has a Degree of Membership.
	 */
	double DOM = 0;

  /**
   * this is the maximum of the set's membership function. For instance, if
   * the set is triangular then this will be the peak point of the triangular.
   * if the set has a plateau then this value will be the mid point of the 
   * plateau. This value is set in the constructor to avoid run-time
   * calculation of mid-point values.
   *
   */
	double RepresentativeValue = 0;
	
	/**
	 * NOTE: NEVER create this directly!  Use a FzSet instead!
	 * @param repVal
	 */
	public FuzzySet(double repVal){
		DOM = 0.0;
		RepresentativeValue = repVal;
	}
	
	/**
	 * NEVER CREATE THIS DIRECTLY
	 */
	public FuzzySet() {}

	/**
	 * return the degree of membership in this set of the given value. NOTE,
	 * this does not set m_dDOM to the DOM of the value passed as the parameter.
	 * This is because the centroid defuzzification method also uses this method
	 * to determine the DOMs of the values it uses as its sample points.
	 */
	public double CalculateDOM(double val) { return 0; }
	
	//if this fuzzy set is part of a consequent FLV, and it is fired by a rule 
	//then this method sets the DOM (in this context, the DOM represents a
	//confidence level)to the maximum of the parameter value or the set's 
	//existing m_dDOM value
	public void ORwithDOM(double val){if (val > DOM) DOM = val;}
	
	//accessor methods
	public double GetRepresentativeVal() { return RepresentativeValue; }
	
	public void ClearDOM() { DOM = 0.0; }
	
	public double GetDOM() { return DOM; }
	
	public void SetDOM(double val)
	{
		assert ((val <=1) && (val >= 0)) : "Incorrect value passed to FuzzySet.SetDOM()";
		DOM = val;
	}
}
