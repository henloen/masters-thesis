package hgsadc.implementations;

import hgsadc.protocols.Genotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GenotypeHGS implements Genotype {

	private HashMap<Integer, Set<Integer>> installationDeparturePatternChromosome; //the key is an installation number and the value is a set of periods/days
	private HashMap<Integer, Set<Integer>> vesselDeparturePatternChromosome; //the key is a vessel number and the value is a set of periods/days
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome; //the first key is a period/day, the second key is a vessel number and the value is a set of installation numbers 
	
	public GenotypeHGS(
			HashMap<Integer, Set<Integer>> installationDeparturePatternChromosome,
			HashMap<Integer, Set<Integer>> vesselDeparturePatternChromosome,
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome) {
		this.installationDeparturePatternChromosome = installationDeparturePatternChromosome;
		this.vesselDeparturePatternChromosome = vesselDeparturePatternChromosome;
		this.giantTourChromosome = giantTourChromosome;
	}

	public HashMap<Integer, Set<Integer>> getInstallationDeparturePatternChromosome() {
		return installationDeparturePatternChromosome;
	}

	public HashMap<Integer, Set<Integer>> getVesselDeparturePatternChromosome() {
		return vesselDeparturePatternChromosome;
	}

	public HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> getGiantTourChromosome() {
		return giantTourChromosome;
	}
	
	public HashMap<Integer, Set<Integer>> getVesselsByInstallation() {
		HashMap<Integer, Set<Integer>> vesselsByInstallation = new HashMap<Integer, Set<Integer>>();
		for (Integer installation : installationDeparturePatternChromosome.keySet()) {
			vesselsByInstallation.put(installation, new HashSet<Integer>());
		}
		for (Integer day : giantTourChromosome.keySet()) {
			for (Integer vessel : giantTourChromosome.get(day).keySet()) {
				for (Integer installation : giantTourChromosome.get(day).get(vessel)) {
					Set<Integer> vessels = vesselsByInstallation.get(installation);
					vessels.add(vessel);
					vesselsByInstallation.put(installation, vessels);
				}
			}
		}
		return vesselsByInstallation;
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
