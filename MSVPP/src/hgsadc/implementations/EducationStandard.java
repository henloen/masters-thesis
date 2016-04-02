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
import java.util.Set;

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
				Voyage improvedVoyage = getImprovedRoute(voyage);
				giantTour.get(day).put(vessel, improvedVoyage);
				giantTourChromosome.get(day).put(vessel.getNumber(), improvedVoyage.getInstallations());
			}
		}
	}
	
	private Voyage getImprovedRoute(Voyage voyage) {
		ArrayList<Integer> installations = new ArrayList<Integer>(voyage.getInstallations());
		ArrayList<Integer> untreatedInstallations = new ArrayList<Integer>(voyage.getInstallations());
		while (untreatedInstallations.size() > 0) {
			Integer u = Utilities.pickAndRemoveRandomElementFromList(untreatedInstallations);
			Integer x = getSuccessor(u, installations);
			ArrayList<Integer> neighbours = getNeighbours(u, installations);
			while (neighbours.size() > 0) {
				Integer v = Utilities.pickAndRemoveRandomElementFromList(neighbours);
				Integer y = getSuccessor(v, installations);
				doRandomMove(u, v, x, y, installations);
			}
		}
		return new Voyage(installations, voyage.getVessel(), voyage.getVesselDeparturePattern(), voyage.getDepartureDay(), problemData);
	}
	
	private Integer getSuccessor(Integer installation, ArrayList<Integer> installations) {
		int indexOfInstallation = installations.indexOf(installations);
		if (indexOfInstallation == (installations.size()-1)) { //the installation has no successor if it's the last element
			return null;
		}
		else {
			return installations.get(indexOfInstallation+1);
		}
	}
	
	private ArrayList<Integer> getNeighbours(Integer installation, ArrayList<Integer> installations) {
		double granularityTreshold = problemData.getHeuristicParameterDouble("Granularity threshold in RI");
		int numberOfNeighbours = (int) (installations.size()*granularityTreshold);
		ArrayList<Integer> neighbours = new ArrayList<Integer>(installations); //create set of neighbours
		neighbours.remove(installation); //remove this installation from the set
		//get the distance from all other installations to this installation as key,value-pairs 
		ArrayList<Map.Entry<Installation, Double>> distances = new ArrayList<Map.Entry<Installation, Double>>(problemData.getDistances().get(problemData.getInstallationByNumber(installation)).entrySet());
		//remove key-value-pairs that are not neighbours 
		for (Map.Entry<Installation, Double> distance : distances) {
			if (! neighbours.contains(distance.getKey().getNumber())) {
				distances.remove(distance);
			}
		}
		//sort the key-value-pairs by distance, having the pairs with the highest distance first
		Collections.sort(distances, Collections.reverseOrder(Utilities.getMapEntryWithDoubleComparator()));
		//removes the neighbours with highest distance until the correct number of neighbours is obtained
		while (neighbours.size() > numberOfNeighbours) {
			neighbours.remove(distances.remove(0).getKey().getNumber());
		}
		return neighbours;
	}
	
	
	private void doRandomMove(Integer u, Integer v, Integer x, Integer y, ArrayList<Integer> installations) {
		int numberOfMoves = 6;
		ArrayList<Integer> unusedMoves = new ArrayList<Integer>();
		for (int i = 0; i < numberOfMoves; i++) {
			unusedMoves.add(i);
		}
		
	}
	
	
	private void patternImprovement(Individual individual) {
		//TODO
	}

}
