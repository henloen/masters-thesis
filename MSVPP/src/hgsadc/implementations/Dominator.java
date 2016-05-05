package hgsadc.implementations;

import java.util.ArrayList;

import hgsadc.Individual;
import hgsadc.Utilities;

public class Dominator {

	boolean minimizeCost, maximizePersistence, maximizeRobustness;
	
	public Dominator(boolean minimizeCost, boolean maximizePersistence) {
		this.minimizeCost = minimizeCost;
		this.maximizePersistence = maximizePersistence;
	}

	public boolean dominates(Individual ind1, Individual ind2) {
		int costCompare = 0, persistenceCompare = 0, robustnessCompare = 0;
		
		if (minimizeCost){
			costCompare = costCompare(ind1, ind2);
			if (costCompare > 0) return false; // ind1 does not dominate ind2
		}
		if (maximizePersistence){
			persistenceCompare = persistenceCompare(ind1, ind2);
			if (persistenceCompare > 0) return false; // ind1 does not dominate ind2
		}
		if (maximizeRobustness){
			robustnessCompare = robustnessCompare(ind1, ind2);
			if (robustnessCompare > 0) return false; // ind1 does not dominate ind2
		}
		
		// At this stage, we know that ind2 does not dominate ind1, i.e. ind1 is at least as good wrt all objectives as ind2
		if (minimizeCost){
			if (costCompare < 0) return true;
		}
		if (maximizePersistence){
			if (persistenceCompare < 0) return true;
		}
		if (maximizeRobustness){
			if (robustnessCompare < 0) return true;
		}
		
		return false; // 
	}
	
	private int robustnessCompare(Individual ind1, Individual ind2) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int persistenceCompare(Individual ind1, Individual ind2) {
		return Utilities.getPersistenceComparator().compare(ind1, ind2);
	}

	private int costCompare(Individual ind1, Individual ind2){
		return Utilities.getPenalizedCostComparator().compare(ind1, ind2);
	}
	
	public ArrayList<String> getObjectiveNames(){
		ArrayList<String> names = new ArrayList<String>();
		if (minimizeCost) names.add("Cost");
		if (maximizePersistence) names.add("Persistence");
		if (maximizeRobustness) names.add("Robustness");
		
		return names;
	}

	public boolean hasEqualObjectiveValues(Individual ind1, Individual ind2) {
		boolean costEqual = false, persistenceEqual = false, robustnessEqual = false;
		
		if (minimizeCost){
			costEqual = costCompare(ind1, ind2) == 0;
		}
		if (maximizePersistence){
			persistenceEqual= persistenceCompare(ind1, ind2) == 0;
		}
		if (maximizeRobustness){
			robustnessEqual = robustnessCompare(ind1, ind2) == 0;
		}
		
		return (!minimizeCost || costEqual) && (!maximizePersistence || persistenceEqual) && (!maximizeRobustness || robustnessEqual);  
	}
	
}
