package ea;

import java.util.ArrayList;

import ea.common.AdultSelectionFullGenerational;
import ea.common.AdultSelectionGenerationalMixing;
import ea.common.AdultSelectionOverproduction;
import ea.common.ParentSelectionFitnessProportionate;
import ea.common.Reproduction;
import ea.onemax.FitnessOneMax;
import ea.onemax.GeneticOperatorOneMax;
import ea.onemax.GenoToPhenoOneMax;
import ea.onemax.InitialPopulationOneMax;
import ea.onemax.LocalSearchOneMax;
import ea.onemax.StopOneMax;
import ea.protocols.FitnessEvaluationProtocol;
import ea.protocols.GeneticOperatorProtocol;
import ea.protocols.GenoToPhenoProtocol;
import ea.protocols.InitialPopulationProtocol;
import ea.protocols.LocalSearchProtocol;
import ea.protocols.ParentSelectionProtocol;
import ea.protocols.ReproductionProtocol;
import ea.protocols.StopProtocol;

public class EAprocesses {
	
	private InitialPopulationProtocol initialPopulationProtocol;
	private GenoToPhenoProtocol genoToPhenoProtocol;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private AdultSelectionProtocol adultSelectionProtocol;
	private LocalSearchProtocol localSearchProtocol;
	private StopProtocol stopProtocol;
	private ParentSelectionProtocol parentSelectionProtocol;
	private ReproductionProtocol reproductionProtocol;
	private GeneticOperatorProtocol geneticOperatorProtocol;
	
	private Parameters parameters;

	public EAprocesses(Parameters parameters) {
		this.parameters = parameters;
		selectProtocols();
	}
	
	public ArrayList<Individual> createInitialPopulation() {
		return initialPopulationProtocol.createInitialPopulation(parameters);
	}
	
	public void convertGenoToPheno(Population children) {
		genoToPhenoProtocol.convertGenoToPheno(children);
	}
	
	public void evaluateFitness(Population population) {
		fitnessEvaluationProtocol.evaluateFitness(population);
	}

	public void selectAdults(Population children, Population adults) {
		adultSelectionProtocol.selectAdults(children, adults, parameters);
	}
	
	public void improveIndividual(Population children) {
		localSearchProtocol.improveIndividuals(children);
	}
	
	public boolean stoppingCriterion(Population adults, int generationNumber) {
		return stopProtocol.stoppingCriterion(adults, generationNumber, parameters);
	}
	
	public ArrayList<ArrayList<Individual>> selectParents(Population adults) {
		return parentSelectionProtocol.selectParents(adults, parameters.getnChildren());
	}
	
	public ArrayList<Individual> reproduction(ArrayList<ArrayList<Individual>> parents) {
		return reproductionProtocol.reproduction(parents, parameters.getCrossoverRate(), parameters.getMutationRate());
	}	
	
	private void selectProtocols() {
		selectInitialPopulationProtocol();
		selectGenoToPhenoProtocol();
		selectFitnessEvaluationProtocol();
		selectAdultSelectionProtocol();
		selectLocalSearchProtocol();
		selectStopProtocol();
		selectParentSelectionProtocol();
		selectGeneticOperatorProtocol();
		selectReproductionProtocol();
	}
	
	private void selectInitialPopulationProtocol() {
		switch (parameters.getInitialPopulationProtocolString()) {
			case "OneMax": initialPopulationProtocol = new InitialPopulationOneMax();
				break;
			default: initialPopulationProtocol = null;
				break;
		}
	}
	
	private void selectGenoToPhenoProtocol() {
		switch (parameters.getGenoToPhenoProtocolString()) {
			case "OneMax" : genoToPhenoProtocol = new GenoToPhenoOneMax();
				break;
			default: genoToPhenoProtocol = null;
				break;
		}
	}
	
	private void selectFitnessEvaluationProtocol() {
		switch (parameters.getFitnessEvaluationProtocolString()) {
			case "OneMax" : fitnessEvaluationProtocol= new FitnessOneMax();
				break;
			default: fitnessEvaluationProtocol = null;
				break;
		}
	}
	
	private void selectAdultSelectionProtocol() {
		switch (parameters.getAdultSelectionProtocolString()) {
			case "Full Generational Replacement" : adultSelectionProtocol= new AdultSelectionFullGenerational();
				break;
			case "Overproduction" : adultSelectionProtocol= new AdultSelectionOverproduction();
				break;
			case "Generational Mixing" : adultSelectionProtocol= new AdultSelectionGenerationalMixing();
				break;
			default: adultSelectionProtocol = null;
				break;
		}
	}

	private void selectLocalSearchProtocol() {
		switch (parameters.getLocalSearchProtocolString()) {
			case "OneMax" : localSearchProtocol = new LocalSearchOneMax();
				break;
			default: localSearchProtocol = null;
				break;
		}
	}
	
	private void selectStopProtocol() {
		switch (parameters.getStopProtocolString()) {
			case "OneMax" : stopProtocol = new StopOneMax();
				break;
			default: stopProtocol = null;
				break;
		}
	}
	
	private void selectParentSelectionProtocol() {
		switch (parameters.getParentSelectionProtocolString()) {
			case "Fitness Proportionate" : parentSelectionProtocol = new ParentSelectionFitnessProportionate();
				break;
			default: parentSelectionProtocol = null;
				break;
		}
	}
	
	private void selectGeneticOperatorProtocol(){
		switch (parameters.getGeneticOperatorProtocolString()) {
		case "OneMax" : geneticOperatorProtocol = new GeneticOperatorOneMax();
			break;
		default: geneticOperatorProtocol = null;
			break;
	}
	}
	
	private void selectReproductionProtocol() {
		switch (parameters.getReproductionProtocolString()) {
			case "Standard" : reproductionProtocol = new Reproduction(geneticOperatorProtocol);
				break;
			default: reproductionProtocol = null;
				break;
		}
	}
	
}
