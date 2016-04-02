package hgsadc;

import hgsadc.implementations.DiversificationStandard;
import hgsadc.implementations.EducationStandard;
import hgsadc.implementations.FitnessEvaluationStandard;
import hgsadc.implementations.GenoToPhenoConverterStandard;
import hgsadc.implementations.InitialPopulationStandard;
import hgsadc.implementations.ParentSelectionBinaryTournament;
import hgsadc.implementations.RepairStandard;
import hgsadc.implementations.ReproductionStandard;
import hgsadc.implementations.SurvivorSelectionStandard;
import hgsadc.protocols.DiversificationProtocol;
import hgsadc.protocols.EducationProtocol;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;
import hgsadc.protocols.InitialPopulationProtocol;
import hgsadc.protocols.ParentSelectionProtocol;
import hgsadc.protocols.RepairProtocol;
import hgsadc.protocols.ReproductionProtocol;
import hgsadc.protocols.SurvivorSelectionProtocol;

import java.util.ArrayList;
import java.util.Random;


public class HGSprocesses {
	
	private ProblemData problemData;
	
	private InitialPopulationProtocol initialPopulationProtocol;
	private GenoToPhenoConverterProtocol genoToPhenoConverterProtocol;
	private FitnessEvaluationProtocol fitnessEvaluationProtocol;
	private ParentSelectionProtocol parentSelectionProtocol;
	private ReproductionProtocol reproductionProtocol;
	private EducationProtocol educationProtocol;
	private RepairProtocol repairProtocol;
	private DiversificationProtocol diversificationProtocol;
	private SurvivorSelectionProtocol survivorSelectionProtocol;
	

	public HGSprocesses(ProblemData problemData) {
		this.problemData = problemData;
		selectProtocols();
	}
	
	public Individual createIndividual() {
		Individual individual = initialPopulationProtocol.createIndividual();
		convertGenotypeToPhenotype(individual);
		return individual;
	}
	
	public void convertGenotypeToPhenotype(Individual individual) {
		genoToPhenoConverterProtocol.convertGenotypeToPhenotype(individual);
		fitnessEvaluationProtocol.setPenalizedCostIndividual(individual);
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
		Individual individual = reproductionProtocol.crossover(parents);
		convertGenotypeToPhenotype(individual);
		return individual;
	}

	public void educate(Individual individual) {
		educationProtocol.educate(individual);
	}
	
	public void repair(Individual individual, double probability) {
		double randomDouble = new Random().nextDouble();
		if (probability < randomDouble) {
			repairProtocol.repair(individual);
		}
	}
	
	public void repair(Individual individual) {
		double probability = problemData.getHeuristicParameterDouble("Repair rate");
		repair(individual, probability);
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
		selectRepairProtocol();
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
			case "standard": reproductionProtocol = new ReproductionStandard(problemData, fitnessEvaluationProtocol);
				break;
			default: reproductionProtocol = null;
				break;
		}
	}

	private void selectEducationProtocol() {
		switch (problemData.getHeuristicParameters().get("Education protocol")) {
			case "standard": educationProtocol = new EducationStandard(problemData, fitnessEvaluationProtocol);
				break;
			default: educationProtocol = null;
				break;
		}
	}
	
	private void selectRepairProtocol() {
		switch (problemData.getHeuristicParameters().get("Repair protocol")) {
			case "standard": repairProtocol = new RepairStandard();
				break;
			default: repairProtocol = null;
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
