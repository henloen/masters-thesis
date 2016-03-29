package hgsadc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class PatternGenerator {
	
	private ArrayList<Installation> customerInstallations;
	private HashMap<Integer, Set<String>> generatedBitstrings;
	private int lengthOfPlanningPeriod, minDuration;
	
	
	public PatternGenerator(ArrayList<Installation> customerInstallations) {
		this.customerInstallations = customerInstallations;
		generatedBitstrings = new HashMap<Integer, Set<String>>();
	}

	public HashMap<Integer, Set<Set<Integer>>> generateInstallationDeparturePatterns(int lengthOfPlanningPeriod) {
		this.lengthOfPlanningPeriod = lengthOfPlanningPeriod;
		HashMap<Integer, Set<Set<Integer>>> patterns = new HashMap<Integer, Set<Set<Integer>>>();
		Set<Integer> frequencies = getInstallationFrequencies();
		for (int frequency : frequencies) {
			patterns.put(frequency, generatePattern(frequency, true));
		}
		return patterns;
	}

	public HashMap<Integer, Set<Set<Integer>>> generateVesselDeparturePatterns(int minDuration, int maxDuration, int lengthOfPlanningPeriod) {
		this.lengthOfPlanningPeriod = lengthOfPlanningPeriod;
		this.minDuration = minDuration;
		HashMap<Integer, Set<Set<Integer>>> patterns = new HashMap<Integer, Set<Set<Integer>>>();
		Set<Integer> possibleNumberOfDepartures = getNumberOfDepartures(minDuration, lengthOfPlanningPeriod);
		for (int numberOfDepartures: possibleNumberOfDepartures) {
			patterns.put(numberOfDepartures, generatePattern(numberOfDepartures, false));
		}
		return patterns;
	}
	
	private Set<Integer> getNumberOfDepartures(int minDuration, int lengthOfPlanningPeriod) {
		Set<Integer> numberOfDepartures = new HashSet<Integer>();
		int maxNumberOfDepartures = lengthOfPlanningPeriod / minDuration; //e.g. a 7-day period with 2-day voyages as the shortest voyage can maximum have floor(7/2) = 3 departures
		for (int i=0;i<=maxNumberOfDepartures;i++){
			numberOfDepartures.add(i);
		}
		return numberOfDepartures;
	}
	
	private Set<Integer> getInstallationFrequencies() {
		Set<Integer> frequencies = new HashSet<Integer>();
		for (Installation installation : customerInstallations) {
			frequencies.add(installation.getFrequency());
		}
		return frequencies;
	}
	
	private Set<Set<Integer>> generatePattern(int ones, boolean installationPattern) {//true->installationPattern, false->vesselPattern
		Set<Set<Integer>> patterns = new HashSet<Set<Integer>>();
		Set<String> bitPatterns = getAllBitStrings(ones);
		for (String str : bitPatterns) {
			patterns.add(bitStringToSet(str));
		}
		return validPatterns(patterns, installationPattern);
		
	}
	private Set<Set<Integer>> validPatterns(Set<Set<Integer>> allPatterns, boolean installationPatterns) {
		Set<Set<Integer>> validPatterns = new HashSet<Set<Integer>>();
		for (Set<Integer> pattern : allPatterns) {
			if (installationPatterns) {
				if (validInstallationPattern(pattern)) {
					validPatterns.add(pattern);
				}
			}
			else{
				if (validVesselPattern(pattern)) {
					validPatterns.add(pattern);
				}
			}
		}
		return validPatterns;
	}
	
	private boolean validInstallationPattern(Set<Integer> pattern) {
		if (pattern.contains(6)) { //no departures are allowed on sundays
			return false;
		}
		int numberOfDepartures = pattern.size();
		if (numberOfDepartures == 1) {
			return true;
		}
		int maxDistance = (lengthOfPlanningPeriod / numberOfDepartures) + 1; //int divided by int returns the floored int of the division, e.g. 7/5 = 1 
		ArrayList<Integer> list = new ArrayList<Integer>(pattern);
		Collections.sort(list);//converted to a list, as the order is important when calculating the distance to the next departure
		for (int i=0; i<list.size()-1;i++) {
			int distanceToNext = list.get(i+1) - list.get(i);
			if (! isValidDistance(distanceToNext, maxDistance)) {
				return false;
			}
		}
		int distanceToNext = (lengthOfPlanningPeriod - list.get(list.size()-1)) + list.get(0);//distance to the end of the planning period + distance from the start of the planning periode to the first element 
		if (! isValidDistance(distanceToNext, maxDistance)) {
			return false;
		}
		return true;
	}
	
	private boolean validVesselPattern(Set<Integer> pattern){
		if (pattern.contains(6)) {
			return false;
		}
		if (pattern.size() < 2) {
			return true;
		}
		ArrayList<Integer> list = new ArrayList<Integer>(pattern);
		Collections.sort(list);//converted to a list, as the order is important when calculating the distance to the next departure
		for (int i=0; i<list.size()-1;i++) {
			int duration = list.get(i+1) - list.get(i);
			if (duration < minDuration) {
				return false;
			}
		}
		int duration = (lengthOfPlanningPeriod - list.get(list.size()-1)) + list.get(0);//distance to the end of the planning period + distance from the start of the planning periode to the first element
		if (duration < minDuration) {
			return false;
		}
		return true;
	}
	
	private boolean isValidDistance(int distance, int maxDistance) {
		return ! (distance>maxDistance || distance<(maxDistance-1));
	}
	
	private Set<Integer> bitStringToSet(String bitString){
		Set<Integer> set = new HashSet<Integer>();
		for (int i=0; i<bitString.length();i++) {
			if (bitString.charAt(i)=='1') {
				set.add(i);
			}
		}
		return set;
	}
	
	private Set<String> getAllBitStrings(int ones){
		if (generatedBitstrings.containsKey(ones)) {
			return generatedBitstrings.get(ones);
		}
		Set<String> setOfBitStrings = new HashSet<String>();
		getBitString("", ones, lengthOfPlanningPeriod, setOfBitStrings);
		generatedBitstrings.put(ones, setOfBitStrings);
		return setOfBitStrings;
	}
	
	private void getBitString(String substring, int onesLeft, int bitsLeft, Set<String> set) {
		if (bitsLeft == 0 && onesLeft == 0 ) {
			set.add(substring);
		}
		else if(onesLeft > bitsLeft || bitsLeft == 0) {
			return;
		}
		else {
			getBitString(substring + "0", onesLeft, bitsLeft-1, set);
			getBitString(substring + "1", onesLeft -1, bitsLeft-1, set);
		}
	}
	
}
