package ea.svpp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import voyageGenerationDP.Installation;
import voyageGenerationDP.Vessel;
import voyageGenerationDP.Voyage;

public abstract class UtilitiesSVPP {
	
	public static Set<Installation> getSetOfVisitedInstallations(Voyage voyage){
		ArrayList<Installation> visitedInstallations = voyage.getVisitedInstallations();		
		return new HashSet<>(visitedInstallations);
	}
	
	public static int getNumberOfDeparturesOnDay(int day, HashMap<Vessel, Voyage[]> schedule){
		int nDepartures = 0;
		for (Vessel vessel : schedule.keySet()) {
			Voyage[] vesselSchedule = schedule.get(vessel);
			if (vesselSchedule[day] != null) nDepartures++;
		}
		return nDepartures;
	}

	public static boolean moreVisitsRequired(HashMap<Installation, Integer> remainingVisits){
		for (Installation installation : remainingVisits.keySet()) {
			//System.out.println(installation.getName() + " requires " + remainingVisits.get(installation) + " more visits");
			if (remainingVisits.get(installation) != 0){
				return true;
			}
		}
		return false;
	}
	
	public static <T> T pickRandomElementFromList(List<T> list){
		int randomIndex = new Random().nextInt(list.size());
		return list.get(randomIndex);
	}
	
	public static <T> T pickRandomElementFromList(List<T> list, Set<T> alreadyPicked){
		T object;
		do {
			int randomIndex = new Random().nextInt(list.size());
			object = list.get(randomIndex);
		}
		while (alreadyPicked.contains(object));
		
		return object;
	}

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

	public static boolean voyageVisitsInstallation(Voyage voyage, Installation installation){
		return voyage.getVisitedInstallations().contains(installation);
	}
}
