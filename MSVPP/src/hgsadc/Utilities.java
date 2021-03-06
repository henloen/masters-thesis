package hgsadc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hgsadc.implementations.DayVesselCell;
import hgsadc.implementations.Dominator;

public class Utilities {
	
	public static <T> T pickRandomElementFromSet(Set<T> set) {
		int randomIndex = new Random().nextInt(set.size());
		int counter = 0;
		for (T t : set) {
			if (counter == randomIndex){
				return t;
			}
			counter++;
		}
		return null;
	}
	
	public static <T> T pickRandomElementFromList(List<T> list) {
		int randomIndex = new Random().nextInt(list.size());
		return list.get(randomIndex);
	}
	
	public static double parseDouble(String commaSeparatedDouble) {
		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		df.setDecimalFormatSymbols(symbols);
		try {
			return df.parse(commaSeparatedDouble).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
			System.out.println("Something went wrong when parsing doubles");
			return -1.0;
		}
	}
	
	public static <T> boolean setIsSubsetOfAnySetInCollection(Set<T> setToLookFor, Collection<Set<T>> collectionOfSets){
		for (Set<T> set : collectionOfSets) {
			if (set.containsAll(setToLookFor)) return true;
		}
		return false;
	}
	
	public static <T> ArrayList<T> getAllElements(ArrayList<T> list1, ArrayList<T> list2) {
		ArrayList<T> allElements = new ArrayList<T>();
		allElements.addAll(list1);
		allElements.addAll(list2);
		return allElements;
	}
	
