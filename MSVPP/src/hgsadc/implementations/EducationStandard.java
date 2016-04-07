package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.EducationProtocol;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class EducationStandard implements EducationProtocol {

	private ProblemData problemData;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private PenaltyAdjustmentProtocol penaltyAdjustmentProtocol;
	private GenoToPhenoConverterProtocol genoToPhenoConverter;
	private boolean isRepair;
	private int penaltyMultiplier;
	
	public EducationStandard(ProblemData problemdata, FitnessEvaluationProtocol fitnessEvaluationProtocol, PenaltyAdjustmentProtocol penaltyAdjustmentProtocol, GenoToPhenoConverterProtocol genoToPhenoConverter) {
		this.problemData = problemdata;
		this.fitnessEvaluationProtocol = fitnessEvaluationProtocol;
		this.penaltyAdjustmentProtocol = penaltyAdjustmentProtocol;
		this.genoToPhenoConverter = genoToPhenoConverter;
		this.isRepair = false;
		this.penaltyMultiplier = 1;
	}
	
	@Override
	public void educate(Individual individual) {
		routeImprovement(individual);
		patternImprovement(individual);
		routeImprovement(individual);
		
		if (!isRepair){
			penaltyAdjustmentProtocol.countAddedIndividual(individual);
		}
	}
	
	@Override
	public void repairEducate(Individual individual, int penaltyMultiplier){
		isRepair = true;
		this.penaltyMultiplier = penaltyMultiplier;
		
		//System.out.println("Repairing individual " + individual);
		educate(individual);
		
		isRepair = false;
		this.penaltyMultiplier = 1;
	}
	
	private void routeImprovement(Individual individual) {
		HashMap<Integer, HashMap<Vessel, Voyage>> giantTour = individual.getPhenotype().getGiantTour();
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = individual.getGenotype().getGiantTourChromosome();
		for (Integer day : giantTour.keySet()) {
			for (Vessel vessel : giantTour.get(day).keySet()) {
				Voyage voyage = giantTour.get(day).get(vessel);
				if (voyage != null) {
					Voyage improvedVoyage = getImprovedRoute(voyage);
					giantTour.get(day).put(vessel, improvedVoyage);
					giantTourChromosome.get(day).put(vessel.getNumber(), new ArrayList<Integer>(improvedVoyage.getInstallations()));
				}
			}
		}
	}
	
	private Voyage getImprovedRoute(Voyage voyage) {
		ArrayList<Integer> installations = new ArrayList<Integer>(voyage.getInstallations());
		ArrayList<Integer> untreatedInstallations = new ArrayList<Integer>(voyage.getInstallations());
		while (untreatedInstallations.size() > 0) {
			Integer u = Utilities.pickAndRemoveRandomElementFromList(untreatedInstallations);
			ArrayList<Integer> neighbours = getNeighbours(u, installations);
			while (neighbours.size() > 0) {
				Integer v = Utilities.pickAndRemoveRandomElementFromList(neighbours);
				installations = doRandomMove(u, v, installations, voyage);
			}
		}
		return new Voyage(installations, voyage.getVessel(), voyage.getVesselDeparturePattern(), voyage.getDepartureDay(), problemData);
	}
	
	private ArrayList<Integer> doRandomMove(Integer u, Integer v, ArrayList<Integer> installations, Voyage voyage) {
		//make a list of unused moves
		ArrayList<Integer> unusedMoves = new ArrayList<Integer>();
		Move move = new Move();
		for (int i = 0; i < move.getNumberOfMoves(); i++) {
			unusedMoves.add(i+1); //the moves are 1-indexed, as in Vidal et al 2012
		}
		//attempt the moves in random order
		while (unusedMoves.size() > 0) {
			int moveNumber = Utilities.pickAndRemoveRandomElementFromList(unusedMoves);
			ArrayList<Integer> newInstallations = move.doMove(u, v, installations, moveNumber);
			Voyage newVoyage = new Voyage(newInstallations, voyage.getVessel(), voyage.getVesselDeparturePattern(), voyage.getDepartureDay(), problemData);
			double oldVoyagePenalizedCost;
			double newVoyagePenalizedCost;
			if (! isRepair) {//normal education
				oldVoyagePenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(voyage);
				newVoyagePenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(newVoyage);
			}
			else {//repair education
				double durationViolationPenalty = fitnessEvaluationProtocol.getDurationViolationPenalty() * penaltyMultiplier;
				double capacityViolationPenalty = fitnessEvaluationProtocol.getCapacityViolationPenalty() * penaltyMultiplier;
				double numberOfInstallationsViolationPenalty = fitnessEvaluationProtocol.getNumberOfInstallationsPenalty() * penaltyMultiplier;
				oldVoyagePenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(voyage,durationViolationPenalty,capacityViolationPenalty, numberOfInstallationsViolationPenalty);
				newVoyagePenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(newVoyage,durationViolationPenalty,capacityViolationPenalty, numberOfInstallationsViolationPenalty);
			}
			if (newVoyagePenalizedCost < oldVoyagePenalizedCost) {
				return newInstallations; 
			}
		}
		return installations;
	}
	
	private ArrayList<Integer> getNeighbours(Integer installation, ArrayList<Integer> installations) {
		double granularityTreshold = problemData.getHeuristicParameterDouble("Granularity threshold in RI");
		int numberOfNeighbours = (int) (installations.size()*granularityTreshold);
		ArrayList<Integer> neighbours = new ArrayList<Integer>(installations); //create set of neighbours
		neighbours.remove(installation); //remove this installation from the set
		//get the distance from all other installations to this installation as key,value-pairs 
		ArrayList<Map.Entry<Installation, Double>> distances = new ArrayList<Map.Entry<Installation, Double>>(problemData.getDistances().get(problemData.getInstallationByNumber(installation)).entrySet());
		//remove key-value-pairs that are not neighbours 
		ArrayList<Map.Entry<Installation, Double>> removeList = new ArrayList<Map.Entry<Installation,Double>>();
		for (Map.Entry<Installation, Double> distance : distances) {
			if (! neighbours.contains(distance.getKey().getNumber())) {
				removeList.add(distance);
			}
		}
		distances.removeAll(removeList);
		//sort the key-value-pairs by distance, having the pairs with the highest distance first
		Collections.sort(distances, Collections.reverseOrder(Utilities.getMapEntryWithDoubleComparator()));
		//removes the neighbours with highest distance until the correct number of neighbours is obtained
		while (neighbours.size() > numberOfNeighbours) {
			neighbours.remove(Integer.valueOf(distances.remove(0).getKey().getNumber()));
		}
		return neighbours;
	}

	private void patternImprovement(Individual individual) {
		installationPatternImprovement(individual);
		vesselPatternImprovement(individual);
		installationPatternImprovement(individual);
	}

	private void vesselPatternImprovement(Individual individual) {
		/*
		for each day t with more than one vessel departure
			for all combinations of two voyages departing that day
			        Calculate penalized cost of merging the two voyages (inserting each installation at best position in each voyage)
			end-for
			if best merge has reduced cost
				Choose and perform best merge
			end-if
		end-for
		*/
//		System.out.println("\nRunning vessel pattern improvement");
		HashMap<Integer, Set<Integer>> reversedVesselDepartures = individual.getVesselDeparturesPerDay();
		
		for (Integer day : reversedVesselDepartures.keySet()) {
			// If more than one departure on that day
			if (reversedVesselDepartures.get(day).size() > 1){ 
				Set<Integer> vesselsDepartingOnDay = reversedVesselDepartures.get(day);
				Set<Set<Integer>> allVesselCombinationsForDay = Utilities.cartesianProduct(vesselsDepartingOnDay);
				
				int bestVesselToKeep = -1;
				int bestVesselToRemove = -1;
				Voyage bestNewVoyage = null;
				double bestCostReduction = 0;
				
				
				for (Set<Integer> vesselPair : allVesselCombinationsForDay){
					
					// Randomly select which vessel to remove, and which to keep 
					Integer vesselNumberToKeep = Utilities.pickAndRemoveRandomElementFromSet(vesselPair);
					Vessel vesselToKeep = problemData.getVesselByNumber(vesselNumberToKeep);
					Integer vesselNumberToRemove = Utilities.pickAndRemoveRandomElementFromSet(vesselPair);
					Vessel vesselToRemove = problemData.getVesselByNumber(vesselNumberToRemove);
					
					Voyage voyageToMergeInto = individual.getPhenotype().getGiantTour().get(day).get(vesselToKeep);
					Voyage voyageToMove = individual.getPhenotype().getGiantTour().get(day).get(vesselToRemove);
					
					double currentPenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(voyageToMergeInto) + fitnessEvaluationProtocol.getPenalizedCost(voyageToMove);
					
					ArrayList<Integer> voyageSeq = new ArrayList<>(voyageToMergeInto.getInstallations());
					// Insert each installation in voyageToMove into voyageToMergeInto
					for (Integer installation : voyageToMove.getInstallations()) {
						VoyageInsertion bestInsertion = getBestInsertionIntoVoyage(installation, voyageToMergeInto);
						int bestPos = bestInsertion.positionInVoyageToInsertInto;
						voyageSeq.add(bestPos, installation);
					}
					
					Voyage newVoyage = new Voyage(voyageSeq, vesselToKeep, voyageToMergeInto.getVesselDeparturePattern(), day, problemData);
					double newPenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(newVoyage);
					double costReduction = currentPenalizedCost - newPenalizedCost;
					
					if (costReduction > bestCostReduction){
						bestVesselToKeep = vesselNumberToKeep;
						bestVesselToRemove = vesselNumberToRemove;
						bestNewVoyage =  newVoyage;
						bestCostReduction = costReduction;
					}
				}
				// If there exists a merger resulting in reduced cost, change giant tour
				if (bestCostReduction > 0){
					// Copy current giantTour
					HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour = Utilities.deepCopyGiantTour(individual.getGenotype().getGiantTourChromosome());
					
					giantTour.get(day).put(bestVesselToKeep, bestNewVoyage.getInstallations()); // Add merged voyage to giantTour 
					giantTour.get(day).put(bestVesselToRemove, new ArrayList<>()); // Remove voyage from giantTour
					Genotype newGenotype = new GenotypeHGS(giantTour, problemData.getCustomerInstallations().size(), problemData.getVessels().size());
					// Update individual
//					System.out.println("Genotype before merger: ");
//					System.out.println(GenotypeHGS.getGiantTourString(individual.getGenotype().getGiantTourChromosome()));
//					System.out.println("\nMerging vessel " + bestVesselToRemove + " into vessel " + bestVesselToKeep + " on day " + day);
					individual.setGenotypeAndUpdatePenalizedCost(newGenotype, genoToPhenoConverter, fitnessEvaluationProtocol);
//					System.out.println("Genotype after merger: ");
//					System.out.println(GenotypeHGS.getGiantTourString(individual.getGenotype().getGiantTourChromosome()));
				}
			}
		}
	}


	private void installationPatternImprovement(Individual individual) {
		int iterationsWithoutChange = 0;
		int nInstallations = problemData.getCustomerInstallations().size();
		HashMap<Integer, Set<Set<Integer>>> patterns = problemData.getInstallationDeparturePatterns();
		HashMap<Integer, Set<Integer>> reversedVesselDepartures = individual.getVesselDeparturesPerDay();
		Set<Integer> daysWithVesselDeparture = individual.getDaysWithVesselDeparture();
		HashMap<Integer, Set<Integer>> vesselDeparturePattern = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturePatternChromosome();
		
//		System.out.println("Installation pattern improvement...");
		while (iterationsWithoutChange < nInstallations){
//			System.out.println("Iterations without change: " + iterationsWithoutChange + " no of installations: " + nInstallations);
			for (Installation installation : problemData.getCustomerInstallations()){
//				System.out.println("Changing pattern of installation " + installation.getNumber());
				HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourWithoutInstallation = getCopyOfGiantTourWithoutInstallation(installation, individual);
				int frequency = installation.getFrequency();

				double bestPatternCost = Double.MAX_VALUE;
				ArrayList<VoyageInsertion> bestInsertions = new ArrayList<>();
				
				// Loop through all feasible patterns for installation
				for (Set<Integer> instPattern : patterns.get(frequency)){
					// If the current vessel pattern allows this installation pattern
					if (daysWithVesselDeparture.containsAll(instPattern)){
//						System.out.println("Finding best insertions for pattern " + instPattern.toString());
						ArrayList<VoyageInsertion> patternInsertions = findBestInsertionsForPattern(instPattern, installation.getNumber(), giantTourWithoutInstallation, vesselDeparturePattern, reversedVesselDepartures);
						double patternInsertionCost = VoyageInsertion.getTotalInsertionCost(patternInsertions);
						
						if (patternInsertionCost < bestPatternCost){
							bestInsertions = patternInsertions;
							bestPatternCost = patternInsertionCost;
						}
					}
				}
				Individual newIndividual = applyInsertions(individual, bestInsertions, installation);
				// If the pattern change leads to a better solution, change genotype of individual
				if (newIndividual.getPenalizedCost() < individual.getPenalizedCost()){
					individual.setGenotypeAndUpdatePenalizedCost(newIndividual.getGenotype(), genoToPhenoConverter, fitnessEvaluationProtocol);
					iterationsWithoutChange = 0;
				}
				else {
					iterationsWithoutChange++;
//					System.out.println("No change this iteration, iterations without change: " + iterationsWithoutChange);
				}
			}
		}
	}
	
	private Individual applyInsertions(Individual individual, ArrayList<VoyageInsertion> bestInsertions, Installation installation) {
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour = getCopyOfGiantTourWithoutInstallation(installation, individual);
		
		for (VoyageInsertion voyageInsertion : bestInsertions) {
			int day = voyageInsertion.dayVesselCell.day;
			int vessel = voyageInsertion.dayVesselCell.vessel;
			int pos = voyageInsertion.positionInVoyageToInsertInto;
			int installationNumber = voyageInsertion.installationNumber;
			giantTour.get(day).get(vessel).add(pos, installationNumber);
		}
		
		int nInstallations = problemData.getInstallations().size();
		int nVessels = problemData.getVessels().size();
		
		Individual newIndividual = new Individual(new GenotypeHGS(giantTour, nInstallations, nVessels));
		genoToPhenoConverter.convertGenotypeToPhenotype(newIndividual);
		fitnessEvaluationProtocol.setPenalizedCostIndividual(newIndividual);
		
		return newIndividual;
	}

	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getCopyOfGiantTourWithoutInstallation(
			Installation installation, Individual individual) {
		
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourCopy = Utilities.deepCopyGiantTour(individual.getGenotype().getGiantTourChromosome());
		
		for (Integer day : giantTourCopy.keySet()){
			HashMap<Integer, ArrayList<Integer>> daySchedule = giantTourCopy.get(day);
			
			for (ArrayList<Integer> voyage : daySchedule.values()){
				voyage.remove(Integer.valueOf(installation.getNumber()));
			}
		}
		
		return giantTourCopy;
	}

	private ArrayList<VoyageInsertion> findBestInsertionsForPattern(Set<Integer> installationPattern, int installation, HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourWithoutInstallation, HashMap<Integer, Set<Integer>> vesselPatterns, HashMap<Integer, Set<Integer>> reversedVesselPatterns){
		
		ArrayList<VoyageInsertion> bestInsertions = new ArrayList<>();
		double totalCost = 0;

		// For each day in installationPattern, find the optimal cell to insert the installation
		for (Integer day : installationPattern) {
			
			double bestInsertionCostOnDay = Double.MAX_VALUE;
			int bestVesselOnDay = 0;
			int bestPos  = -1;
			
			// Loop through all vessels departing on that day
			for (Integer vesselNumber : reversedVesselPatterns.get(day)){
				Vessel vessel = problemData.getVesselByNumber(vesselNumber);
				Set<Integer> vesselPattern = vesselPatterns.get(vesselNumber);
				ArrayList<Integer> currentVoyageSeq = giantTourWithoutInstallation.get(day).get(vesselNumber);
				
				Voyage currentVoyage = new Voyage(currentVoyageSeq, vessel, vesselPattern, day, problemData);
				
				// Find best position in voyage to insert installation
				VoyageInsertion bestInsertionForDayVesselCell = getBestInsertionIntoVoyage(installation, currentVoyage);
				
				// If this vessel is better than other vessels for that day
				if (bestInsertionForDayVesselCell.insertionCost < bestInsertionCostOnDay){
					bestInsertionCostOnDay = bestInsertionForDayVesselCell.insertionCost;
					bestVesselOnDay = bestInsertionForDayVesselCell.dayVesselCell.vessel;
					bestPos = bestInsertionForDayVesselCell.positionInVoyageToInsertInto;
				}
			}
			// Save best insertion for that day
			DayVesselCell bestCell = new DayVesselCell(day, bestVesselOnDay);
			bestInsertions.add(new VoyageInsertion(bestCell, installation, bestPos, bestInsertionCostOnDay));
		}
		return bestInsertions;
	}
	
	public VoyageInsertion getBestInsertionIntoVoyage(int installation, Voyage voyage){
		
		ArrayList<Integer> currentVoyageSeq = voyage.getInstallations();
		Vessel vessel = voyage.getVessel();
		Set<Integer> vesselPattern = voyage.getVesselDeparturePattern();
		int day = voyage.getDepartureDay();
		double currentPenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(voyage);
		
		double bestInsertionCostForVoyage = Double.MAX_VALUE;
		int bestPosInVoyage = -1;
		
		for (int pos = 0; pos <= currentVoyageSeq.size(); pos++){
			ArrayList<Integer> newVoyageSeq = new ArrayList<>(currentVoyageSeq);
			newVoyageSeq.add(pos, installation);
			Voyage newVoyage = new Voyage(newVoyageSeq, vessel, vesselPattern, day, problemData);
			double newPenalizedCost = fitnessEvaluationProtocol.getPenalizedCost(newVoyage);
			double insertionCost = newPenalizedCost - currentPenalizedCost;
			
			// If this position is better than previous position in Voyage
			if (insertionCost < bestInsertionCostForVoyage) {
				bestInsertionCostForVoyage = insertionCost;
				bestPosInVoyage = pos;
			}
		}
		DayVesselCell cell = new DayVesselCell(day, vessel.getNumber());
		return new VoyageInsertion(cell, installation, bestPosInVoyage, bestInsertionCostForVoyage); 
	}
}
