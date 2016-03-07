package ea.svpp;

import java.util.HashSet;
import java.util.Set;

import ea.Genotype;

public class GenotypeSVPP implements Genotype {
	
	public static int NUMBER_OF_PSVS;
	public static int NUMBER_OF_DAYS;
	
	private int[][] schedule = new int[NUMBER_OF_PSVS][NUMBER_OF_DAYS]; // Schedule. 1st dimension is PSVs, 2nd is days. Each element represents the voyage which PSV starts sailing on that day	
	private Set<Integer> charteredPSVs = new HashSet<Integer>();
	
	public int[][] getSchedule(){
		return schedule;
	}
	
	public Set<Integer> getcharteredPSVs(){
		return charteredPSVs;
	}

	public GenotypeSVPP(int[][] schedule, Set<Integer> charteredPSVs) {
		this.schedule = schedule;
		this.charteredPSVs = charteredPSVs;
	}

	@Override
	public Genotype cloneGenotype() {
		return new GenotypeSVPP(schedule, charteredPSVs);
	}

}
