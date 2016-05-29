package hgsadc.implementations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Voyage;

public class GenoToPhenoConverterMultiObjective extends GenoToPhenoConverterStandard {

	public GenoToPhenoConverterMultiObjective(ProblemData problemData) {
		super(problemData);
	}
	
	
	@Override
	public void convertGenotypeToPhenotype(Individual individual) {
		super.convertGenotypeToPhenotype(individual);
		
		Dominator dominationCriteria = problemData.dominationCriteria;
		
		if (dominationCriteria.maximizePersistence) setNumberOfChangesFromBaseline(individual);
		if (dominationCriteria.maximizeRobustness) setNumberOfRobustVoyages(individual);
	}

	private void setNumberOfRobustVoyages(Individual individual){
		HashMap<Integer, Integer> minimumSlack = problemData.getMinimumSlackForRobustness();
		
		PhenotypeHGS phenotype = (PhenotypeHGS) individual.getPhenotype();
		int nRobustVoyages = 0;
		for (Integer day : phenotype.getGiantTour().keySet()){
			for (Voyage voyage : phenotype.getGiantTour().get(day).values()){
				if (voyage == null) continue;
				
				int durationDays = voyage.getDurationDays();
					int minSlackForVoyage = minimumSlack.get(durationDays);
				
				if (voyage.getSlack() >= minSlackForVoyage){
					nRobustVoyages++;
				}
			}
		}
		individual.setNumberOfRobustVoyages(nRobustVoyages);
	}
	
	private void setNumberOfChangesFromBaseline(Individual individual){
		GenotypeHGS genotype = (GenotypeHGS) individual.getGenotype();
		
		HashMap<Integer, Set<Integer>> installationPattern = genotype.getInstallationDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> baselinePattern = problemData.getBaselineInstallationPattern();
		
		int nChanges = calculateNumberOfChangesFromBaseline(installationPattern, baselinePattern);
		individual.setNumberOfChangesFromBaseline(nChanges);
//		System.out.println("Individual: " + individual + " has " + nChanges + " from baseline");
	}
	
	private int calculateNumberOfChangesFromBaseline(HashMap<Integer, Set<Integer>> installationPattern, HashMap<Integer, Set<Integer>> baselinePattern){
		int nChanges = 0;
		for (Integer installation : installationPattern.keySet()){
			if (baselinePattern.containsKey(installation)){ // Do not check installations that are not in both baseline and new problem
				Set<Integer> installationDepartures = new HashSet<>(installationPattern.get(installation));
				Set<Integer> baselineDepartures = new HashSet<>(baselinePattern.get(installation));
				
//				System.out.println("Baseline for installation " + installation + " : " + baselineDepartures);
//				System.out.println("New ptrn for installation " + installation + " : " + installationDepartures);
				
				installationDepartures.removeAll(baselineDepartures);
				nChanges += installationDepartures.size();
//				System.out.println("Added departures: " + installationDepartures.size());
				
				installationDepartures = new HashSet<>(installationPattern.get(installation));
				baselineDepartures.removeAll(installationDepartures);
				nChanges += baselineDepartures.size();
//				System.out.println("Removed departures: " + baselineDepartures.size());
				
			}
		}
		return nChanges;

	}
}
