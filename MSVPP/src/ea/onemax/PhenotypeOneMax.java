package ea.onemax;

import ea.Phenotype;

public class PhenotypeOneMax implements Phenotype {
	
	private int phenotypeLength;
	private String phenotypeString;
	
	public PhenotypeOneMax(String phenotypeString) {
		this.phenotypeString = phenotypeString;
		this.phenotypeLength = phenotypeString.length();
	}
	
	public Phenotype clonePhenotype() {
		return new PhenotypeOneMax(phenotypeString);
	}
	
	public String getPhenotypeString() {
		return phenotypeString;
	}
	
	public void setPhenotypeString(String phenotypeString) {
		this.phenotypeString = phenotypeString;
	}
	
	public int getPhenotypeLength() {
		return phenotypeLength;
	}
	
	public String toString() {
		return phenotypeString;
	}
	
	

}
