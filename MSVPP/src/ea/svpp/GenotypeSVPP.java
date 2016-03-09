package ea.svpp;

import java.util.HashMap;
import java.util.Set;

import ea.Genotype;
import voyageGenerationDP.Installation;
import voyageGenerationDP.Vessel;
import voyageGenerationDP.Voyage;

public class GenotypeSVPP implements Genotype {
	
	public static int NUMBER_OF_PSVS;
	public static int NUMBER_OF_DAYS;
	
	private HashMap<Vessel, Voyage[]> schedule;
	
	public Voyage[] getScheduleForPSV(Vessel vessel){
		if (schedule.get(vessel) != null){
			return schedule.get(vessel).clone();
		}
		else {
			return new Voyage[NUMBER_OF_DAYS];
		}
	}
	
	public Set<Vessel> getCharteredVessels(){
		return schedule.keySet();
	}
	
	public Voyage getDeparture(Vessel vessel, int day){
		if (schedule.get(vessel) != null){
			return schedule.get(vessel)[day];
		}
		else {
			return null;
		}
	}

	public int getNumberOfDeparturesOnDay(int day){
		return UtilitiesSVPP.getNumberOfDeparturesOnDay(day, schedule);
	}
	
	public int getNumberOfDaysSailing(Vessel vessel){
		Voyage[] scheduleForVessel = schedule.get(vessel);
		int nDaysSailing = 0;
		int day = 0;
		while (day < NUMBER_OF_DAYS){
			Voyage voyage = scheduleForVessel[day];
			if (voyage != null){
				nDaysSailing += voyage.getDuration();
				day += voyage.getDuration();
			}
			else day++;
		}
		return nDaysSailing;
	}
	
	public int getNumberOfDeparturesToInstallation(Installation installation, int day){
		int numberOfDeparturesToInstallation = 0;
		for (Vessel vessel : getCharteredVessels()){
			
			Voyage voyageStarted = getDeparture(vessel, day);
			if (voyageStarted != null){
				if (UtilitiesSVPP.voyageVisitsInstallation(voyageStarted, installation)){
					numberOfDeparturesToInstallation++;
				}
			}
		}
		return numberOfDeparturesToInstallation;
	}
	
	public GenotypeSVPP(HashMap<Vessel, Voyage[]> schedule) {
		this.schedule = schedule;
	}

	@Override
	public Genotype cloneGenotype() {
		return new GenotypeSVPP(schedule);
	}

}
