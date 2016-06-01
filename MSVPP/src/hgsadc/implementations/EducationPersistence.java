package hgsadc.implementations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import hgsadc.HGSmain;
import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;
import hgsadc.protocols.Genotype;
import hgsadc.protocols.Phenotype;

public class EducationPersistence extends EducationStandard {

	public EducationPersistence(ProblemData problemdata, FitnessEvaluationProtocol fitnessEvaluationProtocol,
			PenaltyAdjustmentProtocol penaltyAdjustmentProtocol, GenoToPhenoConverterProtocol genoToPhenoConverter) {
		super(problemdata, fitnessEvaluationProtocol, penaltyAdjustmentProtocol, genoToPhenoConverter);
	}

	@Override
	public void educate(Individual individual) {
		
		if (isRepair){
			String previousEducation = individual.typeOfEducation; 
			educateWithType(individual,	previousEducation);
		}
		else {
			double randomDouble = new Random().nextDouble();
			
			if (randomDouble <= 0.25){ // costEducate with probability 0.25
				educateWithType(individual, "Cost");
				HGSmain.COST_EDUCATIONS++;
			}
			else if (randomDouble <= 0.5){ // persistenceEducate with probability 0.25
				educateWithType(individual, "Persistence");
				HGSmain.PERSISTENCE_EDUCATIONS++;
			}
			else if (randomDouble <= 0.75) { // Educate first persistence then cost with probability 0.25
				educateWithType(individual, "Persistence+Cost");
				HGSmain.PERSISTENCE_COST_EDUCATIONS++;
			}
			else { // Educate first cost then persistence with probability 0.25
				educateWithType(individual, "Cost+Persistence");
				HGSmain.COST_PERSISTENCE_EDUCATIONS++;
			}
		}
	}
	
	private void educateWithType(Individual individual, String typeOfEducation){
		if(typeOfEducation.equals("Cost")){
//			System.out.println("Educating cost");
			costEducate(individual);
			individual.typeOfEducation = "Cost";
			updatePenaltyAdjustmentCounter(individual);
		}
		else if (typeOfEducation.equals("Persistence")){
			persistenceEducate(individual);
//			vesselPatternImprovement(individual);
			routeImprovement(individual); // Improving routes does not affect installation patterns, i.e. not persistence
			individual.typeOfEducation = "Persistence";
			updatePenaltyAdjustmentCounter(individual);
		}
		else if (typeOfEducation.equals("Persistence+Cost")){
			persistenceEducate(individual);
			costEducate(individual);
			individual.typeOfEducation = "Persistence+Cost";
			updatePenaltyAdjustmentCounter(individual);
		}
		else if (typeOfEducation.equals("Cost+Persistence")){
			costEducate(individual);
			persistenceEducate(individual);
			individual.typeOfEducation = "Cost+Persistence";
			updatePenaltyAdjustmentCounter(individual);
		}
		else {
			System.err.println(typeOfEducation + " is not a valid education");
		}
	}
	
	private void persistenceEducate(Individual individual) {
//		System.out.println("\nEducating persistence on individual " + individual);
		installationPatternImprovementForPersistence(individual);
		moveVoyage(individual);
	}
	
	private void costEducate(Individual individual){
		routeImprovement(individual);
		patternImprovement(individual);
		routeImprovement(individual);
	}

