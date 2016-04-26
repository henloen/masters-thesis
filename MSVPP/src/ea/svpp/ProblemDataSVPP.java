package ea.svpp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ea.Individual;
import voyageGenerationDPold.Installation;
import voyageGenerationDPold.Vessel;
import voyageGenerationDPold.Voyage;

public class ProblemDataSVPP implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public int lengthOfPlanningPeriod;
	public ArrayList<Installation> installations;
	public ArrayList<Vessel> vessels;
	public double[][] distances;
	public ArrayList<ArrayList<Vessel>> vesselSets;
	public HashMap<Integer, ArrayList<Installation>> installationSetsByFrequency;
	public ArrayList<Voyage> voyageSet;
	public HashMap<Vessel, ArrayList<Voyage>> voyageSetByVessel;
	public HashMap<Vessel, HashMap<Installation, ArrayList<Voyage>>> voyageSetByVesselAndInstallation;
	public HashMap<Vessel, HashMap<Integer, ArrayList<Voyage>>> voyageSetByVesselAndDuration;
	public HashMap<Vessel, HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>> voyageSetByVesselAndDurationAndSlack;
	public ArrayList<Integer> depotCapacity;
	public HashMap<Vessel, HashMap<Set<Installation>, Voyage>> voyageByVesselAndInstallationSet; 

	public static String inputFileName = "data/input/Input data.xls",
			outputFileName = "data/output/"; //sets the folder, see the constructor of IO for the filename format
	//heuristic parameters
	public static int removeLongestArcs = 0, minInstallationsHeur = 0, maxArcsRemovedInstallation = 2;
	public static double capacityFraction = 0;
	
	
	public ProblemDataSVPP(int lengthOfPlanningPeriod, ArrayList<Installation> installations, ArrayList<Vessel> vessels, double[][] distances,
			ArrayList<ArrayList<Vessel>> vesselSets,
			HashMap<Integer, ArrayList<Installation>> installationSetsByFrequency, ArrayList<Voyage> voyageSet,
			HashMap<Vessel, ArrayList<Voyage>> voyageSetByVessel,
			HashMap<Vessel, HashMap<Installation, ArrayList<Voyage>>> voyageSetByVesselAndInstallation,
			HashMap<Vessel, HashMap<Integer, ArrayList<Voyage>>> voyageSetByVesselAndDuration,
			HashMap<Vessel, HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>> voyageSetByVesselAndDurationAndSlack,
			ArrayList<Integer> depotCapacity,
			HashMap<Vessel, HashMap<Set<Installation>, Voyage>> voyageByVesselAndInstallationSet) {
		
		this.lengthOfPlanningPeriod = lengthOfPlanningPeriod;
		this.installations = installations;
		this.vessels = vessels;
		this.distances = distances;
		this.vesselSets = vesselSets;
		this.installationSetsByFrequency = installationSetsByFrequency;
		this.voyageSet = voyageSet;
		this.voyageSetByVessel = voyageSetByVessel;
		this.voyageSetByVesselAndInstallation = voyageSetByVesselAndInstallation;
		this.voyageSetByVesselAndDuration = voyageSetByVesselAndDuration;
		this.voyageSetByVesselAndDurationAndSlack = voyageSetByVesselAndDurationAndSlack;
		this.depotCapacity = depotCapacity;
		this.voyageByVesselAndInstallationSet = voyageByVesselAndInstallationSet;
	}
	
	public boolean isFeasibleSchedule(Individual individual){
		
		/*
		 * 1. All installations are serviced the required number of times
		 * 2. PSV does not sail more than the required number of days
		 * 3. Number of PSVs at depot does not exceed depot capacity
		 * 4. PSV does not start new voyage before it has returned from another
		 * 5. Evenly spread departures to each installation
		 */
		System.out.println("Checking feasibility...");
		if (!constraintInstallationVisitsSatisfied(individual)){
			System.out.println("Installation visits broken");
			return false;
		} else if (!constraintDaysPerPSVSatisfied(individual)){
			System.out.println("Days per PSV broken");
			return false;
		} else if (!constraintDepotCapacitySatisfied(individual)){
			System.out.println("Depot capacity broken");
			return false;
		} else if (!constraintOverlappingVoyagesSatisfied(individual)) {
			System.out.println("Overlapping voyages broken");
			return false;
		} else if (!constraintEvenlySpreadDeparturesSatisfied(individual)){
			System.out.println("Evenly spread departures broken");
			return false;
		}
		return true;
		
	}
	
	public boolean constraintInstallationVisitsSatisfied(Individual individual){
		
		System.out.println("checking installation Visits");
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		HashMap<Installation, Integer> remainingVisits = getRequiredVisits();
		
		// Find number of visits to each installation
		for (Vessel vessel : genotype.getCharteredVessels()){
			for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
				Voyage voyage = genotype.getDeparture(vessel, day);
				
				if (voyage != null){
					for (Installation installation : voyage.getVisitedInstallations()) {
						// Subtract 1 from remaining visits to this installation
						int remVisitsToInstallation = remainingVisits.get(installation);
//						System.out.println(installation.getName() + " requires " + remVisitsToInstallation);
						remainingVisits.put(installation, remVisitsToInstallation-1);
					}
				}
			}
		}
		// Check that all installations have required number of visits
		if (UtilitiesSVPP.moreVisitsRequired(remainingVisits)) return false;
		else return true;
	}

	private boolean constraintOverlappingVoyagesSatisfied(Individual individual) {
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		for (Vessel vessel : genotype.getCharteredVessels()){
			int day = 0;
			while (day < GenotypeSVPP.NUMBER_OF_DAYS){
				
				Voyage voyage = genotype.getDeparture(vessel, day);
				
				if (voyage != null){ // The PSV departs on a new voyage on this day
					int voyageDuration = voyage.getDuration();
					for (int i = 1; i < voyageDuration; i++){
						/* Note: Duration includes the day voyage begins. E.g. a PSV starting a 2-day voyage on
						 * Monday can depart again on Wednesday
						 */
						int dayToCheck = (day+i) % GenotypeSVPP.NUMBER_OF_DAYS;
						
						if (genotype.getDeparture(vessel, dayToCheck) != null){
							return false;
						}
						day += voyageDuration;
					}
				} else { // PSV does not depart on a new voyage on this day
					day++;
				}
			}
		}
		return true;
	}

	private boolean constraintDepotCapacitySatisfied(Individual individual) {
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
			int nDepartures = genotype.getNumberOfDeparturesOnDay(day);
			
			if (nDepartures > depotCapacity.get(day)){
				return false; // DepotCapacity is exceeded
			}
		}
		return true;
	}

	private boolean constraintDaysPerPSVSatisfied(Individual individual) {
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		for (Vessel vessel : genotype.getCharteredVessels()){
			int nDaysSailing = genotype.getNumberOfDaysSailing(vessel);
			if (nDaysSailing > vessel.getNumberOfDaysAvailable()){
				return false;
			}
		}
		return true;
	}
	
	private boolean constraintEvenlySpreadDeparturesSatisfied(Individual individual) {
		/*
		 * Parameters used for spreading departures
		 * hf ::	  [6, 2, 2, 3, 1, 0, 0] ! Horizon in which we need to constrain number of departures, given f required visits
		 * Pf_min ::  [0, 0, 1, 2, 1, 0, 0] ! Minimum no of departures to an installation in the horizon hf
		 * Pf_max ::  [1, 1, 3, 4, 2, 1, 1] ! Maximum no of departures to an installation in the horizon hf
		 */
		int[] hf = {6, 2, 2, 3, 1, 0, 0};
		int[] Pf_min = {0, 0, 1, 2, 1, 0, 0};
		int[] Pf_max = {1, 1, 3, 4, 2, 1, 1};
		
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		for (int inst = 1; inst < installations.size(); inst++){
			Installation installation = installations.get(inst); // Loop through all installations
			for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){ // Loop through all days
				int nDeparturesToInstallationInHorizon = 0;
				
				int requiredVisits = installation.getFrequency();
				for (int i = 0; i < hf[requiredVisits]; i++){
					int dayToCheck = (day + i) % GenotypeSVPP.NUMBER_OF_DAYS;
					int nDeparturesOnDay = genotype.getNumberOfDeparturesToInstallation(installation, dayToCheck);
					
					if (nDeparturesOnDay > 1) return false; // Maximum one departure per day
					nDeparturesToInstallationInHorizon += nDeparturesOnDay;
				}
				if (nDeparturesToInstallationInHorizon < Pf_min[requiredVisits] || nDeparturesToInstallationInHorizon > Pf_max[requiredVisits]){
					return false;
				}
			}
		}
		return true;
	}
	
	public HashMap<Installation, Integer> getRequiredVisits(){
		HashMap<Installation, Integer> requiredVisits = new HashMap<>();
		
		for (int i = 1; i < installations.size(); i++){ // Depot has no required visits, therefore start on 1
			Installation installation = installations.get(i);
			requiredVisits.put(installation, installation.getFrequency());
		}
		return requiredVisits;
	}
	
	
}
