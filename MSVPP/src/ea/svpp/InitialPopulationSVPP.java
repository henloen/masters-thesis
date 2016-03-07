package ea.svpp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ea.Individual;
import ea.Parameters;
import ea.protocols.InitialPopulationProtocol;
import voyageGenerationDP.Installation;
import voyageGenerationDP.Vessel;
import voyageGenerationDP.Voyage;

public class InitialPopulationSVPP implements InitialPopulationProtocol {
	
	Parameters parameters;
	ProblemDataSVPP problemData;
	
	public InitialPopulationSVPP(Parameters parameters) {
		this.parameters = parameters;
		problemData = (ProblemDataSVPP) parameters.getProblemData();
	}

	@Override
	public ArrayList<Individual> createInitialPopulation() {
		GenotypeSVPP.NUMBER_OF_DAYS = problemData.lengthOfPlanningPeriod;
		GenotypeSVPP.NUMBER_OF_PSVS = problemData.vessels.size();
		
		System.out.println("Number of days: " + GenotypeSVPP.NUMBER_OF_DAYS + ". Number of PSVs: " + GenotypeSVPP.NUMBER_OF_PSVS);
		int populationSize = parameters.getnChildren();
		System.out.println("Population size: " + populationSize);
		ArrayList<Individual> initialPopulation = new ArrayList<Individual>(populationSize);
		
		for (int i = 0; i < populationSize; i++){
			initialPopulation.add(createSingleSolution());
			System.out.println("Finished creating individual " + i);
		}
		return initialPopulation;
	}
	
