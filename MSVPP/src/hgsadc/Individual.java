package hgsadc;

import hgsadc.protocols.Genotype;
import hgsadc.protocols.Phenotype;


public class Individual {

	private Genotype genotype;
	private Phenotype phenotype;
	private double penalizedCost;
	
	public Individual(Genotype genotype) {
		this.genotype = genotype;
	}
	
	public Genotype getGenotype() {
		return genotype;
	}
	
	public void setPhenotype(Phenotype phenotype) {
		this.phenotype = phenotype;
	}
	
	public Phenotype getPhenotype() {
		return phenotype;
	}
	
	public double getPenalizedCost() {
		return penalizedCost;
	}

	public void setPenalizedCost(double penalizedCost) {
		this.penalizedCost = penalizedCost;
	}
	
	public String toString() {
		String str = "";
		if (phenotype != null) {
			str += "Penalized cost: " + penalizedCost + "\n" + phenotype.toString() + "\n"; 
		}
		return  str;
	}
	
}
