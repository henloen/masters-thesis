package hgsadc.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface Genotype {
	
	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getGiantTourChromosome();

	public Set<Integer> getDaysWithVesselDeparture();
	
}
