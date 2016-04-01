package hgsadc;

import hgsadc.implementations.DiversificationStandard;
import hgsadc.implementations.EducationStandard;
import hgsadc.implementations.FitnessEvaluationStandard;
import hgsadc.implementations.GenoToPhenoConverterStandard;
import hgsadc.implementations.InitialPopulationStandard;
import hgsadc.implementations.ParentSelectionBinaryTournament;
import hgsadc.implementations.ReproductionStandard;
import hgsadc.implementations.SurvivorSelectionStandard;
import hgsadc.protocols.DiversificationProtocol;
import hgsadc.protocols.EducationProtocol;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;
import hgsadc.protocols.InitialPopulationProtocol;
import hgsadc.protocols.ParentSelectionProtocol;
import hgsadc.protocols.ReproductionProtocol;
import hgsadc.protocols.SurvivorSelectionProtocol;

import java.util.ArrayList;


public class HGSprocesses {
	
	private ProblemData problemData;
	private InitialPopulationProtocol initialPopulationProtocol;
	private GenoToPhenoConverterProtocol genoToPhenoConverterProtocol;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private ParentSelectionProtocol parentSelectionProtocol;
	private ReproductionProtocol reproductionProtocol;
	private EducationProtocol educationProtocol;
	private DiversificationProtocol diversificationProtocol;
	private SurvivorSelectionProtocol survivorSelectionProtocol;
	

	public HGSprocesses(ProblemData problemData) {
		this.problemData = problemData;
		selectProtocols();
	}
	
	public ArrayList<Individual> createInitialPopulation() {
		return initialPopulationProtocol.createInitialPopulation();
	}
	
	public void convertGenotypeToPhenotype(ArrayList<Individual> population) {
		genoToPhenoConverterProtocol.convertGenotypeToPhenotype(population);
		fitnessEvaluationProtocol.setPenalizedCost(population);
	}
	
	public void setPenalizedCost(Individual individual) {
		fitnessEvaluationProtocol.setPenalizedCost(individual);
	}
	
	public void updateBiasedFitness(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		fitnessEvaluationProtocol.updateBiasedFitness(entirePopulation);
	}
	
	public void addDiversityDistance(Individual individual) {
		fitnessEvaluationProtocol.addDiversityDistance(individual);
	}
	
	
	public ArrayList<Individual> selectParents(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		ArrayList<Individual> entirePopulation = Utilities.getAllElements(feasiblePopulation, infeasiblePopulation);
		return parentSelectionProtocol.selectParents(entirePopulation);
	}

	public Individual generateOffspring(ArrayList<Individual> parents) {
		return reproductionProtocol.crossover(parents);
	}

	public void educateOffspring(Individual offspring) {
		educationProtocol.educate(offspring);
	}

	public void diversify(ArrayList<Individual> feasiblePopulation, ArrayList<Individual> infeasiblePopulation) {
		diversificationProtocol.diversify(feasiblePopulation, infeasiblePopulation);
	}

	public void survivorSelection(ArrayList<Individual> subpopulation, ArrayList<Individual> otherSubpopulation) {
		survivorSelectionProtocol.selectSurvivors(subpopulation, otherSubpopulation, fitnessEvaluationProtocol);
	}
	
	private void selectProtocols() {
		selectInitialPopulationProtocol();
		selectGenoToPhenoConverterProtocol();
		selectFitnessEvaluationProtocol();
		selectParentSelectionProtocol();
		selectReproductionProtocol();
		selectEducationProtocol();
		selectDiversificationProtocol();
		selectSurvivorSelectionProtocol();
	}

	private void selectInitialPopulationProtocol() {
		switch (problemData.getHeuristicParameters().get("Initial population heuristic")) {
			case "standard": initialPopulationProtocol = new InitialPopulationStandard(problemData);
				break;
			default: initialPopulationProtocol = null;
				break;
		}
	}
	
	private void selectGenoToPhenoConverterProtocol() {
		switch (problemData.getHeuristicParameters().get("Genotype to phenotype converter protocol")) {
			case "standard": genoToPhenoConverterProtocol = new GenoToPhenoConverterStandard(problemData);
				break;
			default: genoToPhenoConverterProtocol = null;
				break;
		}
	}
	
	private void selectFitnessEvaluationProtocol() {
		switch (problemData.getHeuristicParameters().get("Fitness evaluation protocol")) {
			case "standard": fitnessEvaluationProtocol = new FitnessEvaluationStandard(problemData);
				break;
			default: fitnessEvaluationProtocol = null;
				break;
		}
	}
	
	private void selectParentSelectionProtocol() {
		switch (problemData.getHeuristicParameters().get("Parent selection protocol")) {
			case "binary tournament": parentSelectionProtocol = new ParentSelectionBinaryTournament();
				break;
			default: parentSelectionProtocol = null;
				break;
		}
	}

	private void selectReproductionProtocol() {
		switch (problemData.getHeuristicParameters().get("Reproduction protocol")) {
			case "standard": reproductionProtocol = new ReproductionStandard();
				break;
			default: reproductionProtocol = null;
				break;
		}
	}

	private void selectEducationProtocol() {
		switch (problemData.getHeuristicParameters().get("Education protocol")) {
			case "standard": educationProtocol = new EducationStandard();
				break;
			default: educationProtocol = null;
				break;
		}
	}
	
	private void selectDiversificationProtocol() {
		switch (problemData.getHeuristicParameters().get("Diversification protocol")) {
			case "standard": diversificationProtocol = new DiversificationStandard();
				break;
			default: diversificationProtocol = null;
				break;
		}
	}

	private void selectSurvivorSelectionProtocol() {
		switch (problemData.getHeuristicParameters().get("Survivor selection protocol")) {
			case "standard": survivorSelectionProtocol = new SurvivorSelectionStandard(problemData);
				break;
			default: survivorSelectionProtocol = null;
				break;
		}	
	}

}
