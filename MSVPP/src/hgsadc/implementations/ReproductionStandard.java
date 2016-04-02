package hgsadc.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.ReproductionProtocol;

public class ReproductionStandard implements ReproductionProtocol {
	
	private ProblemData problemData;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private static int NUMBER_OF_CROSSOVER_RESTARTS = 0;
	
	public ReproductionStandard(ProblemData problemData, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		this.problemData = problemData;
		this.fitnessEvaluationProtocol = fitnessEvaluationProtocol;
	}

	@Override
	public Individual crossover(ArrayList<Individual> parents) {
		
		//System.out.println("Starting crossover of parents " + parents.get(0) + " and " + parents.get(1));
		// Step 0: Inheritance rule
		int nDays = problemData.getLengthOfPlanningPeriod();
		int nVessels = problemData.getVessels().size();
		int numberOfCells = nDays * nVessels;
		int nInstallations = problemData.getCustomerInstallations().size();
		
		//System.out.println("nVessels: " + nVessels + " nDays: " + nDays + " nCells: " + numberOfCells + " nInstallations: " + nInstallations);
		
		int randomNumber1 = new Random().nextInt(numberOfCells);
		int randomNumber2 = new Random().nextInt(numberOfCells);
		
		// Making sure n1 < n2
		int n1 = randomNumber1 < randomNumber2 ? randomNumber1 : randomNumber2;
		int n2 = randomNumber1 < randomNumber2 ? randomNumber2 : randomNumber1;
		
		//System.out.println("n1: " + n1 + ", n2: " + n2);
		
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
		
	/*	System.out.println("Delta1: " + Utilities.printCells(cellsToCopyFromP1));
		System.out.println("Delta2: " + Utilities.printCells(cellsToCopyFromP2));
		System.out.println("DeltaMix: " + Utilities.printCells(cellsToCopyFromBoth));
	*/
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>  p1 = ((GenotypeHGS) parents.get(0).getGenotype()).getGiantTourChromosome(); // First parent 
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>  p2 = ((GenotypeHGS) parents.get(1).getGenotype()).getGiantTourChromosome(); // Second parent
		
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = GenotypeHGS.generateEmptyGiantTourChromosome(nDays, nVessels);
		
		// Step 1: Inherit data from p1
		//System.out.println("Step 1: Inherit data from p1");
		// Copy from Delta_1
		for (DayVesselCell cell : cellsToCopyFromP1) {
			ArrayList<Integer> departuresToCopy = p1.get(cell.day).get(cell.vessel);
			giantTourChromosome.get(cell.day).put(cell.vessel, departuresToCopy);
			//System.out.println("Copying cell " + cell + " from p1");
		}
		// Copy from Delta_mix, parent 1
		for (DayVesselCell cell : cellsToCopyFromBoth) {
			//System.out.println("Copying part of cell " + cell + " from p1");

			ArrayList<Integer> parent1Departures = p1.get(cell.day).get(cell.vessel);
			ArrayList<Integer> departuresToCopy = new ArrayList<>();
			
			//System.out.println("Parent1 departures: " + parent1Departures);
			if (parent1Departures.size() > 0){
				
				// Cutoff points
				int alpha = new Random().nextInt(parent1Departures.size());
				int beta = new Random().nextInt(parent1Departures.size());
				
				//System.out.println("Alpha: " + alpha + " Beta: " + beta);
				if (alpha <= beta){
					departuresToCopy = new ArrayList<>(parent1Departures.subList(alpha, beta));
				}
				else { // beta < alpha --> Wrap around, but maintain original order (i.e. skip departures between beta and alpha)
					departuresToCopy = new ArrayList<>();
					// First section
					for (int i = 0; i < beta; i++){
						departuresToCopy.add(parent1Departures.get(i));
					}
					// Second section
					for (int i = alpha; i < parent1Departures.size(); i++){
						departuresToCopy.add(parent1Departures.get(i));
					}
				}
			}
			//System.out.println("Departures to copy: " + departuresToCopy);
			giantTourChromosome.get(cell.day).put(cell.vessel, departuresToCopy);
		}
		
		// Step 2: Inherit data from p2
		//System.out.println("Step 2: Inherit data from p2");
		Set<DayVesselCell> cellsToCopyFromP2OrBoth = new HashSet<>(cellsToCopyFromP2);  // Delta_2 U Delta_mix
		cellsToCopyFromP2OrBoth.addAll(cellsToCopyFromBoth);
		
		// Chromosomes so far
		List<HashMap<Integer, Set<Integer>>> chromosomes = GenotypeHGS.generateDeparturePatternChromosomesFromGiantTour(giantTourChromosome, nInstallations, nVessels);
		HashMap<Integer, Set<Integer>> installationChromosome = chromosomes.get(0); 
		HashMap<Integer, Set<Integer>> vesselChromosome = chromosomes.get(1);
		
		while (!cellsToCopyFromP2OrBoth.isEmpty()){
			DayVesselCell cell = Utilities.pickAndRemoveRandomElementFromSet(cellsToCopyFromP2OrBoth);
			
		//	System.out.println("Copying cell " + cell + " from parent 2");
			/* Feasibility checks:
			 * 1. Spread of departures (valid installation departure pattern)
			 * 2. Depot capacity
			 * 3. Valid vessel departure pattern
			 * 4. At least one vesselDeparture on each day with installationDeparture
			 *  	--> No need to handle here? Patterns are not predetermined, they are determined by the giant tour
			 */
			int day = cell.day;
			int vessel = cell.vessel;
			
			ArrayList<Integer> departuresInParent2Cell = p2.get(day).get(vessel);
			
			//System.out.println("Departures in parent 2: " + departuresInParent2Cell);
			for (Integer installation : departuresInParent2Cell) {
				if (!feasibleInstallationPattern(installation, day, installationChromosome)){
					//System.out.println("Infeasible installation pattern: " + installation);
				}
				else if (!availableDepotCapacity(day, giantTourChromosome)){
					//System.out.println("Insufficient depot capacity: " + installation);
				}
				else if (!feasibleVesselPattern(day, vessel, vesselChromosome)){
					//System.out.println("Infeasible installation pattern: " + installation);
				}
				else {
					// Add installation to voyage
					//System.out.println("Feasible copy: " + installation);
					giantTourChromosome.get(day).get(vessel).add(installation);
					installationChromosome.get(installation).add(day);
					vesselChromosome.get(vessel).add(day);
				}
			}
		}
		
		//System.out.println("==============================================");
		//System.out.println("Current chromosome " + GenotypeHGS.getGiantTourChromosomeString(giantTourChromosome));
		
		//System.out.println("Step 3: Complete installation services");
		/* Step 3: Complete installation services
		 * 	Make sure all installations have required number of visits
		 *  While there are unserviced installations:
		 *  	Pick random unserviced installation
		 *  	Insert departure at best (day,vessel)-combination
		 */
		
		
		boolean foundFeasibleFillIn = fillInRemainingServices(parents, giantTourChromosome, installationChromosome, vesselChromosome);
		
		
		if (foundFeasibleFillIn){
			GenotypeHGS offspringGenotype = new GenotypeHGS(installationChromosome, vesselChromosome, giantTourChromosome);
			/*System.out.println("==================================================");
			System.out.println("Offspring: ");
			System.out.println(offspringGenotype);
			System.out.println("Total number of crossover restarts: " + NUMBER_OF_CROSSOVER_RESTARTS);
			*/
			return new Individual(offspringGenotype);
		}
		else {
			NUMBER_OF_CROSSOVER_RESTARTS++;
			System.out.println("Crossover failed " + NUMBER_OF_CROSSOVER_RESTARTS + " time(s), trying again");
			return crossover(parents);
		}
		
	}