	private void installationPatternImprovementForPersistence(Individual individual){
			/*
			 * pick one random installation i
			 * change departure pattern to the correct (baseline) pattern
			 * if (new pattern is feasible wrt depot, feasible patterns, psv patterns)
			 * 		keep pattern
			 * else
			 * 		move on to next random installation
			 * end-if
			 */
	//		System.out.println("\nPersistenceEducating individual " + individual);
			HashMap<Integer, Set<Integer>> baselinePattern = problemData.getBaselineInstallationPattern();
			HashMap<Integer, Set<Integer>> currentPattern = ((GenotypeHGS) individual.getGenotype()).getInstallationDeparturePatternChromosome();
			
			HashMap<Integer, Set<Integer>> vesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturePatternChromosome();
			HashMap<Integer, Set<Integer>> reversedVesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturesPerDay();
			
			Set<Integer> alreadyCheckedInstallations = new HashSet<>();
			
			while (alreadyCheckedInstallations.size() < problemData.getCustomerInstallations().size()){
				
				Installation inst = Utilities.pickRandomElementFromList(problemData.getCustomerInstallations());
				int instNumber = inst.getNumber();
	//			System.out.println("Changing pattern of installation " + instNumber);
				
				if (!baselinePattern.containsKey(instNumber) || inst.getFrequency() != baselinePattern.get(instNumber).size()){
					alreadyCheckedInstallations.add(instNumber);
					continue;
				}
				
				Set<Integer> baselineInstPattern =  baselinePattern.get(instNumber);
				
				if (baselineInstPattern.equals(currentPattern.get(instNumber))){
					// Zero changes in this installation's pattern, or baseline does not contain the installation
	//				System.out.println("Baseline = current pattern: ");
	//				System.out.println("Baseline: " + baselineInstPattern);
	//				System.out.println("Current: " + currentPattern.get(instNumber));
					alreadyCheckedInstallations.add(instNumber);
					continue;
				}
				HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourWithoutInstallation = getCopyOfGiantTourWithoutInstallation(inst, individual.getGenotype().getGiantTourChromosome());
				
				ArrayList<VoyageInsertion> insertions = new ArrayList<>();
				for (Integer day : baselineInstPattern){
					insertions.add(findLeastCostInsertionOnDay(instNumber, day, giantTourWithoutInstallation, vesselPatterns, reversedVesselPatterns));
				}
				Individual newIndividual = applyInsertions(individual, insertions, inst);
				individual.setGenotypeAndUpdatePenalizedCost(newIndividual.getGenotype(), genoToPhenoConverter, fitnessEvaluationProtocol);
				alreadyCheckedInstallations.add(instNumber);
				break; // Only change one installation at a time
	
				/* Feasibility checks:
				*	Depot capacity: not necessary, can always add installation to an existing voyage
				*	PSV patterns: not neccessary, is penalized in penalized cost
				**/
			}
			
		}

	
	// NOT USED
	private void moveVoyage(Individual individual){
			/* Calculates the minimum number of voyages required on each day in the baseline solution. If any day has too few voyages,
			 * add new voyage on that day by removing another voyage
			 */
			Phenotype phenotype = individual.getPhenotype();
			
			HashMap<Integer, Integer> requiredVoyages = getRequiredNumberOfVoyagesPerDay();
			HashMap<Integer, Integer> currentNumberOfVoyages = individual.getNumberOfVoyagesPerDay();
			
	//		System.out.println("Required voyages: " + requiredVoyages);
	//		System.out.println("Current nvoyages: " + currentNumberOfVoyages);
			
			Set<Integer> daysWithTooFewVoyages = new HashSet<>();
			Set<Integer> daysWithTooManyVoyages = new HashSet<>();
			
			for (Integer day : currentNumberOfVoyages.keySet()){
				int requiredVoyagesOnDay = requiredVoyages.get(day);
				int currentNumberOfVoyagesOnDay = currentNumberOfVoyages.get(day);
				
				if (currentNumberOfVoyagesOnDay < requiredVoyagesOnDay){
					daysWithTooFewVoyages.add(day);
				}
				else if (currentNumberOfVoyagesOnDay > requiredVoyagesOnDay){
					daysWithTooManyVoyages.add(day);
				}
			}
	//		System.out.println("Days too few: " + daysWithTooFewVoyages);
	//		System.out.println("Days too many: " + daysWithTooManyVoyages);
			
			// The algorithm needs to remove a voyage to create a new one
			if (daysWithTooFewVoyages.isEmpty()){
				HGSmain.UNFEASIBLE_PERSISTENCE_EDUCATIONS++;
				HGSmain.NODAYSWITHTOOFEWVOYAGES++;
				return;
			}
			if (daysWithTooManyVoyages.isEmpty()){
	//			System.out.println("No feasible move");
				HGSmain.UNFEASIBLE_PERSISTENCE_EDUCATIONS++;
				HGSmain.NODAYSWITHTOOMANYVOYAGES++;
				return;
			}
	
			HashMap<Integer, Set<Integer>> vesselPattern = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturePatternChromosome();
			int dayToMoveTo = -1;
			int dayToMoveFrom = -1;
			int vesselToMoveTo = -1;
			
			
			// Pick days to move to and from
			do {
				if(daysWithTooFewVoyages.isEmpty()){
					HGSmain.UNFEASIBLE_PERSISTENCE_EDUCATIONS++;
	//				System.out.println("No feasible move");
					HGSmain.NOFEASIBLEVESSELDAYS++;
					return;
				}
				dayToMoveTo = Utilities.pickAndRemoveRandomElementFromSet(daysWithTooFewVoyages);
	
				Set<Integer> possibleDaysToMoveFrom = new HashSet<>(daysWithTooManyVoyages);
				do {
					if (possibleDaysToMoveFrom.isEmpty()){
						dayToMoveFrom = -1;
						break; // inner loop
					}
					dayToMoveFrom = Utilities.pickAndRemoveRandomElementFromSet(possibleDaysToMoveFrom);
					vesselToMoveTo = findBestVesselToMoveVoyage(dayToMoveFrom, dayToMoveTo, vesselPattern, phenotype);
				} while (vesselToMoveTo == -1); // No feasible vessel found on dayToMoveTo
				
				if (dayToMoveFrom != -1){
					break; // Valid days found, breaking outer loop and proceeding
				}
			} while(true);
			
			// Create set of all installations departed to on dayToMoveFrom
			HashMap<Vessel, Voyage> voyagesOnDayToMoveFrom = individual.getPhenotype().getGiantTour().get(dayToMoveFrom);
			Set<Installation> departuresOnDay = new HashSet<>();
			for (Voyage voy : voyagesOnDayToMoveFrom.values()){
				if (voy != null){
					departuresOnDay.addAll(voy.getVisitedInstallations());
				}
			}
			
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour = Utilities.deepCopyGiantTour(individual.getGenotype().getGiantTourChromosome());
			HashMap<Integer, Set<Integer>> installationChromosome = ((GenotypeHGS) individual.getGenotype()).getInstallationDeparturePatternChromosome();
			
			int installationsMoved = 0;
			HashMap<Integer, Set<Integer>> baselinePattern = problemData.getBaselineInstallationPattern();
			Set<Installation> instsWithBaselineOnDayToMoveTo = getInstallationsWithDepartureOnDay(baselinePattern, dayToMoveTo);
			instsWithBaselineOnDayToMoveTo.retainAll(departuresOnDay);
			
			// Loop through all installations visited on dayToMoveFrom that needs to be visited on dayToMoveTo
			for (Installation installation : instsWithBaselineOnDayToMoveTo) {
				int instNum = installation.getNumber();
				Set<Integer> installationPattern = installationChromosome.get(instNum);
				Set<Integer> baselineInstPattern = baselinePattern.get(instNum);
				
				// Check if installation is already visited on dayToMoveTo or if new pattern is feasible
				if (baselineInstPattern != null && canMoveInstallationVisit(installation, dayToMoveFrom, dayToMoveTo, installationPattern, baselineInstPattern)){
					moveInstallationVisit(installation, vesselToMoveTo, dayToMoveFrom, dayToMoveTo, giantTour);
					installationsMoved++;
				}
				if (installationsMoved >= problemData.getMaxInstallationsPerVoyage()){
					break;
				}
			}
			int nInstallations = problemData.getCustomerInstallations().size();
			int nVessels = problemData.getVessels().size();
			GenotypeHGS genotypeAfterMoving = new GenotypeHGS(giantTour, nInstallations, nVessels);
			
			mergeRemainingDepartures(dayToMoveFrom, vesselToMoveTo, giantTour, genotypeAfterMoving); // Merge remaining installation departures
			
			// Update individual
			GenotypeHGS newGenotype = new GenotypeHGS(giantTour, nInstallations, nVessels);
			individual.setGenotypeAndUpdatePenalizedCost(newGenotype, genoToPhenoConverter, fitnessEvaluationProtocol);
		}

