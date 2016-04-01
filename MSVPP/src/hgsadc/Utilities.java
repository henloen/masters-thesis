package hgsadc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
	
}
