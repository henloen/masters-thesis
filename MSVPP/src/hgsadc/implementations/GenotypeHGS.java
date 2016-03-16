package hgsadc.implementations;

import hgsadc.protocols.Genotype;

import java.util.HashMap;
import java.util.Set;

public class GenotypeHGS implements Genotype {

	private HashMap<Integer, Set<Integer>> installationDeparturePattern; //the key is an installation number and the value is a set of periods/days
	private HashMap<Integer, Set<Integer>> vesselDeparturePattern; //the key is a vessel number and the value is a set of periods/days
	private HashMap<Integer, HashMap<Integer, Set<Integer>>> giantTour; //the first key is a vessel number, the second key is a period/day and the value is a set of installation numbers 
	
	public GenotypeHGS(
			HashMap<Integer, Set<Integer>> installationDeparturePattern,
			HashMap<Integer, Set<Integer>> vesselDeparturePattern,
			HashMap<Integer, HashMap<Integer, Set<Integer>>> giantTour) {
		this.installationDeparturePattern = installationDeparturePattern;
		this.vesselDeparturePattern = vesselDeparturePattern;
		this.giantTour = giantTour;
	}
	
	

}
