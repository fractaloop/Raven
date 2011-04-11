package raven.goals.fuzzy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuzzyModule {

	public enum DefuzzifyMethod { max_av, centroid };
	private HashMap<String, FuzzyVariable> varMap = new HashMap<String, FuzzyVariable>();
	private List<FuzzyRule> rules = new ArrayList<FuzzyRule>();
	private int numSamples = 15;
	
	public void Fuzzify(String nameOfVariable, double val) {
		if(varMap.containsKey(nameOfVariable)){
			varMap.get(nameOfVariable).Fuzzify(val);
		}
	}

	public double Defuzzify(String key, DefuzzifyMethod method) {
		if(varMap.containsKey(key)){
			SetConfidencesOfConsequentsToZero();
			for(FuzzyRule rule : rules){
				rule.Calculate();
			}
			switch(method){
			case centroid:
				return varMap.get(key).DefuzzifyCentroid(numSamples);
			case max_av:
				return varMap.get(key).DefuzzifyMaxAv();
			}
		}
		return 0;
	}

	public FuzzyVariable CreateFLV(String varName) {
		FuzzyVariable newGuy = new FuzzyVariable();
		varMap.put(varName, newGuy);
		return newGuy;
	}

	public void AddRule(FuzzyTerm antecedent, FuzzyTerm consequence) {
		rules.add(new FuzzyRule(antecedent, consequence));
	}
	
	private void SetConfidencesOfConsequentsToZero(){
		for(FuzzyRule rule : rules){
			rule.SetConfidenceOfConsequentToZero();
		}
	}
	
	public void WriteAllDOMs(){
		System.out.print("\n\n");
		for(String key : varMap.keySet()){
			System.out.println("\n---------------------------" + key);
			varMap.get(key).WriteDOMs();
			System.out.println();
		}
	}

}
