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
		// TODO Auto-generated method stub
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

	
}
