package hgsadc.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface Genotype {
	
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getGiantTourChromosome();

	public Set<Integer> getDaysWithVesselDeparture();

	public HashMap<Integer, Set<Integer>> getVesselDeparturesPerDay();

	HashMap<Integer, Set<Integer>> getInstallationDeparturesPerDay();

	Set<Integer> getDaysWithInstallationDeparture();
	
}
