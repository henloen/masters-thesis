package ea.svpp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import ea.Individual;
import ea.Parameters;
import ea.protocols.InitialPopulationProtocol;

public class InitialPopulationSVPP implements InitialPopulationProtocol {

	@Override
	public ArrayList<Individual> createInitialPopulation(Parameters parameters) {
		// TODO Auto-generated method stub
		
		
		return null;
	}
	
	public Individual createSingleSolution(Parameters parameters){
		/* Greedy heuristic:
		 * 1. Charter random PSVx
		 * 2. Assign random voyage (which visits necessary installations) to PSVx on Monday
		 * 3. Assign new random voyage to PSVx on next available day
		 * 4. If PSVx have no more available days, charter new random PSVy
		 * 5. Repeat 2-4
		 */
		Set<Integer> charteredPSVs = new HashSet<Integer>();
		int[][] schedule = new int[GenotypeSVPP.NUMBER_OF_PSVS][GenotypeSVPP.NUMBER_OF_DAYS];
		
		ProblemDataSVPP problemData = (ProblemDataSVPP) parameters.getProblemData();
		
		int[] remainingVisits = new int[problemData.installations.size()];
		
		
		
		int PSV = new Random().nextInt(GenotypeSVPP.NUMBER_OF_PSVS);
		int day = 0;
		
		
		
		GenotypeSVPP genotype = new GenotypeSVPP(schedule, charteredPSVs);
		
		
		
		
		return null;
		
	}
	
	private int[] getNumberOfRequiredVisits(){
		for (Installation installation)
	}

}
