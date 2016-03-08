package ea.svpp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import voyageGenerationDP.Voyage;

public class UtilitiesSVPP {
	
	
	public static Set<Integer> getSetOfVisitedInstallations(Voyage voyage){
		ArrayList<Integer> visitedInstallations = voyage.getVisited();		
		HashSet<Integer> visitedSet = new HashSet<>();
		
		for (int i = 0; i < visitedInstallations.size()-1; i++){
			visitedSet.add(visitedInstallations.get(i));
		}
		return visitedSet;
	}
	

	public static Integer getRandomIntegerFromSet(Set<Integer> set){
		int indexToPick = new Random().nextInt(set.size());
		int i = 0;
		for (Integer element : set){
			if (i == indexToPick){
				return element;
			}
			i++;
		}
		return null;
	}
}