	public static <K> Comparator<Map.Entry<K, Double>> getMapEntryWithDoubleComparator() {
		return new Comparator<Map.Entry<K, Double>>() {
			public int compare(Map.Entry<K, Double> o1, Map.Entry<K,Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		};
	}

	public static <T> T pickAndRemoveRandomElementFromSet(Set<T> set) {
		int randomIndex = new Random().nextInt(set.size());
		int counter = 0;
		for (T t : set) {
			if (counter == randomIndex){
				set.remove(t);
				return t;
			}
			counter++;
		}
		return null;
	}

	public static String printCells(Set<DayVesselCell> setOfCells) {
		String str = "";
		for (DayVesselCell cell : setOfCells) {
			str += cell.toString() + " ";
		}
		return str;
	}

	public static <T> T pickAndRemoveRandomElementFromList(
			ArrayList<T> list) {
		T element = pickRandomElementFromList(list);
		list.remove(element);
		return element;
	}
	
	public static HashMap<Integer, Set<Integer>> getReversedHashMap(HashMap<Integer, Set<Integer>> hashMap) {
		HashMap<Integer, Set<Integer>> reversedHashMap = new HashMap<Integer, Set<Integer>>();
		for (Integer key : hashMap.keySet()) {
			for (Integer value : hashMap.get(key)) {
				Set<Integer> existingSet = reversedHashMap.get(value);
				if (existingSet == null) {
					existingSet = new HashSet<Integer>();
				}
				existingSet.add(key);
				reversedHashMap.put(value, existingSet);
			}
		}
		return reversedHashMap;
	}

	public static HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> deepCopyGiantTour(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour) {

		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourCopy = new HashMap<>();
		
		for (Integer day : giantTour.keySet()) {
			HashMap<Integer, ArrayList<Integer>> dayDepartures = giantTour.get(day);
			HashMap<Integer, ArrayList<Integer>> dayDeparturesCopy = new HashMap<>();
			
			for (Integer vessel : dayDepartures.keySet()){
				ArrayList<Integer> voyageCopy = new ArrayList<>(dayDepartures.get(vessel));
				dayDeparturesCopy.put(vessel, voyageCopy);
			}
			giantTourCopy.put(day, dayDeparturesCopy);
		}
		return giantTourCopy;
	}
	
	public static HashMap<Integer, Set<Integer>> deepCopyDepartureChromosome(HashMap<Integer, Set<Integer>> departureChromosome){
		HashMap<Integer, Set<Integer>> chromosomeCopy = new HashMap<Integer, Set<Integer>>();
		
		for (int key : departureChromosome.keySet()) {
			Set<Integer> setCopy = new HashSet<>();
			for (int i : departureChromosome.get(key)){
				setCopy.add(i);
			}
			chromosomeCopy.put(key, setCopy);
		}
		return chromosomeCopy;
		
	}
	
	/**
	 * 
	 * @param set
	 * @return Returns cartesian product of itself, i.e. set x set, excluding those pairs where both elements in a pair are equal
	 */
	public static <T> Set<Set<T>> cartesianProduct(Set<T> set) {
		HashSet<Set<T>> cartProduct = new HashSet<>();
		for (T element : set) {
			for (T element2 : set){
				if (element != element2){
					HashSet<T> pair = new HashSet<T>();
					pair.add(element);
					pair.add(element2);
					cartProduct.add(pair);
				}
			}
		}
		return cartProduct;
	}

	public static ArrayList<Set<Individual>> nonDominatedSorting(Collection<Individual> population, Dominator dominator){
		ArrayList<Set<Individual>> paretoFronts = new ArrayList<>();
		Set<Individual> paretoFront1 = new HashSet<>();
		
		HashMap<Individual, Integer> dominationCount = new HashMap<>(); // Number of individuals that dominates the individual
		HashMap<Individual, Set<Individual>> dominatedSet = new HashMap<>(); // Set of individuals the individual dominates
		
		for (Individual ind: population){
			Set<Individual> indDominatesSet = Utilities.getIndividualsDominatedBy(ind, population, dominator);
			Set<Individual> indDominatedBySet = Utilities.getIndidividualsDominating(ind, population, dominator);
			int domCount = indDominatedBySet.size();
			
			dominationCount.put(ind, domCount);
			dominatedSet.put(ind, indDominatesSet);
			
			if (domCount == 0){
				paretoFront1.add(ind);
			}
		}
		
		paretoFronts.add(paretoFront1); // First non-dominated front
		
		Set<Individual> previousFront = paretoFront1;
		while ( !previousFront.isEmpty()){ 
			Set<Individual> nextFront = new HashSet<>();
			for (Individual dominatorInd : previousFront){
				for (Individual dominatedInd : dominatedSet.get(dominatorInd)){
					int domCount = dominationCount.get(dominatedInd);
					dominationCount.put(dominatedInd, domCount-1); // Reduce domination count by 1
					if (domCount-1 == 0){
						nextFront.add(dominatedInd);
					}
				}
			}
			previousFront = nextFront;
			paretoFronts.add(nextFront);
		}
		
		return paretoFronts;
		
	}
	/**
	 * 
	 * @param ind
	 * @param population
	 * @return Set of individuals that dominates the given individual
	 */
	public static Set<Individual> getIndidividualsDominating(Individual ind, Collection<Individual> population, Dominator dominator) {
		Set<Individual> dominatingIndividuals = new HashSet<>();
		
		for (Individual otherInd : population){
			if (ind == otherInd) continue; // Do not compare with self
			
			if (dominator.dominates(otherInd, ind)){ // otherInd dominates ind
				dominatingIndividuals.add(otherInd);
			}
		}
		return dominatingIndividuals;
	}
	/**
	 * 
	 * @param ind
	 * @param population
	 * @return Set of individuals that are dominated by the given individual
	 */
	public static Set<Individual> getIndividualsDominatedBy(Individual ind, Collection<Individual> population, Dominator dominator) {
		Set<Individual> dominatedIndividuals = new HashSet<>();
		
		for (Individual otherInd : population){
			if (ind == otherInd) continue; // Do not compare with self
			
			if (dominator.dominates(ind, otherInd)){ // ind dominates otherInd
				dominatedIndividuals.add(otherInd);
			}
		}
		return dominatedIndividuals;
	}

	//sorts so the elements with the lowest biased fitness are first in the list
	public static Comparator<Individual> getBiasedFitnessComparator() {
		return new Comparator<Individual>() {
			public int compare(Individual ind1, Individual ind2) {
				if (ind1.getBiasedFitness() < ind2.getBiasedFitness()) {
					return -1;
				}
				else if (ind1.getBiasedFitness() > ind2.getBiasedFitness()) {
					return 1;
				}
				else {
					return 0;
				}
			}
		};
	}

	public static Comparator<Individual> getPenalizedCostComparator() {
		return new Comparator<Individual>() {
			public int compare(Individual ind1, Individual ind2) {
				if (ind1.getPenalizedCost() < ind2.getPenalizedCost()) {
					return -1;
				}
				else if (ind1.getPenalizedCost() > ind2.getPenalizedCost()) {
					return 1;
				}
				else {
					return 0;
				}
			}
		};
	}
	
	public static Comparator<Individual> getRobustnessComparator() {
		return new Comparator<Individual>() {
			public int compare(Individual ind1, Individual ind2) {
				if (ind1.getRobustness() > ind2.getRobustness()) {
					return -1;
				}
				else if (ind1.getRobustness() < ind2.getRobustness()) {
					return 1;
				}
				else {
					return 0;
				}
			}
		};
	}

	public static Comparator<Individual> getPersistenceComparator() {
		return new Comparator<Individual>() {
			public int compare(Individual ind1, Individual ind2) {
				if (ind1.getNumberOfChangesFromBaseline() < ind2.getNumberOfChangesFromBaseline()) {
					return -1;
				}
				else if (ind1.getNumberOfChangesFromBaseline() > ind2.getNumberOfChangesFromBaseline()) {
					return 1;
				}
				return 0;
			}
		};
	}

	public static Comparator<Individual> getDiversityContributionComparator() {
		return new Comparator<Individual>() {
			public int compare(Individual ind1, Individual ind2) {
				if (ind1.getDiversityContribution() < ind2.getDiversityContribution()) {
					return 1;
				}
				else if (ind1.getDiversityContribution() > ind2.getDiversityContribution()) {
					return -1;
				}
				return 0;
			}
		};
	}

	public static Set<Individual> removeObjectiveClones(Set<Individual> subpopulation, Dominator dominator) {
		Set<Individual> individualsToRemove = new HashSet<>();
		for (Individual ind : subpopulation){
			for (Individual otherInd : subpopulation){
				if (ind == otherInd) continue; // Do not compare with self
				if (individualsToRemove.contains(ind)) continue; // Individual has already been removed
				
				if (dominator.hasEqualObjectiveValues(ind, otherInd)){
					individualsToRemove.add(otherInd);
				}
			}
		}
		subpopulation.removeAll(individualsToRemove);
		
		return subpopulation;
		
	}
	
	public static Set<Individual> getObjectiveClones(Individual ind, Set<Individual> subpopulation, Dominator dominator){
		Set<Individual> clones = new HashSet<>();
		
		for (Individual otherInd : subpopulation){
			if (ind == otherInd) continue; // Do not compare with self
			
			if (dominator.hasEqualObjectiveValues(ind, otherInd)){
				clones.add(otherInd);
			}
		}
		return clones;
	}
	
}
