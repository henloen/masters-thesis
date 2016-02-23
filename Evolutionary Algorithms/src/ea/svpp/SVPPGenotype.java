package ea.svpp;

import java.util.HashSet;

import ea.Genotype;

public class SVPPGenotype implements Genotype {
	
	public static int NUMBER_OF_PSVS;
	public static int NUMBER_OF_DAYS;
	
	private int[][] genotype = new int[NUMBER_OF_PSVS][NUMBER_OF_DAYS]; // Schedule. 1st dimension is PSVs, 2nd is days. Each element represents the voyage which PSV starts sailing on that day	
	
	public SVPPGenotype(int[][] genotype) {
		this.genotype = genotype;
	}


	@Override
	public Genotype cloneGenotype() {
		// TODO Auto-generated method stub
		return null;
	}

}
