package ea;

import java.util.ArrayList;

import ea.common.AdultSelectionFullGenerational;
import ea.common.AdultSelectionGenerationalMixing;
import ea.common.AdultSelectionOverproduction;
import ea.common.ParentSelectionFitnessProportionate;
import ea.common.ReproductionStandard;
import ea.onemax.CrossoverOneMax;
import ea.onemax.FitnessOneMax;
import ea.onemax.GenoToPhenoOneMax;
import ea.onemax.InitialPopulationOneMax;
import ea.onemax.LocalSearchOneMax;
import ea.onemax.MutationOneMax;
import ea.onemax.StopOneMax;
import ea.protocols.AdultSelectionProtocol;
import ea.protocols.CrossoverOperator;
import ea.protocols.FitnessEvaluationProtocol;
import ea.protocols.GenoToPhenoProtocol;
import ea.protocols.InitialPopulationProtocol;
import ea.protocols.LocalSearchProtocol;
import ea.protocols.MutationOperator;
import ea.protocols.ParentSelectionProtocol;
import ea.protocols.ReproductionProtocol;
import ea.protocols.StopProtocol;
import ea.svpp.FitnessSVPP;
import ea.svpp.GenoToPhenoSVPP;
import ea.svpp.InitialPopulationSVPP;
import ea.svpp.ProblemDataSVPP;

public class EAprocesses {
	
	private InitialPopulationProtocol initialPopulationProtocol;
	private GenoToPhenoProtocol genoToPhenoProtocol;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private AdultSelectionProtocol adultSelectionProtocol;
	private LocalSearchProtocol localSearchProtocol;
	private StopProtocol stopProtocol;
	private ParentSelectionProtocol parentSelectionProtocol;
	private ReproductionProtocol reproductionProtocol;
	
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
		if (localSearchProtocol != null){
			localSearchProtocol.improveIndividuals(children);			
		}
	}
	
	public boolean stoppingCriterion(Population adults, int generationNumber) {
		return stopProtocol.stoppingCriterion(adults, generationNumber, parameters);
	}
	
	public ArrayList<ArrayList<Individual>> selectParents(Population adults) {
		return parentSelectionProtocol.selectParents(adults, parameters.getnChildren());
	}
	
	public ArrayList<Individual> reproduction(ArrayList<ArrayList<Individual>> parents) {
		return reproductionProtocol.reproduce(parents, parameters.getCrossoverRate(), parameters.getMutationRate());
	}	
	
	private void selectProtocols() {
		selectInitialPopulationProtocol();
		selectGenoToPhenoProtocol();
		selectFitnessEvaluationProtocol();
		selectAdultSelectionProtocol();
		selectLocalSearchProtocol();
		selectStopProtocol();
		selectParentSelectionProtocol();
		selectReproductionProtocol();
	}
	
	private void selectInitialPopulationProtocol() {
		switch (parameters.getInitialPopulationProtocolString()) {
			case "OneMax": initialPopulationProtocol = new InitialPopulationOneMax();
				break;
			case "SVPP": initialPopulationProtocol = new InitialPopulationSVPP();
				break;
			default: initialPopulationProtocol = null;
				break;
		}
	}
	
	private void selectGenoToPhenoProtocol() {
		switch (parameters.getGenoToPhenoProtocolString()) {
			case "OneMax" : genoToPhenoProtocol = new GenoToPhenoOneMax();
				break;
			case "SVPP" : genoToPhenoProtocol = new GenoToPhenoSVPP();
				break;
			default: genoToPhenoProtocol = null;
				break;
		}
	}
	
	private void selectFitnessEvaluationProtocol() {
		switch (parameters.getFitnessEvaluationProtocolString()) {
			case "OneMax" : fitnessEvaluationProtocol= new FitnessOneMax();
				break;
			case "SVPP" :
				String fitnessFunction = parameters.getOptionalParameters().get("SVPPFitness");
				ProblemDataSVPP problemData = (ProblemDataSVPP) parameters.getProblemData();
				fitnessEvaluationProtocol = new FitnessSVPP(problemData, fitnessFunction);
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
	
	private CrossoverOperator selectCrossoverOperator(){
		CrossoverOperator crossoverOperator;
		
		// Selecting crossover operator
		switch (parameters.getCrossoverOperatorString()) {
		case "OneMax" : return new CrossoverOneMax();
		default: return null;
		}
	}
	
	private MutationOperator selectMutationOperator(){
		MutationOperator mutationOperator;
		
		// Selecting mutation operator
		switch (parameters.getMutationOperatorString()) {
		case "OneMax" : return new MutationOneMax();
		default: return null;
		}
	}
	
	
	private void selectReproductionProtocol() {
		switch (parameters.getReproductionProtocolString()) {
			case "Standard" :
				CrossoverOperator crossoverOperator = selectCrossoverOperator();
				MutationOperator mutationOperator = selectMutationOperator();
				reproductionProtocol = new ReproductionStandard(crossoverOperator, mutationOperator);
				break;
			default: reproductionProtocol = null;
				break;
		}
	}
	
}
