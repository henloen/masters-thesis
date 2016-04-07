package hgsadc.implementations;

import hgsadc.Utilities;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;
import hgsadc.protocols.Genotype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

	public GenotypeHGS(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome, int nInstallations, int nVessels) {
		
		List<HashMap<Integer, Set<Integer>>> chromosomes = generateDeparturePatternChromosomesFromGiantTour(giantTourChromosome, nInstallations, nVessels);
		HashMap<Integer, Set<Integer>>	installationDeparturePatternChromosome = chromosomes.get(0);
		HashMap<Integer, Set<Integer>> vesselDeparturePatternChromosome = chromosomes.get(1);
		
		this.installationDeparturePatternChromosome = installationDeparturePatternChromosome;
		this.vesselDeparturePatternChromosome = vesselDeparturePatternChromosome;
		this.giantTourChromosome = giantTourChromosome;
	}
	
	
	
	/** 
	 * @param giantTourChromosome
	 * @param nInstallations
	 * @param nVessels
	 * @return A List containing two elements, 1: installationDepartureChromosome and 2: vessselDepartureChromosome
	 */
	public static List<HashMap<Integer, Set<Integer>>> generateDeparturePatternChromosomesFromGiantTour(
			HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome, int nInstallations, int nVessels) {
		
		HashMap<Integer, Set<Integer>> installationDeparturePatternChromosome = generateEmptyChromosome(nInstallations); // Installation --> {Days}
		HashMap<Integer, Set<Integer>> vesselDeparturePatternChromosome = generateEmptyChromosome(nVessels); // Vessel --> {Days}
		
		for (int day = 0; day < giantTourChromosome.size(); day++) {
			HashMap<Integer, ArrayList<Integer>> dayChromosome = giantTourChromosome.get(day);
			for (int vessel = 1; vessel <= dayChromosome.size(); vessel++){
				ArrayList<Integer> dayVesselDepartures = dayChromosome.get(vessel);
				for (Integer installation : dayVesselDepartures) {
					installationDeparturePatternChromosome.get(installation).add(day);
					vesselDeparturePatternChromosome.get(vessel).add(day);
				}
			}
		}
		ArrayList<HashMap<Integer, Set<Integer>>> chromosomes = new ArrayList<HashMap<Integer,Set<Integer>>>(); 
		chromosomes.add(installationDeparturePatternChromosome);
		chromosomes.add(vesselDeparturePatternChromosome);
		
		return chromosomes;
	}

	public static HashMap<Integer, Set<Integer>> generateEmptyChromosome(int nKeys) {
		HashMap<Integer, Set<Integer>> chromosome = new HashMap<>();
		for (int i = 1; i <= nKeys; i++){
			chromosome.put(i, new HashSet<>());
		}
		return chromosome;
	}
	
	public static HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> generateEmptyGiantTourChromosome(int nDays, int nVessels){
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome = new HashMap<>();
		
		for (int day = 0; day < nDays; day++) {
			HashMap<Integer, ArrayList<Integer>> vesselAllocations = new HashMap<>();
			for (int vessel = 1; vessel <= nVessels; vessel++){
				vesselAllocations.put(vessel, new ArrayList<Integer>());
			}
			giantTourChromosome.put(day, vesselAllocations);
		}
		
		return giantTourChromosome;
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
		str += getGiantTourChromosomeString(giantTourChromosome);
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
	
	public static String getGiantTourChromosomeString(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome) {
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

	@Override
	public Set<Integer> getDaysWithVesselDeparture() {
		Set<Integer> daysWithVesselDeparture = new HashSet<Integer>();
		HashMap<Integer, Set<Integer>> vesselDeparturesPerDay = getVesselDeparturesPerDay();
		
		for (Integer day : vesselDeparturesPerDay.keySet()) {
			if (!vesselDeparturesPerDay.get(day).isEmpty()){
				daysWithVesselDeparture.add(day);
			}
		}
		return daysWithVesselDeparture;
	}

	@Override
	public HashMap<Integer, Set<Integer>> getVesselDeparturesPerDay() {
		return Utilities.getReversedHashMap(vesselDeparturePatternChromosome);
	}
	
	@Override
	public HashMap<Integer, Set<Integer>> getInstallationDeparturesPerDay() {
		return Utilities.getReversedHashMap(installationDeparturePatternChromosome);
	}
	
	@Override
	public Set<Integer> getDaysWithInstallationDeparture() {
		Set<Integer> daysWithInstallationDeparture = new HashSet<Integer>();
		HashMap<Integer, Set<Integer>> vesselDeparturesPerDay = getVesselDeparturesPerDay();
		
		for (Integer day : vesselDeparturesPerDay.keySet()) {
			if (!vesselDeparturesPerDay.get(day).isEmpty()){
				daysWithInstallationDeparture.add(day);
			}
		}
		return daysWithInstallationDeparture;
	}
	
	public static String getGiantTourString(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour){
		String str = "";
		for (Integer day : giantTour.keySet()) {
			for (Integer vessel : giantTour.get(day).keySet()) {
				str += "(" + day + "," + vessel + ")={";
				for (Integer installation : giantTour.get(day).get(vessel)){
					str += installation + " ";
				}
				str += "}, ";
			}
		}
		return str;
		
	}
	
}