	private Set<Installation> getInstallationsWithDepartureOnDay(HashMap<Integer, Set<Integer>> baselinePattern, int day) {
		HashSet<Installation> installationsDepartingOnDay = new HashSet<>();
		for (Entry<Integer, Set<Integer>> instAndPattern : baselinePattern.entrySet()){
			if (instAndPattern.getValue().contains(day)){
				installationsDepartingOnDay.add(problemData.getInstallationByNumber(instAndPattern.getKey()));
			}
		}
		
		return installationsDepartingOnDay;
	}

	private void mergeRemainingDepartures(int day, int vesselToMove,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour, GenotypeHGS genotype) {
		
		HashMap<Integer, Set<Integer>> vesselPatterns = genotype.getVesselDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> reversedVesselPatterns = genotype.getVesselDeparturesPerDay();
		
		// Greedily insert all
		ArrayList<Integer> voyageToRemove = giantTour.get(day).get(vesselToMove);
		giantTour.get(day).put(vesselToMove, new ArrayList<>()); // Remove voyage
		
		for (Integer inst : voyageToRemove){
			VoyageInsertion insertion = findLeastCostInsertionOnDay(inst, day, giantTour, vesselPatterns, reversedVesselPatterns);
			applyInsertion(insertion, giantTour);
		}
	}

