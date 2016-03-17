package hgsadc.implementations;

import hgsadc.protocols.Genotype;

import java.util.HashMap;
import java.util.Set;

public class GenotypeHGS implements Genotype {

	private HashMap<Integer, Set<Integer>> installationDeparturePatternChromosome; //the key is an installation number and the value is a set of periods/days
	private HashMap<Integer, Set<Integer>> vesselDeparturePatternChromosome; //the key is a vessel number and the value is a set of periods/days
	private HashMap<Integer, HashMap<Integer, Set<Integer>>> giantTourChromosome; //the first key is a period/day, the second key is a vessel number and the value is a set of installation numbers 
	
	public GenotypeHGS(
			HashMap<Integer, Set<Integer>> installationDeparturePatternChromosome,
			HashMap<Integer, Set<Integer>> vesselDeparturePatternChromosome,
			HashMap<Integer, HashMap<Integer, Set<Integer>>> giantTourChromosome) {
		this.installationDeparturePatternChromosome = installationDeparturePatternChromosome;
		this.vesselDeparturePatternChromosome = vesselDeparturePatternChromosome;
		this.giantTourChromosome = giantTourChromosome;
	}

	public HashMap<Integer, Set<Integer>> getInstallationDeparturePattern() {
		return installationDeparturePatternChromosome;
	}

	public HashMap<Integer, Set<Integer>> getVesselDeparturePattern() {
		return vesselDeparturePatternChromosome;
	}

	public HashMap<Integer, HashMap<Integer, Set<Integer>>> getGiantTour() {
		return giantTourChromosome;
	}
	
	public String toString() {
		String str = getInstallationDeparturePatternChromosomeString();
		str += getVesselDeparturePatternChromosomeString();
		str += getGiantTourChromosomeString();
		return str;
	}
	
	private String getInstallationDeparturePatternChromosomeString() {
		String str = "Installation patterns: " + "\n";
		for (Integer installation : installationDeparturePatternChromosome.keySet()) {
			str += "Installation " + installation + ": " + installationDeparturePatternChromosome.get(installation) + "\n"; 
		}
		return str;
	}
	
	private String getVesselDeparturePatternChromosomeString() {
		String str = "Vessel patterns: " + "\n";
		for (Integer vessel : vesselDeparturePatternChromosome.keySet()) {
			str += "Vessel " + vessel + ": " + vesselDeparturePatternChromosome.get(vessel) + "\n"; 
		}
		return str;
	}
	
	private String getGiantTourChromosomeString() {
		String str = "Giant tour:" + "\n";
		for (Integer day : giantTourChromosome.keySet()) {
			str += "Day " + day + ": ";
			for (Integer vessel : giantTourChromosome.get(day).keySet()) {
				str += "Vessel " + vessel + ": " + giantTourChromosome.get(day).get(vessel) + " ";
			}
			str += "\n";
		}
		return str;
	}
}
