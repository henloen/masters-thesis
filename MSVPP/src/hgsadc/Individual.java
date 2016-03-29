package hgsadc;

import hgsadc.protocols.Genotype;
import hgsadc.protocols.Phenotype;


public class Individual {

	private Genotype genotype;
	private Phenotype phenotype;
	
	public Individual(Genotype genotype) {
		this.genotype = genotype;
	}
	
	public Genotype getGenotype() {
		return genotype;
	}
	
	public String toString() {
		return genotype.toString();
	}
	
}
