/**
 * 
 */
package raven.goals.fuzzy;

/**
 * @author Chet
 *
 */
public class FuzzyRule {
	
	// Usually a composite of several sets and operators
	private FuzzyTerm antecedent;
	
	// Usually a single FuzzySet, but can be several ANDed together
	private FuzzyTerm consequence;
	
	public FuzzyRule(FuzzyTerm ant, FuzzyTerm con){
		antecedent = ant;
		consequence = con;
	}
	
	public void SetConfidenceOfConsequentToZero(){
		consequence.ClearDOM();
	}
	
	public void Calculate(){
		consequence.ORwithDOM(antecedent.GetDOM());
	}
}