	private void moveInstallationVisit(Installation installation, int vessel, int dayToMoveFrom, int dayToMoveTo,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour) {

		int instNumber = installation.getNumber();
		int vesselVisitingInst = getVesselDepartingToInstallationOnDay(installation, dayToMoveFrom, giantTour);
		giantTour.get(dayToMoveFrom).get(vesselVisitingInst).remove(Integer.valueOf(instNumber));
		giantTour.get(dayToMoveTo).get(vessel).add(instNumber);
		
	}

	private int getVesselDepartingToInstallationOnDay(Installation installation, int day,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour) {

		for (Integer vessel : giantTour.get(day).keySet()){
			List<Integer> voyage = giantTour.get(day).get(vessel);
			if (voyage.contains(installation.getNumber())){
				return vessel;
			}
		}
		return -1; // No departure to installation on day
	}

	private boolean canMoveInstallationVisit(Installation installation, int dayToMoveFrom, int dayToMoveTo,
			Set<Integer> installationPattern, Set<Integer> baselinePattern) {
		// Returns true if there is no departure to installation on dayToMoveTo already and new pattern is feasible
		if (installationPattern.contains(dayToMoveTo)){
			return false;
		}
		
		if (baselinePattern.contains(dayToMoveFrom)){
			return false;
		}
			
		Set<Integer> newPattern = new HashSet<>(installationPattern);
		newPattern.remove(dayToMoveFrom);
		newPattern.add(dayToMoveTo);
	
		
		Set<Set<Integer>> feasiblePatternsForInstallation = problemData.getInstallationDeparturePatterns().get(installation.getFrequency()); 

		if (feasiblePatternsForInstallation.contains(newPattern)){
//			System.out.println("Feasible");
			return true;
		}
		else {
//			System.out.println("Infeasible");
			return false;
		}
	}
	
	private boolean canMoveVoyageToDayAndVessel(int dayToMoveFrom, int dayToMoveTo, int vesselNum, Set<Integer> departureDaysForVessel, Phenotype phenotype){
		int nDays = problemData.getLengthOfPlanningPeriod();

		ArrayList<Integer> sortedDepartures = new ArrayList<>(departureDaysForVessel);
		Collections.sort(sortedDepartures, Collections.reverseOrder()); // Sort days from latest to earliest
		sortedDepartures.remove(Integer.valueOf(dayToMoveFrom)); // Disregard dayToMoveFrom
		
		if (sortedDepartures.isEmpty()){
			return true;
		}
		
		int dayOfPreviousDeparture = -1;
		for (Integer day : sortedDepartures){
			if (day < dayToMoveTo){
				dayOfPreviousDeparture = day;
			}
		}
		// If no departure before dayToMoveTo, previous departure is the last in the week
		dayOfPreviousDeparture = dayOfPreviousDeparture == -1 ? sortedDepartures.get(0) : dayOfPreviousDeparture; 
		
		Vessel vessel = problemData.getVesselByNumber(vesselNum);
		Voyage previousVoyage = phenotype.getGiantTour().get(dayOfPreviousDeparture).get(vessel);

		// If previous starts later than dayToMoveTo, subtract nDays to wrap around week
		dayOfPreviousDeparture = dayOfPreviousDeparture > dayToMoveTo ? dayOfPreviousDeparture - nDays : dayOfPreviousDeparture;

		int durationOfPreviousVoyage = previousVoyage.getDurationDays();
		int dayOfReturnOfPreviousVoyage = dayOfPreviousDeparture + durationOfPreviousVoyage;
		
		// Does vessel return from previous voyage in time to start a new voyage on dayToMoveTo? 
		if (dayOfReturnOfPreviousVoyage <= dayToMoveFrom){
			return true;
		}
		// Does vessel depart on day after dayToMoveTo? Disregard dayToMoveFrom
		int dayAfter = (dayToMoveTo + 1) % 7;
		if (dayAfter == dayToMoveFrom || !departureDaysForVessel.contains(dayAfter)){
			return true;
		}
		return false;
	}
	
