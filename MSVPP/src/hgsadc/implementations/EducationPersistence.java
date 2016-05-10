package hgsadc.implementations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.FNEG;

import hgsadc.Individual;
import hgsadc.Installation;
import hgsadc.ProblemData;
import hgsadc.Utilities;
import hgsadc.protocols.FitnessEvaluationProtocol;
import hgsadc.protocols.GenoToPhenoConverterProtocol;

public class EducationPersistence extends EducationStandard {

	public EducationPersistence(ProblemData problemdata, FitnessEvaluationProtocol fitnessEvaluationProtocol,
			PenaltyAdjustmentProtocol penaltyAdjustmentProtocol, GenoToPhenoConverterProtocol genoToPhenoConverter) {
		super(problemdata, fitnessEvaluationProtocol, penaltyAdjustmentProtocol, genoToPhenoConverter);
	}

	@Override
	public void educate(Individual individual) {

		if (new Random().nextBoolean() || isRepair){ // costEducate with probability 0.5
//			System.out.println("Educating cost");
			costEducate(individual);
		}
		else {
//			System.out.println("Educating persistence...");
			persistenceEducate(individual); // persistenceEducate with probability 0.5
		}
	}

	
	private void persistenceEducate(Individual individual) {
		installationPatternImprovementForPersistence(individual);
	}
		

	private void installationPatternImprovementForPersistence(Individual individual){
		/*
		 * pick one random installation i
		 * change departure pattern to the correct (baseline) pattern
		 * if (new pattern is feasible wrt depot, feasible patterns, psv patterns)
		 * 		keep pattern
		 * else
		 * 		move on to next random installation
		 * end-if
		 */
		
		HashMap<Integer, Set<Integer>> baselinePattern = problemData.getBaselineInstallationPattern();
		HashMap<Integer, Set<Integer>> currentPattern = ((GenotypeHGS) individual.getGenotype()).getInstallationDeparturePatternChromosome();
		
		HashMap<Integer, Set<Integer>> vesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> reversedVesselPatterns = ((GenotypeHGS) individual.getGenotype()).getVesselDeparturesPerDay();
		
		Set<Integer> alreadyCheckedInstallations = new HashSet<>();
		
		while (alreadyCheckedInstallations.size() < problemData.getCustomerInstallations().size()){
			
			Installation inst = Utilities.pickRandomElementFromList(problemData.getCustomerInstallations());
			int instNumber = inst.getNumber();
//			System.out.println("Changing pattern of installation " + instNumber);
			
			if (!baselinePattern.containsKey(instNumber)){
				alreadyCheckedInstallations.add(instNumber);
				continue;
			}
			
			Set<Integer> baselineInstPattern =  baselinePattern.get(instNumber);
			
			if (baselineInstPattern.equals(currentPattern.get(instNumber))){
				// Zero changes in this installation's pattern, or baseline does not contain the installation
//				System.out.println("Baseline = current pattern: ");
//				System.out.println("Baseline: " + baselineInstPattern);
//				System.out.println("Current: " + currentPattern.get(instNumber));
				alreadyCheckedInstallations.add(instNumber);
			}
			else {
				HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> giantTourWithoutInstallation = getCopyOfGiantTourWithoutInstallation(inst, individual);
				
				ArrayList<VoyageInsertion> insertions = new ArrayList<>();
				for (Integer day : baselineInstPattern){
					insertions.add(findLeastCostInsertionOnDay(instNumber, day, giantTourWithoutInstallation, vesselPatterns, reversedVesselPatterns));
				}
				Individual newIndividual = applyInsertions(individual, insertions, inst);
				individual.setGenotypeAndUpdatePenalizedCost(newIndividual.getGenotype(), genoToPhenoConverter, fitnessEvaluationProtocol);
				break; // Only change one installation at a time
			}
			/* Feasibility checks:
			*	Depot capacity: not necessary, can always add installation to an existing voyage
			*	PSV patterns: not neccessary, is penalized in penalized cost
			**/
		}
		
	}
		

	private void costEducate(Individual individual){
		super.educate(individual);
	}
}
