package hgsadc.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.ReproductionProtocol;

public class ReproductionStandard implements ReproductionProtocol {
	
	private ProblemData problemData;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	
	public ReproductionStandard(ProblemData problemData, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		this.problemData = problemData;
		this.fitnessEvaluationProtocol = fitnessEvaluationProtocol;
	}

	@Override
	public Individual crossover(ArrayList<Individual> parents) {
		
		System.out.println("Starting crossover of parents " + parents.get(0) + " and " + parents.get(1));
		// Step 0: Inheritance rule
		int nDays = problemData.getLengthOfPlanningPeriod();
		int nVessels = problemData.getVessels().size();
		int numberOfCells = nDays * nVessels;
		int nInstallations = problemData.getCustomerInstallations().size();
		
		System.out.println("nVessels: " + nVessels + " nDays: " + nDays + " nCells: " + numberOfCells + " nInstallations: " + nInstallations);
		
		int randomNumber1 = new Random().nextInt(numberOfCells);
		int randomNumber2 = new Random().nextInt(numberOfCells);
		
		// Making sure n1 < n2
		int n1 = randomNumber1 < randomNumber2 ? randomNumber1 : randomNumber2;
		int n2 = randomNumber1 < randomNumber2 ? randomNumber2 : randomNumber1;
		
		System.out.println("n1: " + n1 + ", n2: " + n2);
		
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
		
		System.out.println("Delta1: " + Utilities.printCells(cellsToCopyFromP1));
		System.out.println("Delta2: " + Utilities.printCells(cellsToCopyFromP2));
		System.out.println("DeltaMix: " + Utilities.printCells(cellsToCopyFromBoth));
		
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>  p1 = ((GenotypeHGS) parents.get(0).getGenotype()).getGiantTourChromosome(); // First parent 
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>  p2 = ((GenotypeHGS) parents.get(1).getGenotype()).getGiantTourChromosome(); // Second parent
		
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = GenotypeHGS.generateEmptyGiantTourChromosome(nDays, nVessels);
		
		// Step 1: Inherit data from p1
		// Copy from Delta_1
		for (DayVesselCell cell : cellsToCopyFromP1) {
			ArrayList<Integer> departuresToCopy = p1.get(cell.day).get(cell.vessel);
			giantTourChromosome.get(cell.day).put(cell.vessel, departuresToCopy);
			System.out.println("Copying cell " + cell + " from ");
		}
		// Copy from Delta_mix, parent 1
		for (DayVesselCell cell : cellsToCopyFromBoth) {
			ArrayList<Integer> parent1Departures = p1.get(cell.day).get(cell.vessel);
			ArrayList<Integer> departuresToCopy;
			
			// Cutoff points
			int alpha = new Random().nextInt(parent1Departures.size());
			int beta = new Random().nextInt(parent1Departures.size());
			
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
			giantTourChromosome.get(cell.day).put(cell.vessel, departuresToCopy);
		}
		
		// Step 2: Inherit data from p2
		Set<DayVesselCell> cellsToCopyFromP2OrBoth = new HashSet<>(cellsToCopyFromP2);  // Delta_2 U Delta_mix
		cellsToCopyFromP2OrBoth.addAll(cellsToCopyFromBoth);
		
		// Chromosomes so far
		List<HashMap<Integer, Set<Integer>>> chromosomes = GenotypeHGS.generateDeparturePatternChromosomesFromGiantTour(giantTourChromosome, nInstallations, nVessels);
		HashMap<Integer, Set<Integer>> installationChromosome = chromosomes.get(0); 
		HashMap<Integer, Set<Integer>> vesselChromosome = chromosomes.get(1);
		
		while (!cellsToCopyFromP2OrBoth.isEmpty()){
			DayVesselCell cell = Utilities.pickAndRemoveRandomElementFromSet(cellsToCopyFromP2OrBoth);
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
			
			for (Integer installation : departuresInParent2Cell) {
				if (feasibleInstallationPattern(installation, day, installationChromosome) && availableDepotCapacity(day, giantTourChromosome) && 
						feasibleVesselPattern(day, vessel, vesselChromosome)){
					// Add installation to voyage
					giantTourChromosome.get(day).get(vessel).add(installation);
					installationChromosome.get(installation).add(day);
					vesselChromosome.get(vessel).add(day);
				}
			}
		}
		
		/* Step 3: Complete installation services
		 * 	Make sure all installations have required number of visits
		 *  While there are unserviced installations:
		 *  	Pick random unserviced installation
		 *  	Insert departure at best (day,vessel)-combination
		 */
		HashMap<Integer, Integer> remainingVisits = getRemainingVisits(installationChromosome);
		while (!remainingVisits.isEmpty()){
			Integer installation = Utilities.pickRandomElementFromSet(remainingVisits.keySet());
			
			Set<DayVesselCell> admissibleCells = getAdmissibleCells(installation, installationChromosome, vesselChromosome, giantTourChromosome);
			
			DayVesselCell bestInsertionCell = getBestInsertion(installation, admissibleCells, giantTourChromosome);
			insertInstallation(installation, bestInsertionCell, giantTourChromosome, installationChromosome, vesselChromosome);
			
			int remVisitsToInstallation = remainingVisits.get(installation)-1;
			if (remVisitsToInstallation == 0){
				remainingVisits.remove(installation);
			}
			else {
				remainingVisits.put(installation, remVisitsToInstallation);
			}
			
		}
		
		GenotypeHGS offspringGenotype = new GenotypeHGS(vesselChromosome, installationChromosome, giantTourChromosome);		
		
		return new Individual(offspringGenotype);
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

	private void insertInstallation(Integer installation, DayVesselCell cellToInsertInto,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome,
			HashMap<Integer, Set<Integer>> installationChromosome, HashMap<Integer, Set<Integer>> vesselChromosome) {

		int day = cellToInsertInto.day;
		int vessel = cellToInsertInto.vessel;
		giantTourChromosome.get(day).get(vessel).add(installation); // Adding to end of voyage
		installationChromosome.get(installation).add(day);
		vesselChromosome.get(vessel).add(day);
	}

	/**
	 * 
	 * @param installation
	 * @param admissibleCells
	 * @param giantTourChromosome
	 * @return The DayVesselCell in admissibleCells which has the lowest penalized cost of inserting installation
	 */
	private DayVesselCell getBestInsertion(Integer installation, Set<DayVesselCell> admissibleCells,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome) {
		
		DayVesselCell bestCell = null;
		int bestCost = Integer.MAX_VALUE;
		for (DayVesselCell cell : admissibleCells) {
			int insertionCost = getInsertionCost(installation, cell, giantTourChromosome);
			if (insertionCost < bestCost){
				bestCost = insertionCost;
				bestCell = cell;
			}
		}
		
		return bestCell;
	}

	private int getInsertionCost(Integer installation, DayVesselCell cell,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome) {
		// TODO Auto-generated method stub
		
		return 0;
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
		
		for (ArrayList<Integer> departures : giantTourChromosome.get(day).values()) { // Loop through all vessels for that day
			if (!departures.isEmpty()) nDeparturesOnDay++;
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
		
		Set<Integer> newInstallationPattern = new HashSet<Integer>(installationChromosome.get(installation));
		newInstallationPattern.add(day);
		
		Installation installationObject = problemData.getInstallationByNumber(installation);
		int frequency = installationObject.getFrequency();
		Set<Set<Integer>> feasiblePatterns = problemData.getInstallationDeparturePatterns().get(frequency);
		
		// Check if new pattern is subset of any valid pattern
		return Utilities.setIsSubsetOfAnySetInCollection(newInstallationPattern, feasiblePatterns);
	}

}
