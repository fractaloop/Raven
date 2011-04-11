/**
 * 
 */
package raven.goals.fuzzy;

import java.util.ArrayList;
import java.util.List;

import raven.utils.Cloner;

/**
 * @author chester
 *
 */
public class FzAnd extends FzSet {

	private List<FuzzyTerm> terms = new ArrayList<FuzzyTerm>();
	
	private static final long serialVersionUID = 4479794358836523910L;

	public FzAnd(FuzzyTerm condition1, FuzzyTerm condition2, FuzzyTerm condition3, FuzzyTerm condition4){
		terms.add(condition1);
		terms.add(condition2);
		terms.add(condition3);
		terms.add(condition4);
	}
	
	public FzAnd(FuzzyTerm condition1, FuzzyTerm condition2, FuzzyTerm condition3) {
		terms.add(condition1);
		terms.add(condition2);
		terms.add(condition3);
	}
	
	public FzAnd(FuzzyTerm condition1, FuzzyTerm condition2) {
		terms.add(condition1);
		terms.add(condition2);
	}
	
	public FzAnd(FuzzyTerm condition1) {
		terms.add(condition1);
	}
	
	public FuzzyTerm Clone() {
		return Cloner.Clone(this);
	}
	
	public double GetDOM(){
		double smallest = Double.MAX_VALUE;
		
		for (FuzzyTerm term : terms)
		{
		  if (term.GetDOM() < smallest)
		  {
		    smallest = term.GetDOM();
		  }
		}
		return smallest;
	}
	
	public void ClearDOM() {
		for(FuzzyTerm term : terms){
			term.ClearDOM();
		}
	}
	
	public void ORwithDOM(double val){
		for(FuzzyTerm term : terms){
			term.ORwithDOM(val);
		}
	}
}
