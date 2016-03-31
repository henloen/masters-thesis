package hgsadc.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.protocols.ReproductionProtocol;

public class ReproductionStandard implements ReproductionProtocol {
	
	private ProblemData problemData;
	
	@Override
	public Individual crossover(ArrayList<Individual> parents) {
		
		int numberOfDays = problemData.getLengthOfPlanningPeriod();
		int numberOfVessels = problemData.getVessels().size();
		int numberOfCells = numberOfDays * numberOfVessels;
		
		int randomNumber1 = new Random().nextInt(numberOfCells);
		int randomNumber2 = new Random().nextInt(numberOfCells);
		
		// Making sure n1 < n2
		int n1 = randomNumber1 < randomNumber2 ? randomNumber1 : randomNumber2;
		int n2 = randomNumber1 < randomNumber2 ? randomNumber2 : randomNumber1;
		
		Set<DayVesselCell> allCells = (Set<DayVesselCell>) problemData.getAllDayVesselCells().clone();
		
		Set<DayVesselCell> cellsToCopyFromP1 = new HashSet<>(); // Delta_1
		// Select cells to copy from parent 1
		for (int i = 1; i <= n1; i++){
			cellsToCopyFromP1.add(Utilities.pickAndRemoveRandomElementFromSet(allCells));
		}
		
		Set<DayVesselCell> cellsToCopyFromP2 = new HashSet<>(); // Delta_2
		// Select cells to copy from parent 2
		for (int i = n1 + 1; i <= n2; i++){
			cellsToCopyFromP2.add(Utilities.pickAndRemoveRandomElementFromSet(allCells));
		}
		
		// The rest of the cells will be a mix of parent 1 and parent 2
		Set<DayVesselCell> cellsToCopyFromBoth = allCells; // Delta_mix
		
		GenotypeHGS p1 = (GenotypeHGS) parents.get(0).getGenotype(); // First parent 
		GenotypeHGS p2 = (GenotypeHGS) parents.get(1).getGenotype(); // Second parent
		
		
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = null;
		
		int nVessels = problemData.getVessels().size();
		int nInstallations = problemData.getCustomerInstallations().size();
		GenotypeHGS offspring = new GenotypeHGS(giantTourChromosome, nInstallations, nVessels);
				
		return null;
	}

}