	private boolean fillInRemainingServices(ArrayList<Individual> parents,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome,
			HashMap<Integer, Set<Integer>> installationChromosome, HashMap<Integer, Set<Integer>> vesselChromosome) {
		
		HashMap<Integer, Integer> remainingVisits = getRemainingVisits(installationChromosome);
		
		while (!remainingVisits.isEmpty()){
			Integer installation = Utilities.pickRandomElementFromSet(remainingVisits.keySet());
			
			Set<DayVesselCell> admissibleCells = getAdmissibleCells(installation, installationChromosome, vesselChromosome, giantTourChromosome);

			if (admissibleCells.isEmpty()){
				return false;
			}
			else {
				doBestInsertion(installation, admissibleCells, giantTourChromosome, installationChromosome, vesselChromosome);
				remainingVisits = getRemainingVisits(installationChromosome);
			}
		}
		return true;
	}
	
	private HashMap<Integer, Integer> getRemainingVisits(HashMap<Integer, Set<Integer>> installationChromosome) {
		
		HashMap<Integer, Integer> remainingVisits = new HashMap<Integer, Integer>();
		for (Integer installation : installationChromosome.keySet()) {
			int requiredVisits = problemData.getInstallationByNumber(installation).getFrequency();
			int remainingVisitsForInstallation = requiredVisits - installationChromosome.get(installation).size();
			
			if (remainingVisitsForInstallation > 0){
				remainingVisits.put(installation, remainingVisitsForInstallation);
			}
		}
		return remainingVisits;
	}

	private void insertInstallation(Integer installation, DayVesselCell cellToInsertInto, int positionToInsertInto,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome,
			HashMap<Integer, Set<Integer>> installationChromosome, HashMap<Integer, Set<Integer>> vesselChromosome) {

		int day = cellToInsertInto.day;
		int vessel = cellToInsertInto.vessel;
		giantTourChromosome.get(day).get(vessel).add(positionToInsertInto, installation); // Adding to end of voyage
		installationChromosome.get(installation).add(day);
		vesselChromosome.get(vessel).add(day);
	}

