package ea.onemax;

import java.util.Random;

import ea.Genotype;

public class GenotypeOneMax implements Genotype {
	
	private int genotypeLength;
	private String genotypeString;
	
	//creates a genotype with a random genotypeString of length genotypeLength  
	public GenotypeOneMax(int genotypeLength) {
		this.genotypeLength = genotypeLength;
		genotypeString = "";
		Random rand = new Random();
		for (int i = 0; i < genotypeLength; i++) {
			boolean randomBoolean = rand.nextBoolean();
			if (randomBoolean) {
				genotypeString += "1";
			}
			else {
				genotypeString += "0";
			}
		}
	}
	
	//creates a genotype with the specified genotypeString
	public GenotypeOneMax(String genotypeString) {
		this.genotypeString = genotypeString;
		this.genotypeLength = genotypeString.length();
	}

	@Override
	public Genotype cloneGenotype() {
		return new GenotypeOneMax(genotypeString);
	}
	
	public String toString() {
		return genotypeString;
	}

	public String getGenotypeString() {
		return genotypeString;
	}

	public void setGenotypeString(String genotypeString) {
		this.genotypeString = genotypeString;
	}

	public int getGenotypeLength() {
		return genotypeLength;
	}

	public void setGenotypeLength(int genotypeLength) {
		this.genotypeLength = genotypeLength;
	}
	

}
