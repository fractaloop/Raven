/**
 * 
 */
package raven.goals.fuzzy;

import raven.utils.Cloner;

/**
 * @author chester
 *
 */
public class FzFairly implements FuzzyTerm{

	private FuzzySet set;
	
	public FzFairly(FzSet desirabilitySet){
		set = desirabilitySet.set();
	}

	@Override
	public FuzzyTerm Clone() {
		return Cloner.Clone(this);
	}

	@Override
	public double GetDOM() {
		return Math.sqrt(set.GetDOM());
	}

	@Override
	public void ClearDOM() {
		set.ClearDOM();
	}

	@Override
	public void ORwithDOM(double value) {
		set.ORwithDOM(Math.sqrt(value));
	}
}
