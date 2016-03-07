package ea.svpp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import ea.Individual;
import voyageGenerationDP.Installation;
import voyageGenerationDP.Vessel;
import voyageGenerationDP.Voyage;

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
	public HashMap<Vessel, HashMap<Set<Integer>, Voyage>> voyageByVesselAndInstallationSet; 

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
			HashMap<Vessel, HashMap<Set<Integer>, Voyage>> voyageByVesselAndInstallationSet) {
		
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
		
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		int[] visitsPerInstallation = new int[installations.size()];
	
		// Find number of visits to each installation
		for (int PSV : genotype.getcharteredPSVs()){
			for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
				int voyageNumber = genotype.getSchedule()[PSV][day];
				
				if (voyageNumber > 0){
					Voyage voyage = voyageSet.get(voyageNumber-1); // Are the voyages in the voyageSet ordered? Yes! That's convenient
																   // Note that voyageNumbers are 1-indexed
					for (int installation : voyage.getVisited()) {
						if (installation >= visitsPerInstallation.length){
							// The visited list in each voyage contains the installation N+1 to indicate 2nd visit at depot, hence this check
							continue;
						}
						visitsPerInstallation[installation]++; // Add 1 to counter for every installation 
					}
				}
			}
		}
		// Check that all installations have required number of visits
		for (Installation installation : installations){
			int instNumber = installation.getNumber();
			if (visitsPerInstallation[instNumber] < installation.getFrequency()){
				return false;
			}
		}
		return true; // All installations have the required number of visits
	}

	private boolean constraintOverlappingVoyagesSatisfied(Individual individual) {
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		int[][] scheduleArray = genotype.getSchedule();
		
		for (int PSV = 0; PSV < GenotypeSVPP.NUMBER_OF_PSVS; PSV++) {
			int day = 0;
			while (day < GenotypeSVPP.NUMBER_OF_DAYS){
				
				int voyageStarted = scheduleArray[PSV][day];
				if (voyageStarted != 0){ // The PSV departs on a new voyage on this day
					int voyageDuration = voyageSet.get(voyageStarted-1).getDuration();
					for (int i = 1; i < voyageDuration; i++){
						/* Note: Duration includes the day voyage begins. E.g. a PSV starting a 2-day voyage on
						 * Monday can depart again on Wednesday
						 */
						if (scheduleArray[PSV][day+i] != 0){
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
		
		int[][] scheduleArray = genotype.getSchedule();
		
		for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
			int nDepartures = 0; // Departures on this day, i.e. how many PSVs that are serviced
			for (int PSV = 0; PSV < GenotypeSVPP.NUMBER_OF_PSVS; PSV++){
				nDepartures = scheduleArray[PSV][day] != 0 ? nDepartures + 1 : nDepartures;
			}
			if (nDepartures > depotCapacity.get(day)){
				return false; // DepotCapacity is exceeded
			}
		}
		return true;
	}

	private boolean constraintDaysPerPSVSatisfied(Individual individual) {
		GenotypeSVPP genotype = (GenotypeSVPP) individual.getGenotype();
		
		int[][] scheduleArray = genotype.getSchedule();
		
		for (int PSV = 0; PSV < GenotypeSVPP.NUMBER_OF_PSVS; PSV++){
			int nDaysSailing = 0; // Number of days the PSV is out sailing
			int day = 0;
			while (day < GenotypeSVPP.NUMBER_OF_DAYS){
				int voyageStarted = scheduleArray[PSV][day];
				if (voyageStarted != 0){
					int voyageDuration = voyageSet.get(voyageStarted-1).getDuration();
					nDaysSailing += voyageDuration;
					day += voyageDuration;
				}
				else {
					day++;
				}
			}
			if (nDaysSailing > vessels.get(PSV).getNumberOfDaysAvailable()){
				return false; // Number of days available is exceeded
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
		int[][] scheduleArray = genotype.getSchedule();
		
		for (int inst = 1; inst < installations.size(); inst++){
			Installation installation = installations.get(inst);
			for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
				int numberOfDeparturesToInstallationInHorizon = 0;
				
				int requiredVisits = installation.getFrequency();
				for (int i = 0; i < hf[requiredVisits]; i++){
					int dayToCheck = (day + i) % GenotypeSVPP.NUMBER_OF_DAYS;
					
					for (int PSV = 0; PSV < GenotypeSVPP.NUMBER_OF_PSVS; PSV++){
							
						int voyageStarted = scheduleArray[PSV][dayToCheck];
						if (voyageStarted != 0){
							if (voyageVisitsInstallation(installation, vessels.get(PSV), voyageSet.get(voyageStarted-1))){
								numberOfDeparturesToInstallationInHorizon++;
							}
						}
					}
				}
				if (numberOfDeparturesToInstallationInHorizon < Pf_min[requiredVisits] || numberOfDeparturesToInstallationInHorizon > Pf_max[requiredVisits]){
					return false;
				}
			}
		}
		return true;
	}
	
	
	
	public boolean voyageVisitsInstallation(Installation installation, Vessel vessel, Voyage voyage){
		ArrayList<Voyage> voyages = voyageSetByVesselAndInstallation.get(vessel).get(installation);		
		return voyages.contains(voyage);
	}
	
	
}
