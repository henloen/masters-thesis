package ea.svpp;

import java.util.Set;

import ea.Phenotype;
import voyageGenerationDP.Vessel;

public class PhenotypeSVPP implements Phenotype {
	
	private GenotypeSVPP genotype;
	@Override
	public Phenotype clonePhenotype() {
		return new PhenotypeSVPP(genotype);
	}
	
	public PhenotypeSVPP(GenotypeSVPP genotype) {
		this.genotype = genotype;
	}

	public GenotypeSVPP getGenotype(){
		return genotype;
	}
	

}
