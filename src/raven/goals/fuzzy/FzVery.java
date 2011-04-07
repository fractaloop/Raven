/**
 * 
 */
package raven.goals.fuzzy;

import raven.utils.Cloner;

/**
 * @author chester
 *
 */
public class FzVery implements FuzzyTerm{

	private FzSet set;
	
	public FzVery(FzSet baseSet)
	{
		set = baseSet;
	}

	@Override
	public void ClearDOM() {
		set.ClearDOM();
	}

	@Override
	public FuzzyTerm Clone() {
		return Cloner.Clone(this);
	}

	@Override
	public double GetDOM() {
		return set.GetDOM() * set.GetDOM();
	}

	@Override
	public void ORwithDOM(double value) {
		set.ORwithDOM(value * value);
	}
	
	
	
}
