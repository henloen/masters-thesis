package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.Vessel;
import hgsadc.Voyage;
import hgsadc.protocols.EducationProtocol;
import hgsadc.protocols.FitnessEvaluationProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EducationStandard implements EducationProtocol {

	private ProblemData problemData;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private boolean isRepair;
	private int penaltyMultiplier;
	
	public EducationStandard(ProblemData problemdata, FitnessEvaluationProtocol fitnessEvaluationProtocol) {
		this.problemData = problemdata;
		this.fitnessEvaluationProtocol = fitnessEvaluationProtocol;
	}
	
	@Override
	public void educate(Individual individual) {
		routeImprovement(individual);
		patternImprovement(individual);
		routeImprovement(individual);
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
					giantTourChromosome.get(day).put(vessel.getNumber(), improvedVoyage.getInstallations());
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
		//TODO
	}

}