	private int findBestVesselToMoveVoyage(int dayToMoveFrom, int dayToMoveTo, HashMap<Integer, Set<Integer>> vesselPattern, Phenotype phenotype){
		int nDays = problemData.getLengthOfPlanningPeriod();
		
		/*
		 * For all vessels
		 * 		if (canMoveVoyageToDayAndVessel)
		 * 			find slack until next departure, not counting dayToMoveFrom
		 */
		
		int maxSlack = 0;
		int bestVessel = -1;
		
		for (Integer vessel : vesselPattern.keySet()){
			Set<Integer> departureDaysForVessel = vesselPattern.get(vessel);
			
			if (!canMoveVoyageToDayAndVessel(dayToMoveFrom, dayToMoveTo, vessel, departureDaysForVessel, phenotype)){
				continue;
			}
			
			ArrayList<Integer> sortedDepartures = new ArrayList<>(departureDaysForVessel);
			sortedDepartures.remove(Integer.valueOf(dayToMoveFrom)); // Do not consider dayToMoveFrom, assume that voyage can be removed from here
			sortedDepartures.add(dayToMoveTo);
			Collections.sort(sortedDepartures);
			int index = sortedDepartures.indexOf(dayToMoveTo);
			
			// Find number of days until next departure
			int indexNext = (index + 1) % sortedDepartures.size();
			int slack = sortedDepartures.get(indexNext) - sortedDepartures.get(index);
			slack = (slack + nDays) % nDays;
			
			if(slack > maxSlack){
				maxSlack = slack;
				bestVessel = vessel;
			}
		}
		return bestVessel;
		
	}
	
