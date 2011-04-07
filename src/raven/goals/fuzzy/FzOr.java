/**
 * 
 */
package raven.goals.fuzzy;

import java.util.ArrayList;
import java.util.List;

import raven.utils.Cloner;

/**
 * @author Chet
 *
 */
public class FzOr implements FuzzyTerm {

	List<FuzzyTerm> terms = new ArrayList<FuzzyTerm>();
	
	public FzOr(FuzzyTerm op1, FuzzyTerm op2){
		terms.add(op1);
		terms.add(op2);
	}
	
	public FzOr(FuzzyTerm op1, FuzzyTerm op2, FuzzyTerm op3){
		terms.add(op1);
		terms.add(op2);
		terms.add(op3);
	}
	
	public FzOr(FuzzyTerm op1, FuzzyTerm op2, FuzzyTerm op3, FuzzyTerm op4){
		terms.add(op1);
		terms.add(op2);
		terms.add(op3);
		terms.add(op4);
	}
	
	@Override
	public FuzzyTerm Clone() {
		return Cloner.Clone(this);
	}

	@Override
	public double GetDOM() {
		double largest = Double.MIN_VALUE;
		for(FuzzyTerm term : terms){
			if(term.GetDOM() > largest)
				largest = term.GetDOM();
		}
		return largest;
	}

	/**
	 * Not used in this class
	 */
	@Override
	public void ClearDOM() {}

	/**
	 * Not used in this class
	 */
	@Override
	public void ORwithDOM(double value) {}
}
