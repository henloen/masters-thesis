package ea.svpp;

import java.util.Set;

import ea.Phenotype;

public class SVPPPhenotype implements Phenotype {
	
	public final Set<Integer> charteredPSVs;
	public final Set<int[]> voyagesSailed;
	/*
	 * Each array indicates indices v,r,t for which x_vrt = 1. E.g. if the array[3, 1, 2] is in the Set,
	 * PSV 3 sails voyage 1 on day 2
	*/
	
	@Override
	public Phenotype clonePhenotype() {
		// TODO Auto-generated method stub
		// What do we need this for?
		return null;
	}
	
	public SVPPPhenotype(Set<Integer> charteredPSVs, Set<int[]> voyagesSailed) {
		this.charteredPSVs = charteredPSVs;
		this.voyagesSailed =voyagesSailed;
	}
	
	public int getScheduleCost(){
		// TODO
		return 0;
	}
	
	public int getScheduleRobustness(){
		// TODO
		return 0;
	}
	
	public int getSchedulePersistence(){
		// TODO
		return 0;
	}
	
	public Set<Integer> getCharteredPSVs(){
		return charteredPSVs;
	}
	
	public int getNumberOfCharteredPSVs(){
		return charteredPSVs.size();
	}
	

}
