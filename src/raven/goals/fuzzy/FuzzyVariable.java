package raven.goals.fuzzy;

import java.util.HashMap;
import java.util.Map;

import raven.goals.fuzzy.FzSet;

/**
 * This class should never be constructed directly.  Instead, construct it via a call to FuzzyModule.CreateFLV()!
 * @author chester
 *
 */
public class FuzzyVariable {

	private Map<String, FuzzySet> MemberSets = new HashMap<String, FuzzySet>();
	private double minRange = 0, maxRange = 0;
	
	private void AdjustRangeToFit(double min, double max){
		  if (min < minRange) minRange = min;
		  if (max > maxRange) maxRange = max;
	}
	
	private FzSet FzSet(FuzzySet fuzzySet) {
		return new FzSet(fuzzySet);
	}
	
	  //the following methods create instances of the sets named in the method
	  //name and add them to the member set map. Each time a set of any type is
	  //added the m_dMinRange and m_dMaxRange are adjusted accordingly. All of the
	  //methods return a proxy class representing the newly created instance. This
	  //proxy set can be used as an operand when creating the rule base.
	
	public FzSet AddLeftShoulderSet(String name, double minBound, double peak, double maxBound) {
		FuzzySetLeftShoulder value = new FuzzySetLeftShoulder(peak, peak-minBound, maxBound-peak);  
		MemberSets.put(name, value);

		  //adjust range if necessary
		  AdjustRangeToFit(minBound, maxBound);

		  return FzSet(MemberSets.get(name));
	}

	public FzSet AddTriangularSet(String name, double minBound, double peak, double maxBound) {
		FuzzySetTriangle value = new FuzzySetTriangle(peak, peak - minBound, maxBound - peak);
		MemberSets.put(name, value);
		  //adjust range if necessary
		  AdjustRangeToFit(minBound, maxBound);

		  return FzSet(MemberSets.get(name));
	}

	public FzSet AddRightShoulderSet(String name, double minBound, double peak, double maxBound) {
		FuzzySetRightShoulder value = new FuzzySetRightShoulder(peak, peak - minBound, maxBound- peak);
		MemberSets.put(name, value);
		AdjustRangeToFit(minBound, maxBound);
		return FzSet(MemberSets.get(name));
	}
	
	public FzSet AddSingletonSet(String name, double minBound, double peak, double maxBound){
		FuzzySetSingleton value = new FuzzySetSingleton(peak, peak - minBound, maxBound- peak);
		MemberSets.put(name, value);
		AdjustRangeToFit(minBound, maxBound);
		return FzSet(MemberSets.get(name));
	}
	
	/**
	 * Fuzzify a variable by calculating the DOM in each subset.
	 */
	public void Fuzzify(double val) {
		  //make sure the value is within the bounds of this variable
		  assert ( (val >= minRange) && (val <= maxRange) ) : "<FuzzyVariable::Fuzzify>: value out of range";

		  //for each set in the FuzzyVariable calculate the DOM for the given value
		  for(FuzzySet fSet : MemberSets.values()) {
			  fSet.SetDOM(fSet.CalculateDOM(val));
		  }
	}
	
	/**
	 * Defuzzifies the value by averaging the maxima of the sets that have fired
	 *
	 * OUTPUT = sum (maxima * DOM) / sum (DOMs) 
	 */
	public double DefuzzifyMaxAv() {
		double bottom = 0.0;
		double top    = 0.0;
		
		for (FuzzySet fSet : MemberSets.values()) {
		  bottom += fSet.GetDOM();
		  top += fSet.GetRepresentativeVal() * fSet.GetDOM();
		}
	
		//make sure bottom is not equal to zero
		if (0 == bottom) return 0.0;
	
		return top / bottom; 
	}
	
	
	
	/**
	 * Defuzzifies a variable based on the centroid method.
	 * 
	 * @param numberOfSamples
	 * @return
	 */
	public double DefuzzifyCentroid(int numberOfSamples) 
	{
		  //calculate the step size
		  double StepSize = (maxRange - minRange)/(double)numberOfSamples;

		  double TotalArea    = 0.0;
		  double SumOfMoments = 0.0;

		  //step through the range of this variable in increments equal to StepSize
		  //adding up the contribution (lower of CalculateDOM or the actual DOM of this
		  //variable's fuzzified value) for each subset. This gives an approximation of
		  //the total area of the fuzzy manifold.(This is similar to how the area under
		  //a curve is calculated using calculus... the heights of lots of 'slices' are
		  //summed to give the total area.)
		  //
		  //in addition the moment of each slice is calculated and summed. Dividing
		  //the total area by the sum of the moments gives the centroid. (Just like
		  //calculating the center of mass of an object)
		  for (int samp=1; samp<=numberOfSamples; ++samp)
		  {
		    //for each set get the contribution to the area. This is the lower of the 
		    //value returned from CalculateDOM or the actual DOM of the fuzzified 
		    //value itself   
		    for (FuzzySet fSet : MemberSets.values())
		    {
		      double contribution = 
		          MinOf(fSet.CalculateDOM(minRange + samp * StepSize), fSet.GetDOM());

		      TotalArea += contribution;

		      SumOfMoments += (minRange + samp * StepSize)  * contribution;
		    }
		  }

		  //make sure total area is not equal to zero
		  if (0 == TotalArea) return 0.0;
		  
		  return (SumOfMoments / TotalArea);
		}	
	
	private double MinOf(double a, double b){
		if(a > b) { 
			return a; 
		} else { 
			return b;
		}
	}

	public void WriteDOMs() {
		for(String key : MemberSets.keySet()){
			System.out.println(key + " is " + MemberSets.get(key).GetDOM());
		}
		System.out.println("Min Range: " + minRange);
		System.out.println("Max Range: " + maxRange);
	}
}
