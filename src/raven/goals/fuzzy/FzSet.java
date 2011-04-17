package raven.goals.fuzzy;

import java.io.Serializable;

import raven.utils.Cloner;

public class FzSet implements FuzzyTerm, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2454603820964493112L;
	
	private FuzzySet set;
	
	/**
	 * NEVER CREATE THIS DIRECTLY
	 */
	public FzSet() {}
	
	public FzSet(FuzzySet fs){
		set = fs;
	}
	
	@Override
	public void ClearDOM() {
		set.ClearDOM();
	}

	@Override
	public FuzzyTerm Clone() {
		// TODO: Implement Serializable on all Fuzzy classes so we can clone by serializing the 
		// held set and serializing it into a new object.
	      return Cloner.Clone(set);
	}

	@Override
	public double GetDOM() {
		return set.GetDOM();
	}

	@Override
	public void ORwithDOM(double value) {
		set.ORwithDOM(value);		
	}
	
	public FuzzySet set() { return set; }
	
}
