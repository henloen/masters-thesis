package hgsadc.implementations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import hgsadc.Individual;
import hgsadc.ProblemData;

public class GenoToPhenoConverterMultiObjective extends GenoToPhenoConverterStandard {

	public GenoToPhenoConverterMultiObjective(ProblemData problemData) {
		super(problemData);
	}
	
	
	@Override
	public void convertGenotypeToPhenotype(Individual individual) {
		super.convertGenotypeToPhenotype(individual);
		setNumberOfChangesFromBaseline(individual);
	}



	private void setNumberOfChangesFromBaseline(Individual individual){
		GenotypeHGS genotype = (GenotypeHGS) individual.getGenotype();
		
		HashMap<Integer, Set<Integer>> installationPattern = genotype.getInstallationDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> baselinePattern = problemData.getBaselineInstallationPattern();
		
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
		individual.setNumberOfChangesFromBaseline(nChanges);
	}
}
