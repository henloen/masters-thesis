package ea.onemax;

import ea.Individual;
import ea.Phenotype;
import ea.Population;
import ea.protocols.GenoToPhenoProtocol;

public class GenoToPhenoOneMax implements GenoToPhenoProtocol {

	@Override
	public void convertGenoToPheno(Population population) {
		for (Individual individual : population.getIndividuals()) {
			setPhenotype(individual);
		}
	}
	
	private void setPhenotype(Individual individual) {
		GenotypeOneMax genotypeOneMax = (GenotypeOneMax) individual.getGenotype(); //cast to access "getGenotypeString"
		Phenotype phenotype = new PhenotypeOneMax(genotypeOneMax.getGenotypeString());
		individual.setPhenotype(phenotype);
	}

}