	public Individual createSingleSolution(){
		/* Greedy heuristic 2:
		 * 1. Charter random PSVx
		 * 2. Assign a voyage that visits all installations with remainingVisits
		 * 3. If no voyage exists
		 * 3. Assign random voyage that visits i to PSVx on a random day
		 * 4. Repeat 2-3 until all installations are visited
		 */
		
		Individual individual = new Individual(null);
		
		int[] remainingVisits = getNumberOfRequiredVisits();
		
		for (int i = 0; i < remainingVisits.length; i++) {
			System.out.println("Installation " + (i+1) + " requires " + remainingVisits[i] + " more visits.");
		}
		
		do {
			Set<Integer> charteredPSVs = new HashSet<Integer>();
			int[][] schedule = new int[GenotypeSVPP.NUMBER_OF_PSVS][GenotypeSVPP.NUMBER_OF_DAYS];
			
			while (moreVisitsRequired(remainingVisits)) {
				
				int PSV;
				do {
					PSV = new Random().nextInt(GenotypeSVPP.NUMBER_OF_PSVS);
				} while (charteredPSVs.contains(PSV));
				
				charteredPSVs.add(PSV);
				System.out.println("Chartered PSV " + PSV);
				
				Vessel vessel = problemData.vessels.get(PSV);
				
				
				int day = charteredPSVs.size()-1;
				/* Attempt to spread departures by incrementing starting day for each PSV.
				 * BUT! 
				 */
				// int day = 0;
				int remainingDays = vessel.getNumberOfDaysAvailable();
				
				while (remainingDays >= 2){
					// Check if available depot capacity today
					int depotCapacity = problemData.depotCapacity.get(day);
					int nDeparturesOnDay = numberOfDeparturesOnDay(schedule, day);
					if (nDeparturesOnDay >= depotCapacity){
						day++;
						day = day % GenotypeSVPP.NUMBER_OF_DAYS; // Start at next week
						remainingDays--;
						continue; // Move on to next day
					}
					
					Set<Integer> installationsToVisit = new HashSet<>();
					installationsToVisit.add(0); // Add depot to set
					for (int i = 1; i < remainingVisits.length; i++) {
						if (remainingVisits[i] > 0) installationsToVisit.add(i);
					}
					
					Voyage voyage = problemData.voyageByVesselAndInstallationSet.get(vessel).get(installationsToVisit);
	
					while(voyage == null || remainingDays < voyage.getDuration()){
						Integer installationToRemove = new Random().nextInt(problemData.installations.size());
						System.out.println("Removing installation " + installationToRemove);
						installationsToVisit.remove(installationToRemove);
						voyage = problemData.voyageByVesselAndInstallationSet.get(vessel).get(installationsToVisit);
					}
					
					schedule[PSV][day] = voyage.getNumber(); // Assign voyage to PSV on this day
					//System.out.println("Voyage " + voyage.getNumber() + " with duration " + voyage.getDuration() + " assigned on day " + day);
					day += voyage.getDuration();
					day = day % GenotypeSVPP.NUMBER_OF_DAYS; // Start at next week
					remainingDays -= voyage.getDuration();
				}
				
				if (charteredPSVs.size() >= GenotypeSVPP.NUMBER_OF_PSVS) break;
			}
			GenotypeSVPP genotype = new GenotypeSVPP(schedule, charteredPSVs);
			individual = new Individual(genotype);

		} while (!problemData.isFeasibleSchedule(individual));
		
		return individual;
	}
	
	
	private int maxIndex(int[] array){
		int max = Integer.MIN_VALUE;
		int maxInd = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i] > max){
				maxInd = i;
				max = array[i];
			}
		}
		return maxInd;
	}
	
	public Individual createSingleSolution2(){
		/* Greedy heuristic:
		 * 1. Charter random PSVx
		 * 2. Assign random voyage (which visits necessary installations) to PSVx on Monday
		 * 3. Assign new random voyage to PSVx on next available day
		 * 4. If PSVx have no more available days, charter new random PSVy
		 * 5. Repeat 2-4
		 */
		Individual individual = new Individual(null);
		
		int[] remainingVisits = getNumberOfRequiredVisits();
		
		for (int i = 0; i < remainingVisits.length; i++) {
			System.out.println("Installation " + (i+1) + " requires " + remainingVisits[i] + " more visits.");
		}
		
		do {
			Set<Integer> charteredPSVs = new HashSet<Integer>();
			int[][] schedule = new int[GenotypeSVPP.NUMBER_OF_PSVS][GenotypeSVPP.NUMBER_OF_DAYS];
			while (moreVisitsRequired(remainingVisits)) {
				
				int PSV;
				do {
					PSV = new Random().nextInt(GenotypeSVPP.NUMBER_OF_PSVS);
				} while (charteredPSVs.contains(PSV));
				
				charteredPSVs.add(PSV);
				System.out.println("Chartered PSV " + PSV);
				
				int day = 0;
				
				int remainingDays = GenotypeSVPP.NUMBER_OF_DAYS - day;
				
				while (remainingDays >= 2){
					
					// Check if available depot capacity today
					int depotCapacity = problemData.depotCapacity.get(day);
					int nDeparturesOnDay = numberOfDeparturesOnDay(schedule, day);
					if (nDeparturesOnDay >= depotCapacity){
						day++;
						remainingDays--;
						continue; // Move on to next day
					}
					
					ArrayList<Voyage> voyageSetForPSV = problemData.voyageSetByVessel.get(problemData.vessels.get(PSV));
					
					Voyage randomVoyage;
					do {
						int voyageNumber = new Random().nextInt(voyageSetForPSV.size());
						randomVoyage = voyageSetForPSV.get(voyageNumber);
						//System.out.println("Random voyage: " + randomVoyage.getNumber());
						
					} while (!installationsInVoyageRequireVisits(randomVoyage, remainingVisits) || remainingDays < randomVoyage.getDuration());
					
					schedule[PSV][day] = randomVoyage.getNumber(); // Assign voyage to PSV on this day
					//System.out.println("Voyage " + randomVoyage.getNumber() + " with duration " + randomVoyage.getDuration() + " assigned on day " + day);
					day += randomVoyage.getDuration();
					remainingDays -= randomVoyage.getDuration();
				}
				
				if (charteredPSVs.size() >= GenotypeSVPP.NUMBER_OF_PSVS) break;
			}
			GenotypeSVPP genotype = new GenotypeSVPP(schedule, charteredPSVs);
			individual = new Individual(genotype);

		} while (!problemData.isFeasibleSchedule(individual));
		
		return individual;
	}
	
	private int numberOfDeparturesOnDay(int[][] schedule, int day){
		int nDepartures = 0;
		for (int PSV = 0; PSV < schedule.length; PSV++){
			if (schedule[PSV][day] > 0) nDepartures++;
		}
		return nDepartures;
	}
	
	private boolean moreVisitsRequired(int[] remainingVisits){
		for (int i = 0; i < remainingVisits.length; i++) {
			if (remainingVisits[i] != 0){
				return true;
			}
		}
		return false;
	}
	
	private boolean installationsInVoyageRequireVisits(Voyage voyage, int[] remainingVisits){
		for (int installation : voyage.getVisited()){
			if (installation >= remainingVisits.length){
				// The visited list in each voyage contains the installation N+1 to indicate 2nd visit at depot, hence this check
				continue;
			}
			else if (remainingVisits[installation] == 0){
				return false;
			}
		}
		return true;
	}
	
	private int[] getNumberOfRequiredVisits(){
		int[] requiredVisits = new int[problemData.installations.size()];
		for (int i = 1; i < problemData.installations.size(); i++){ // Depot has no required visits, therefore start on 1
			Installation installation = problemData.installations.get(i);
			requiredVisits[i] = installation.getFrequency();
		}
		return requiredVisits;
	}
	
	private Voyage getVoyageForInstallationSetAndPSV(Vessel PSV, Set<Installation> installationSet){
		
		for (Installation installation : installationSet) {
		}
		
		return null;
	}
}
