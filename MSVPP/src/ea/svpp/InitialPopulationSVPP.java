package ea.svpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ea.Individual;
import ea.Parameters;
import ea.protocols.InitialPopulationProtocol;
import voyageGenerationDPold.Installation;
import voyageGenerationDPold.Vessel;
import voyageGenerationDPold.Voyage;

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
		
		do {
			HashMap<Installation, Integer> remainingVisits = problemData.getRequiredVisits();
			HashMap<Vessel, Voyage[]> schedule = new HashMap<>();
			
			for (Installation installation : remainingVisits.keySet()) {
				System.out.println(installation.getName() + " requires " + remainingVisits.get(installation) + " visits.");
			}
			
			while (UtilitiesSVPP.moreVisitsRequired(remainingVisits)) {
				
				Vessel vessel = UtilitiesSVPP.pickRandomElementFromList(problemData.vessels, schedule.keySet());
				
				System.out.println("Chartered " + vessel.getName());
				schedule.put(vessel, new Voyage[GenotypeSVPP.NUMBER_OF_DAYS]);
				
				// Spreads departures by incrementing starting day for each PSV.
				int day = schedule.keySet().size()-1;
				int remainingDays = vessel.getNumberOfDaysAvailable();
				
				while (remainingDays >= 2){
					// Check if available depot capacity today
					int depotCapacity = problemData.depotCapacity.get(day);
					int nDeparturesOnDay = UtilitiesSVPP.getNumberOfDeparturesOnDay(day, schedule);
					if (nDeparturesOnDay >= depotCapacity){
						day++;
						day = day % GenotypeSVPP.NUMBER_OF_DAYS; // Start at next week
						remainingDays--;
						continue; // Move on to next day
					}
					
					Set<Installation> installationsToVisit = new HashSet<>();
					for (Installation installation : remainingVisits.keySet()) {
						if (remainingVisits.get(installation) > 0) installationsToVisit.add(installation);
					}
					
					Voyage voyage = problemData.voyageByVesselAndInstallationSet.get(vessel).get(installationsToVisit);
	
					while(voyage == null || remainingDays < voyage.getDuration()){
						Installation installationToRemove = UtilitiesSVPP.pickRandomElementFromSet(installationsToVisit);
						System.out.println("Removing installation " + installationToRemove);
						installationsToVisit.remove(installationToRemove);
						
						voyage = problemData.voyageByVesselAndInstallationSet.get(vessel).get(installationsToVisit);
					}
					
					schedule.get(vessel)[day] = voyage; // Assign voyage to vessel on this day
					System.out.println("Voyage " + voyage.getNumber() + " with duration " + voyage.getDuration() + " assigned on day " + day);
					
					for (Installation installation : voyage.getVisitedInstallations()) {
						int remVisitsToInstallation = remainingVisits.get(installation);
						remainingVisits.put(installation, remVisitsToInstallation-1);
					}
					
					if (!UtilitiesSVPP.moreVisitsRequired(remainingVisits)){
						System.out.println("No more visits required");
						break;
					}
					
					day += voyage.getDuration();
					day = day % GenotypeSVPP.NUMBER_OF_DAYS; // Start at next week
					remainingDays -= voyage.getDuration();
				}
				
				if (schedule.keySet().size() >= GenotypeSVPP.NUMBER_OF_PSVS) break;
			}
			GenotypeSVPP genotype = new GenotypeSVPP(schedule);
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
	
	/* NOT USED AT THE MOMENT
	public Individual createSingleSolution2(){
		/* Greedy heuristic:
		 * 1. Charter random PSVx
		 * 2. Assign random voyage (which visits necessary installations) to PSVx on Monday
		 * 3. Assign new random voyage to PSVx on next available day
		 * 4. If PSVx have no more available days, charter new random PSVy
		 * 5. Repeat 2-4
		 *
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
	
	*/
	
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
	
	private Voyage getVoyageForInstallationSetAndPSV(Vessel PSV, Set<Installation> installationSet){
		// TODO ???
		for (Installation installation : installationSet) {
		}
		
		return null;
	}
}
