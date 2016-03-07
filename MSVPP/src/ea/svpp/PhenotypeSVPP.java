package ea.svpp;

import java.util.Set;

import ea.Phenotype;

public class PhenotypeSVPP implements Phenotype {
	
	private final Set<Integer> charteredPSVs;
	private final Set<int[]> voyagesSailed;
	/*
	 * Each array indicates indices v,r,t for which x_vrt = 1. E.g. if the array[3, 1, 2] is in the Set,
	 * PSV 3 sails voyage 1 on day 2
	*/
	
	@Override
	public Phenotype clonePhenotype() {
		return new PhenotypeSVPP(charteredPSVs, voyagesSailed);
	}
	
	public PhenotypeSVPP(Set<Integer> charteredPSVs, Set<int[]> voyagesSailed) {
		this.charteredPSVs = charteredPSVs;
		this.voyagesSailed =voyagesSailed;
	}
	
	public Set<int[]> getVoyagesSailed() {
		return voyagesSailed;
	}

	public Set<Integer> getCharteredPSVs(){
		return charteredPSVs;
	}
	
	public int getNumberOfCharteredPSVs(){
		return charteredPSVs.size();
	}
	
	public int getNumberOfVoyagesSailed(){
		return voyagesSailed.size();
	}

}