	public HashMap<Integer, Integer> getRequiredNumberOfVoyagesPerDay(){ // Key=day, value=numberOfVoyagesPerDay
		HashMap<Integer, Integer> requiredVoyagesPerDayBaseline = new HashMap<>();
		HashMap<Integer, Set<Integer>> instDeparturesPerDayBaseline = Utilities.getReversedHashMap(problemData.getBaselineInstallationPattern());
		
		for (int day = 0; day < problemData.getLengthOfPlanningPeriod(); day++){
			if (!instDeparturesPerDayBaseline.containsKey(day)){
				instDeparturesPerDayBaseline.put(day, new HashSet<Integer>());
			}
		}
		
		int maxInstallationsPerVoyage = problemData.getMaxInstallationsPerVoyage();
		
		for (Integer day : instDeparturesPerDayBaseline.keySet()){
			double departuresOnDay = instDeparturesPerDayBaseline.get(day).size();
			int requiredVoyages = (int) Math.round(Math.ceil(departuresOnDay / maxInstallationsPerVoyage));
			requiredVoyagesPerDayBaseline.put(day, requiredVoyages);
		}
		return requiredVoyagesPerDayBaseline;
	}
	
	
	// ================================== METHODS BELOW HERE ARE NOT USED AND UNFINISHED =====================================
	//
	// METHOD ON ICE
	private void swapInstallationPattern(Individual individual){
		/*
		 * Pick an installation i with wrong pattern
		 * Pick another installation with same frequency
		 * Swap installation patterns
		 * If persistence is better, keep new pattern, inserting visits at least cost positions 
		 */
		HashMap<Integer, Set<Integer>> baselinePattern = problemData.getBaselineInstallationPattern();
		HashMap<Integer, Set<Integer>> currentPattern = ((GenotypeHGS) individual.getGenotype()).getInstallationDeparturePatternChromosome();
		
		HashMap<Integer, Set<Integer>> vesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> reversedVesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturesPerDay();
		
		Set<Integer> alreadyCheckedInstallations = new HashSet<>();
		
		while (alreadyCheckedInstallations.size() < problemData.getCustomerInstallations().size()){
			
			Installation inst = Utilities.pickRandomElementFromList(problemData.getCustomerInstallations());
			int instNumber = inst.getNumber();
//			System.out.println("Changing pattern of installation " + instNumber);
			
			if (!baselinePattern.containsKey(instNumber)){
				alreadyCheckedInstallations.add(instNumber);
				continue;
			}
			
			Set<Integer> baselineInstPattern =  baselinePattern.get(instNumber);
			
			if (baselineInstPattern.equals(currentPattern.get(instNumber))){
				// Zero changes in this installation's pattern, or baseline does not contain the installation
//				System.out.println("Baseline = current pattern: ");
//				System.out.println("Baseline: " + baselineInstPattern);
//				System.out.println("Current: " + currentPattern.get(instNumber));
				alreadyCheckedInstallations.add(instNumber);
				continue;
			}
			
			Set<Installation> instsWithSameFrequency = problemData.getInstallationsWithFrequency(inst.getFrequency());
			
			int inst2 = -1;
			for (Installation otherInst : instsWithSameFrequency){
				
			}
			
//			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourWithoutInstallation = getCopyOfGiantTourWithoutInstallation(inst, individual.getGenotype().getGiantTourChromosome());
//			giantTourWithoutInstallation = getCopyOfGiantTourWithoutInstallation(inst2, giantTourWithoutInstallation);
			
			ArrayList<VoyageInsertion> insertions = new ArrayList<>();
			for (Integer day : baselineInstPattern){
//				insertions.add(findLeastCostInsertionOnDay(instNumber, day, giantTourWithoutInstallation, vesselPatterns, reversedVesselPatterns));
			}
			Individual newIndividual = applyInsertions(individual, insertions, inst);
			individual.setGenotypeAndUpdatePenalizedCost(newIndividual.getGenotype(), genoToPhenoConverter, fitnessEvaluationProtocol);
			alreadyCheckedInstallations.add(instNumber);
			break; // Only change one installation at a time
			
			
		}
		
	}
	
