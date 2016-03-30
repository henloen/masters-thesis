package hgsadc;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

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

}
