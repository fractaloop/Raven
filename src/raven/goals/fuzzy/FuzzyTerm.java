package raven.goals.fuzzy;

public interface FuzzyTerm {
	
	/**
	 * Create a hard copy of this FuzzyTerm
	 * @return
	 */
	public FuzzyTerm Clone();
	
	/**
	 * Returns the Degree of Membership of this FuzzyTerm
	 * @return
	 */
	public double GetDOM();
	
	/**
	 * Clears the Degree of membership from this FuzzyTerm
	 */
	public void ClearDOM();
	
	/**
	 * Updates the DOM of the consequent when a rule fires.
	 * @param value
	 */
	public void ORwithDOM(double value);
	
}