	// METHOD ON ICE	
	private void moveOneVisit(Individual individual){
		/*
		 * Pick random installation i
		 * 		Loop through baseline pattern for i
		 * 		Find a day t_base that is missing in current pattern
		 * 		Find a day t_current that is on wrong day
		 * 		Move visit on t_current to t_base
		 * 		If t_base is full, find a visit on t_base that is on wrong day
		 */
		HashMap<Integer, Set<Integer>> baselinePattern = problemData.getBaselineInstallationPattern();
		HashMap<Integer, Set<Integer>> currentPattern = ((GenotypeHGS) individual.getGenotype()).getInstallationDeparturePatternChromosome();
		
		HashMap<Integer, Set<Integer>> vesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> reversedVesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturesPerDay();
		
		Set<Integer> alreadyCheckedInstallations = new HashSet<>();
		
		while (alreadyCheckedInstallations.size() < problemData.getCustomerInstallations().size()){
			
			Installation inst = Utilities.pickRandomElementFromList(problemData.getCustomerInstallations());
			int instNumber = inst.getNumber();
//			System.out.println("Changing pattern of installation " + instNumber);
			
			if (!baselinePattern.containsKey(instNumber)){ // baseline does not contain the installation
				alreadyCheckedInstallations.add(instNumber);
				continue;
			}
			
			Set<Integer> missingDaysForInst =  new HashSet<>(baselinePattern.get(instNumber));
			Set<Integer> wrongDaysForInst = new HashSet<>(currentPattern.get(instNumber));
			
			if (missingDaysForInst.equals(currentPattern.get(instNumber))){
				// Zero changes in this installation's pattern
//				System.out.println("Baseline = current pattern: ");
//				System.out.println("Baseline: " + baselineInstPattern);
//				System.out.println("Current: " + currentPattern.get(instNumber));
				alreadyCheckedInstallations.add(instNumber);
				continue;
			}
			missingDaysForInst.removeAll(wrongDaysForInst); // Departures missing in current solution
			wrongDaysForInst.removeAll(baselinePattern.get(instNumber)); // Departures on wrong days in current solution
			
			int dayToRemove = Utilities.pickRandomElementFromSet(wrongDaysForInst);
			
			int dayToAdd = -1;
			for (Integer day : missingDaysForInst){
				Set<Integer> newPattern = new HashSet<>(currentPattern.get(instNumber));
				newPattern.add(day);
				newPattern.remove(day);
				if (problemData.getInstallationDeparturePatterns().get(instNumber).contains(newPattern)){
					dayToAdd = day;
					break;
				}
			}
			
			if (individual.getPhenotype().existsViolationsOnDay(dayToAdd)){
				// Move visit on dayToAdd to another day
				for (Integer instVisit : individual.getGenotype().getInstallationDeparturesPerDay().get(dayToAdd)){
					Set<Integer> baselineForInst2 = baselinePattern.get(instVisit);
					if (!baselineForInst2.contains(instVisit)){
						
					}
				}
			}
		}
			
	}
	
	
	// NOT IN USE
	public void swapVoyages(Individual individual){
		/*
		 * Pick two random voyages, voyage1 and voyage2 both with same duration (in days) and departing on different days
		 * 
		 * For all installations in voyage1 and voyage2
		 * 		if resulting pattern after swap is infeasible
		 * 			choose new voyages
		 * 		end-if
		 * end-for									
		 * 
		 * if new pattern improves persistence
		 *	 swap voyages in genotype
		 * end-if 
		 */
		PhenotypeHGS phenotype = (PhenotypeHGS) individual.getPhenotype();
		Set<DayVesselCell> copyOfAllDayVesselCells = new HashSet<>(problemData.getAllDayVesselCells());
		List<Set<DayVesselCell>> cellCombinations = new ArrayList<>(Utilities.cartesianProduct(copyOfAllDayVesselCells));
		
		
		System.out.println("CellCombinations:\n" + cellCombinations);
		// All combinations of two DayVesselCells.
		
		Collections.shuffle(cellCombinations); // Shuffle list to make random
		System.out.println("==================== SCHEDULE =========================");
		System.out.println(phenotype.getScheduleString());
		
		// ========================== TESTING REMOVE ==============================
		
		Set<DayVesselCell> wantedCombo = new HashSet<>();
		wantedCombo.add(new DayVesselCell(5,2));
		wantedCombo.add(new DayVesselCell(4,1));
		
		cellCombinations.clear();
		cellCombinations.add(wantedCombo);
		
		for (Set<DayVesselCell> cellCombo : cellCombinations){
			System.out.println("\nTesting cellcombo: " + cellCombo);
			ArrayList<DayVesselCell> cellComboList = new ArrayList<>(cellCombo); 
			
			if (cellCombo.size() != 2){
				System.err.println("Cell combo has wrong size in persistenceEducation/swapVoyages. Cancelling voyage swap.");
				return;
			}
			DayVesselCell cell1 = cellComboList.get(0);
			DayVesselCell cell2 = cellComboList.get(1);
			
			if (cell1.day == cell2.day){
				System.out.println("Cells on same day");
				continue; // Need to swap between different days to have any effect on persistence
			}
			
			Vessel vessel1 = problemData.getVesselByNumber(cell1.vessel);
			Vessel vessel2 = problemData.getVesselByNumber(cell2.vessel);
			
			Voyage voyage1 = phenotype.getGiantTour().get(cell1.day).get(vessel1);
			Voyage voyage2 = phenotype.getGiantTour().get(cell2.day).get(vessel2);
			
			if (voyage1 == null || voyage2 == null){
				System.out.println("One voyage is empty");
				continue; // If there is no voyage in either cell, try other combo
			}
			
			if (voyage1.getDurationDays() != voyage2.getDurationDays()){
				System.out.println("Voyages have different durations");
				System.out.println("Voy1: " + voyage1.getDurationDays() + " days, Voy2: " + voyage2.getDurationDays() + " days");
				System.out.println("Voy1: " + voyage1.getDuration() + " hours, Voy 2: " + voyage2.getDuration() + " hours");
				continue; // Only swap voyages with equal length, to avoid feasibility issues
			}
			if (!isFeasibleVoyageSwap(cell1, cell2, voyage1, voyage2, individual)){
				System.out.println("New pattern is infeasible");
				continue; // Check if the resulting installation patterns are feasible
			}
			
			Genotype improvedGenotype = getImprovedGenotype(individual, cell1, cell2);
			if (improvedGenotype != null){
				
//				System.out.println("Swapping " + cell1 + " and " + cell2);
//				System.out.println("Voyage 1: " + voyage1.getVisitedInstallations());
//				System.out.println("Voyage 2: " + voyage2.getVisitedInstallations());
				individual.setGenotypeAndUpdatePenalizedCost(improvedGenotype, genoToPhenoConverter, fitnessEvaluationProtocol);
				return;
			} // Else: try swapping two other voyages
		}
	}
	// NOT IN USE
	private Genotype getImprovedGenotype(Individual individual, DayVesselCell cell1, DayVesselCell cell2) {
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> tourChromosome = Utilities.deepCopyGiantTour(individual.getGenotype().getGiantTourChromosome());

		int numberOfChangesBeforeSwap = individual.getNumberOfChangesFromBaseline();
		
		ArrayList<Integer> voyage1 = tourChromosome.get(cell1.day).get(cell1.vessel);
		ArrayList<Integer> voyage2 = tourChromosome.get(cell2.day).get(cell2.vessel);
		
		tourChromosome.get(cell1.day).put(cell1.vessel, new ArrayList<>(voyage2));
		tourChromosome.get(cell2.day).put(cell2.vessel, new ArrayList<>(voyage1));
		
		int nInstallations = problemData.getCustomerInstallations().size();
		int nVessels = problemData.getVessels().size();
		Individual newIndividual = new Individual(new GenotypeHGS(tourChromosome, nInstallations, nVessels));
		((GenoToPhenoConverterMultiObjective) genoToPhenoConverter).convertGenotypeToPhenotype(newIndividual);
		
		System.out.println("Trying to swap voyage " + cell1 + " and " + cell2);
		System.out.println("Old persistence: " + numberOfChangesBeforeSwap);
		System.out.println("New persistence: " + newIndividual.getNumberOfChangesFromBaseline());
		
		if (newIndividual.getNumberOfChangesFromBaseline() < numberOfChangesBeforeSwap){
			return newIndividual.getGenotype();
		}
		else {
			return null;
		}
	}

