package ea.svpp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import ea.Individual;
import voyageGenerationDP.Installation;
import voyageGenerationDP.Vessel;
import voyageGenerationDP.Voyage;

public class ProblemDataSVPP implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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

	public static String inputFileName = "data/input/Input data.xls",
			outputFileName = "data/output/"; //sets the folder, see the constructor of IO for the filename format
	//heuristic parameters
	public static int removeLongestArcs = 0, minInstallationsHeur = 0, maxArcsRemovedInstallation = 2;
	public static double capacityFraction = 0;
	
	
	public ProblemDataSVPP(ArrayList<Installation> installations, ArrayList<Vessel> vessels, double[][] distances,
			ArrayList<ArrayList<Vessel>> vesselSets,
			HashMap<Integer, ArrayList<Installation>> installationSetsByFrequency, ArrayList<Voyage> voyageSet,
			HashMap<Vessel, ArrayList<Voyage>> voyageSetByVessel,
			HashMap<Vessel, HashMap<Installation, ArrayList<Voyage>>> voyageSetByVesselAndInstallation,
			HashMap<Vessel, HashMap<Integer, ArrayList<Voyage>>> voyageSetByVesselAndDuration,
			HashMap<Vessel, HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>> voyageSetByVesselAndDurationAndSlack, ArrayList<Integer> depotCapacity) {
		
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
	}
	
	public boolean isFeasibleSchedule(Individual schedule){
		
		/*
		 * 1. All installations are serviced the required number of times
		 * 2. PSV does not sail more than the required number of days
		 * 3. Number of PSVs at depot does not exceed depot capacity
		 * 4. PSV does not start new voyage before it has returned from another
		 * 5. Evenly spread departures to each installation
		 */
		if (!constraintInstallationVisitsSatisfied(schedule)){
			return false;
		} else if (!constraintDaysPerPSVSatisfied(schedule)){
			return false;
		} else if (!constraintDepotCapacitySatisfied(schedule)){
			return false;
		} else if (!constraintOverlappingVoyagesSatisfied(schedule)) {
			return false;
		} else if (!constraintEvenlySpreadDeparturesSatisfied(schedule)){
			return false;
		}
		return true;
		
	}
	
	public boolean constraintInstallationVisitsSatisfied(Individual schedule){
		
		PhenotypeSVPP phenotype = (PhenotypeSVPP) schedule.getPhenotype();
		int[] visitsPerInstallation = new int[installations.size()];
		
		// Find number of visits to each installation
		for (int[] departure : phenotype.getVoyagesSailed()){ // Loop through all departures
			int voyageNumber = departure[2];
			Voyage voyage = voyageSet.get(voyageNumber); // Are the voyages in the voyageSet ordered? Yes! That's convenient
			
			for (int installation : voyage.getVisited()) {
				visitsPerInstallation[installation]++; // Add 1 to counter for every installation 
			}
		}
		
		for (Installation installation : installations){
			int instNumber = installation.getNumber();
			if (installation.getFrequency() != visitsPerInstallation[instNumber]){
				return false;
			}
		}
		return true; // All installations have required number of visits
	}

	private boolean constraintOverlappingVoyagesSatisfied(Individual schedule) {
		GenotypeSVPP genotype = (GenotypeSVPP) schedule.getGenotype();
		
		int[][] scheduleArray = genotype.getSchedule();
		
		for (int PSV = 0; PSV < GenotypeSVPP.NUMBER_OF_PSVS; PSV++) {
			int day = 0;
			while (day < GenotypeSVPP.NUMBER_OF_DAYS){
				
				int voyageStarted = scheduleArray[PSV][day];
				if (voyageStarted != 0){ // The PSV departs on a new voyage on this day
					int voyageDuration = voyageSet.get(voyageStarted).getDuration();
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

	private boolean constraintDepotCapacitySatisfied(Individual schedule) {
		GenotypeSVPP genotype = (GenotypeSVPP) schedule.getGenotype();
		
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

	private boolean constraintDaysPerPSVSatisfied(Individual schedule) {
		GenotypeSVPP genotype = (GenotypeSVPP) schedule.getGenotype();
		
		int[][] scheduleArray = genotype.getSchedule();
		
		for (int PSV = 0; PSV < GenotypeSVPP.NUMBER_OF_PSVS; PSV++){
			int nDaysSailing = 0; // Number of days the PSV is out sailing
			int day = 0;
			while (day < GenotypeSVPP.NUMBER_OF_DAYS){
				int voyageStarted = scheduleArray[PSV][day];
				if (voyageStarted != 0){
					int voyageDuration = voyageSet.get(voyageStarted).getDuration();
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

	
	private boolean constraintEvenlySpreadDeparturesSatisfied(Individual schedule) {
		/*
		 * Parameters used for spreading departures
		 * hf ::	  [6, 2, 2, 3, 1, 0, 0] ! Horizon in which we need to constrain number of departures, given f required visits
		 * Pf_min ::  [0, 0, 1, 2, 1, 0, 0] ! Minimum no of departures to an installation in the horizon hf
		 * Pf_max ::  [1, 1, 3, 4, 2, 1, 1] ! Maximum no of departures to an installation in the horizon hf
		 */
		int[] hf = {6, 2, 2, 3, 1, 0, 0};
		int[] Pf_min = {0, 0, 1, 2, 1, 0, 0};
		int[] Pf_max = {1, 1, 3, 4, 2, 1, 1};
		
		GenotypeSVPP genotype = (GenotypeSVPP) schedule.getGenotype();
		int[][] scheduleArray = genotype.getSchedule();
		
		for (Installation installation : installations){
			for (int day = 0; day < GenotypeSVPP.NUMBER_OF_DAYS; day++){
				int numberOfDeparturesToInstallationInHorizon = 0;
				
				int requiredVisits = installation.getFrequency();
				for (int i = 0; i < hf[requiredVisits]; i++){
					int dayToCheck = (day + i) % GenotypeSVPP.NUMBER_OF_DAYS;
					
					for (int PSV = 0; PSV < GenotypeSVPP.NUMBER_OF_PSVS; PSV++){
							
						int voyageStarted = scheduleArray[PSV][dayToCheck];
						if (voyageStarted != 0){
							if (voyageVisitsInstallation(installation, vessels.get(PSV), voyageSet.get(voyageStarted))){
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
		ArrayList<Voyage> voyages = voyageSetByVesselAndInstallation.get(installation).get(vessel);		
		return voyages.contains(voyage);
	}
	
	
}
