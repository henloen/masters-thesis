package hgsadc.implementations;

import hgsadc.Installation;
import hgsadc.ProblemData;

import java.util.Collections;

public class FeasibleFleetChecker {
	
	public boolean isFeasibleFleet(ProblemData problemData) {
		
		int maxVisitsDemandedByInstallation = Collections.max(problemData.getInstallationDeparturePatterns().keySet());
		int maxVoyagesPerVessel = Collections.max(problemData.getVesselDeparturePatterns().keySet());
		int maxVoyages = problemData.getVessels().size()*maxVoyagesPerVessel;
		if (maxVisitsDemandedByInstallation > maxVoyages) {
			return false;
		}
		
		int maxVisitsPerVoyage = Integer.parseInt(problemData.getProblemInstanceParameters().get("Maximum number of installations"));
		int maxVisits = maxVoyages * maxVisitsPerVoyage;
		
		int demandedVisits = 0;
		for (Installation installation : problemData.getInstallations()) {
			demandedVisits += installation.getFrequency();
		}
		if (demandedVisits > maxVisits) {
			return false;
		}
		
		return true;
		
	}

}
