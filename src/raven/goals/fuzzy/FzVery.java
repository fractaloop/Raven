/**
 * 
 */
package raven.goals.fuzzy;

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
		// TODO Auto-generated method stub
		return null;
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