	// NOT IN USE
	private boolean isFeasibleVoyageSwap(DayVesselCell cell1, DayVesselCell cell2, Voyage voyage1, Voyage voyage2, Individual individual){
		
		GenotypeHGS genotype = (GenotypeHGS) individual.getGenotype();
		HashMap<Integer, Set<Set<Integer>>> feasibleInstPatterns = problemData.getInstallationDeparturePatterns();
		
//		System.out.println("==================== SWAP =================");
//		System.out.println("Cell1: " + cell1 + " Cell2: " + cell2);
		
		// Check installation patterns for voyage1
		for (Installation inst : voyage1.getVisitedInstallations()){
			int instNum = inst.getNumber();
			Set<Integer> instPattern = new HashSet<>(genotype.getInstallationDeparturePatternChromosome().get(instNum));
			
			System.out.println("Old pattern for installation " + instNum + ":");
			System.out.println(instPattern);
			
			if (!voyage2.getVisitedInstallations().contains(inst)){
				instPattern.remove(cell1.day);
			}
			instPattern.add(cell2.day);
			
			System.out.println("New pattern:");
			System.out.println(instPattern);
			
			if (!feasibleInstPatterns.get(inst.getFrequency()).contains(instPattern)){
				System.out.println(instPattern + " is not feasible");
				return false;
			}
		}
		// Check installation patterns for voyag2
		for (Installation inst : voyage2.getVisitedInstallations()){
			int instNum = inst.getNumber();
			Set<Integer> instPattern = new HashSet<>(genotype.getInstallationDeparturePatternChromosome().get(instNum));
			
			System.out.println("Old pattern for installation " + instNum + ":");
			System.out.println(instPattern);
			
			if (!voyage1.getVisitedInstallations().contains(inst)){
				instPattern.remove(cell2.day);
			}
			instPattern.add(cell1.day);
			System.out.println("New pattern:");
			System.out.println(instPattern);
			
			if (!feasibleInstPatterns.get(inst.getFrequency()).contains(instPattern)){
				System.out.println(instPattern + " is not feasible");
				return false;
			}
		}
		return true;
	}
}