	private void doBestInsertion(Integer installation, Set<DayVesselCell> admissibleCells,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome,
			HashMap<Integer, Set<Integer>> installationChromosome, HashMap<Integer, Set<Integer>> vesselChromosome) {
		
		DayVesselCell bestCell = null;
		int bestPos = -1;
		double bestInsertionCost = Double.MAX_VALUE;
		for (DayVesselCell cell : admissibleCells) {
			
			int day = cell.day;
			int vessel = cell.vessel;
			Vessel vesselObject = problemData.getVesselByNumber(vessel);
			ArrayList<Integer> currentVoyageSeq = giantTourChromosome.get(day).get(vessel);
			Set<Integer> vesselPattern = new HashSet<>(vesselChromosome.get(vessel));
			vesselPattern.add(day);
			Voyage currentVoyage = new Voyage(currentVoyageSeq, vesselObject, vesselPattern, day, problemData);
			double currentPenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(currentVoyage);
			
			for (int pos = 0; pos <= currentVoyageSeq.size(); pos++){
				ArrayList<Integer> newVoyageSeq = new ArrayList<>(currentVoyageSeq);
				newVoyageSeq.add(pos, installation);
				Voyage newVoyage = new Voyage(newVoyageSeq, vesselObject, vesselPattern, day, problemData);
				double newPenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(newVoyage);
				double insertionCost = newPenalizedCost - currentPenalizedCost;
				
				if (insertionCost < bestInsertionCost){
					bestInsertionCost = insertionCost;
					bestCell = cell;
					bestPos = pos;
				}
			}
		}
		insertInstallation(installation, bestCell, bestPos, giantTourChromosome, installationChromosome, vesselChromosome);
	}

	/**
	 * 
	 * @param installation
	 * @param installationChromosome
	 * @param vesselChromosome
	 * @param giantTourChromosome
	 * @return Set of DayVesselCells for which it is possible to insert departure to installation without violating depot capacity, installationPatterns or vesselPatterns 
	 */
	private Set<DayVesselCell> getAdmissibleCells(Integer installation,
			HashMap<Integer, Set<Integer>> installationChromosome, HashMap<Integer, Set<Integer>> vesselChromosome,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome) {
		
		Set<DayVesselCell> admissibleCells = new HashSet<>();
		Set<Integer> admissibleDays = getAdmissibleDays(installation, installationChromosome, giantTourChromosome);
		
		for (Integer day : admissibleDays) {
			for (int vessel = 1; vessel <= problemData.getVessels().size(); vessel++){
				if (feasibleVesselPattern(day, vessel, vesselChromosome)){
					admissibleCells.add(new DayVesselCell(day, vessel));
				}
			}
		}

		return admissibleCells;
	}
	/**
	 * 
	 * @param installation 
	 * @param installationChromosome
	 * @param giantTourChromosome
	 * @return Set of days for which it is possible to insert departure to installation without violating depot capacity or creating infeasible installationDeparturePattern
	 */
	private Set<Integer> getAdmissibleDays(Integer installation, HashMap<Integer, Set<Integer>> installationChromosome,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome) {

		Set<Integer> admissibleDays = new HashSet<Integer>();
		for (int day = 0; day < problemData.getLengthOfPlanningPeriod(); day++){
			if (availableDepotCapacity(day, giantTourChromosome) && feasibleInstallationPattern(installation, day, installationChromosome)){
				admissibleDays.add(day);
			}
		}
		return admissibleDays;
	}

	private boolean availableDepotCapacity(int day,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome) {
		int depotCapacity = problemData.getDepotCapacity().get(day);
		int nDeparturesOnDay = 0;
		
		for (ArrayList<Integer> vesselDepartures : giantTourChromosome.get(day).values()) { // Loop through all vessels for that day
			if (!vesselDepartures.isEmpty()) nDeparturesOnDay++;
		}
			
		return nDeparturesOnDay < depotCapacity;
	}

	private boolean feasibleVesselPattern(int day, int vessel, HashMap<Integer, Set<Integer>> vesselChromosome) {

		Set<Integer> newVesselPattern = new HashSet<Integer>(vesselChromosome.get(vessel));
		newVesselPattern.add(day);
		
		Set<Set<Integer>> feasiblePatterns = new HashSet<>();
		// Iterate through all possible numberOfDepartures, add all feasible patterns for each
		for (Set<Set<Integer>> set : problemData.getVesselDeparturePatterns().values()) {
			feasiblePatterns.addAll(set);
		}
		
		// Check if new pattern is subset of any valid pattern
		return Utilities.setIsSubsetOfAnySetInCollection(newVesselPattern, feasiblePatterns);
	}

	private boolean feasibleInstallationPattern(Integer installation, int day, HashMap<Integer, Set<Integer>> installationChromosome) {
		
		Set<Integer> currentInstallationPattern = installationChromosome.get(installation); 
		if (currentInstallationPattern.contains(day)){
			return false;
		}
		
		Set<Integer> newInstallationPattern = new HashSet<Integer>(currentInstallationPattern);
		newInstallationPattern.add(day);
		
		Installation installationObject = problemData.getInstallationByNumber(installation);
		int frequency = installationObject.getFrequency();
		Set<Set<Integer>> feasiblePatterns = problemData.getInstallationDeparturePatterns().get(frequency);
		
		// Check if new pattern is subset of any valid pattern
		return Utilities.setIsSubsetOfAnySetInCollection(newInstallationPattern, feasiblePatterns);
	}

}
