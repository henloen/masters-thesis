package testsAndStuff;

import hgsadc.IO;
import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.implementations.EducationPersistence;
import hgsadc.implementations.EducationStandard;
import hgsadc.implementations.FitnessEvaluationMultiObjective;
import hgsadc.implementations.FitnessEvaluationStandard;
import hgsadc.implementations.GenoToPhenoConverterMultiObjective;
import hgsadc.implementations.GenoToPhenoConverterStandard;
import hgsadc.implementations.GenotypeHGS;
import hgsadc.implementations.PenaltyAdjustmentProtocol;
import hgsadc.protocols.EducationProtocol;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Test {
	
	public static void main(String[] args) {
		Test test = new Test();
//		test.testVoyage();
		test.testVoyageSwap();
	}
	
	private void testVoyageSwap(){
		IO io = new IO("data/hgs/input/input data hgs.xls");
		ProblemData problemData = io.readData(0);
		problemData.generatePatterns();
		FitnessEvaluationProtocol fitnessEvaluationProtocol = new FitnessEvaluationMultiObjective(problemData);
		GenoToPhenoConverterProtocol genoToPhenoProtocol = new GenoToPhenoConverterMultiObjective(problemData);
		PenaltyAdjustmentProtocol penaltyProtocol = new PenaltyAdjustmentProtocol(0);
		EducationPersistence education = new EducationPersistence(problemData, fitnessEvaluationProtocol, penaltyProtocol, genoToPhenoProtocol);
		
		Individual ind = generateTestIndividual();
		genoToPhenoProtocol.convertGenotypeToPhenotype(ind);
		
		System.out.println("\nOld schedule: ");
		System.out.println(ind.getPhenotype().getScheduleString());
		System.out.println("Persistence: " + ind.getNumberOfChangesFromBaseline());
		
		education.swapVoyages(ind);

		System.out.println("\nNew schedule: ");
		System.out.println(ind.getPhenotype().getScheduleString());
		System.out.println("Persistence: " + ind.getNumberOfChangesFromBaseline());
	}
	
	private Individual generateTestIndividual(){
		ArrayList<Integer> voy01 = new ArrayList<>();
		voy01.add(1);
		voy01.add(11);
		voy01.add(7);
		voy01.add(3);
		voy01.add(4);
		voy01.add(5);
		
		ArrayList<Integer> voy12 = new ArrayList<>();
		voy12.add(2);
		voy12.add(8);
		voy12.add(10);
		voy12.add(9);
		voy12.add(11);
		voy12.add(1);
		
		ArrayList<Integer> voy21 = new ArrayList<>();
		voy21.add(5);
		voy21.add(4);
		voy21.add(3);
		voy21.add(7);
		voy21.add(6);
		voy21.add(8);
		
		ArrayList<Integer> voy32 = new ArrayList<>();
		voy32.add(5);
		voy32.add(4);
		voy32.add(3);
		voy32.add(7);
		voy32.add(11);
		voy32.add(1);
		
		ArrayList<Integer> voy41 = new ArrayList<>();
		voy41.add(1);
		voy41.add(7);
		voy41.add(11);
		voy41.add(9);
		voy41.add(8);
		voy41.add(10);
		voy41.add(2);
		

		ArrayList<Integer> voy52 = new ArrayList<>();
		voy52.add(7);
		voy52.add(5);
		voy52.add(4);
		voy52.add(3);
		voy52.add(9);
		voy52.add(8);
		voy52.add(10);
		voy52.add(2);
		
		
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTour = GenotypeHGS.generateEmptyGiantTourChromosome(7, 2);
		
		giantTour.get(0).put(1, voy01);
		giantTour.get(1).put(2, voy12);
		giantTour.get(2).put(1, voy21);
		giantTour.get(3).put(2, voy32);
		giantTour.get(4).put(1, voy41);
		giantTour.get(5).put(2, voy52);
		
		Individual ind = new Individual(new GenotypeHGS(giantTour, 11, 2));
		
		return ind;
	}
	
	
	private void testVoyage() {
		IO io = new IO("data/hgs/input/input data hgs.xls");
		ProblemData problemData = io.readData(0);
		problemData.generatePatterns();
		FitnessEvaluationProtocol fitnessEvaluationProtocol = new FitnessEvaluationStandard(problemData);
		GenoToPhenoConverterProtocol genoToPhenoProtocol = new GenoToPhenoConverterStandard(problemData);
		PenaltyAdjustmentProtocol penalty = new PenaltyAdjustmentProtocol(0.2);
		EducationStandard education = new EducationStandard(problemData, fitnessEvaluationProtocol, penalty, genoToPhenoProtocol);

		
		//initialize optimal genotype
		ArrayList<Integer> installations1 = new ArrayList<Integer>();
		installations1.add(13);
		installations1.add(15);
		installations1.add(14);
		installations1.add(7);
		installations1.add(8);
		installations1.add(10);
		installations1.add(12);
		installations1.add(2);
		
		ArrayList<Integer> installations2 = new ArrayList<Integer>();
		installations2.add(1);
		installations2.add(11);
		installations2.add(9);
		installations2.add(3);
		installations2.add(4);
		installations2.add(5);
		installations2.add(7);
		installations2.add(14);
		
		ArrayList<Integer> installations3 = new ArrayList<Integer>();
		installations3.add(15);
		installations3.add(13);
		installations3.add(1);
		installations3.add(11);
		installations3.add(10);
		installations3.add(8);
		installations3.add(12);
		installations3.add(2);
		
		ArrayList<Integer> installations4 = new ArrayList<Integer>();
		installations4.add(14);
		installations4.add(5);
		installations4.add(4);
		installations4.add(3);
		installations4.add(7);
		installations4.add(15);
		
		ArrayList<Integer> installations5 = new ArrayList<Integer>();
		installations5.add(1);
		installations5.add(12);
		installations5.add(8);
		installations5.add(10);
		installations5.add(9);
		installations5.add(11);
		installations5.add(7);
		installations5.add(13);
		
		ArrayList<Integer> installations6 = new ArrayList<Integer>();
		installations6.add(15);
		installations6.add(14);
		installations6.add(5);
		installations6.add(4);
		installations6.add(3);
		installations6.add(7);
		installations6.add(6);
		installations6.add(13);
		
		ArrayList<Integer> installations7 = new ArrayList<Integer>();
		installations7.add(1);
		installations7.add(11);
		installations7.add(9);
		installations7.add(8);
		installations7.add(12);
		installations7.add(2);
		
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourChromosome =  new HashMap<Integer, HashMap<Integer,ArrayList<Integer>>>();
		
		for (int i = 0; i < 7; i++) {//days
			HashMap<Integer, ArrayList<Integer>> day = new HashMap<Integer, ArrayList<Integer>>();
			for (int j = 1; j < 3; j++) {//vessels
				day.put(j, new ArrayList<Integer>());
			}
			giantTourChromosome.put(i, day);
		}
		
		HashMap<Integer, ArrayList<Integer>> day0 = giantTourChromosome.get(0);
		day0.put(2, installations1);
		
		HashMap<Integer, ArrayList<Integer>> day1 = giantTourChromosome.get(1);
		day1.put(3, installations2);
		
		HashMap<Integer, ArrayList<Integer>> day2 = giantTourChromosome.get(2);
		day2.put(1, installations3);
	
		HashMap<Integer, ArrayList<Integer>> day3 = giantTourChromosome.get(3);
		day3.put(2, installations4);
		
		HashMap<Integer, ArrayList<Integer>> day4 = giantTourChromosome.get(4);
		day4.put(3, installations5);
		
		HashMap<Integer, ArrayList<Integer>> day5 = giantTourChromosome.get(5);
		day5.put(1, installations6);
		day5.put(2, installations7);
		
		System.out.println("Best found solution");
		
		GenotypeHGS bfGenotype = new GenotypeHGS(giantTourChromosome, 15, 3);
		Individual bfIndividual = new Individual(bfGenotype);
		System.out.println(bfGenotype);
		
		genoToPhenoProtocol.convertGenotypeToPhenotype(bfIndividual);
		fitnessEvaluationProtocol.setPenalizedCostIndividual(bfIndividual);
		System.out.println(bfIndividual.getPenalizedCost());
		
		//education.voyageReduction(bfIndividual);
		education.installationPatternSwap(bfIndividual);
		System.out.println(bfIndividual.getPenalizedCost());
		System.out.println(bfIndividual.getPhenotype().getScheduleString());
		
		
	}

}
