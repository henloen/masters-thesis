package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.ProblemData;
import hgsadc.protocols.FitnessEvaluationProtocol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FitnessEvaluationStandard implements FitnessEvaluationProtocol {
	
	private ProblemData problemData;
	private double durationViolationPenalty, capacityViolationPenalty, numberOfInstallationsPenalty, ncloseProp, nEliteProp;
	private HashMap<Individual, HashMap<Individual, Double>> hammingDistances;

	public FitnessEvaluationStandard(ProblemData problemData) {
		this.problemData = problemData;
		this.hammingDistances = new HashMap<Individual, HashMap<Individual,Double>>();
		this.durationViolationPenalty = problemData.getHeuristicParameterDouble("Duration constraint violation penalty");
		this.capacityViolationPenalty = problemData.getHeuristicParameterDouble("Capacity constraint violation penalty");
		this.numberOfInstallationsPenalty = problemData.getHeuristicParameterDouble("Number of installations violation penalty");
		this.ncloseProp = problemData.getHeuristicParameterDouble("Proportion of individuals considered for distance evaluation");
		this.nEliteProp = problemData.getHeuristicParameterDouble("Proportion of elite individuals");
	}

	@Override
	public void setPenalizedCost(Individual individual) {
		PhenotypeHGS phenotype = (PhenotypeHGS) individual.getPhenotype();
		int minNumberOfInstallations = Integer.parseInt(problemData.getProblemInstanceParameters().get("Minimum number of installations"));
		int maxNumberOfInstallations = Integer.parseInt(problemData.getProblemInstanceParameters().get("Maximum number of installations"));
		double penalizedCost = phenotype.getScheduleCost()
				+ durationViolationPenalty*phenotype.getDurationViolation(problemData.getMinVoyageDurationHours(), problemData.getMaxVoyageDurationHours()) 
				+ capacityViolationPenalty*phenotype.getCapacityViolation()
				+ numberOfInstallationsPenalty*phenotype.getNumberOfInstallationsViolation(minNumberOfInstallations, maxNumberOfInstallations);
		individual.setPenalizedCost(penalizedCost);
	}

	@Override
	public void setPenalizedCost(ArrayList<Individual> individuals) {
		for (Individual individual : individuals) {
			setPenalizedCost(individual);
		}
	}
	
	@Override
	public void addDiversityDistance(Individual individual) {
		HashMap<Individual, Double> individualHammingDistances = new HashMap<Individual, Double>();
		for (Individual existingIndividual : hammingDistances.keySet()) {
			HashMap<Individual, Double> exisitingIndividualDistances = hammingDistances.get(existingIndividual);
			double normalizedHammingDistance = getNormalizedHammingDistance(individual, existingIndividual);
			exisitingIndividualDistances.put(individual, normalizedHammingDistance);
			individualHammingDistances.put(existingIndividual, normalizedHammingDistance);
			hammingDistances.put(existingIndividual, exisitingIndividualDistances);
		}
		hammingDistances.put(individual, individualHammingDistances);
	}
	
	//if visited on different days: +1
	//if visited by different vessels: +1
	private double getNormalizedHammingDistance(Individual individual1, Individual individual2) {
		GenotypeHGS genotype1 = (GenotypeHGS) individual1.getGenotype();
		GenotypeHGS genotype2 = (GenotypeHGS) individual2.getGenotype();
		
		int dayDiff = 0;
		int vesselDiff = 0;
		HashMap<Integer, Set<Integer>> installationPatterns1 = genotype1.getInstallationDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> installationPatterns2 = genotype2.getInstallationDeparturePatternChromosome();
		HashMap<Integer, Set<Integer>> vesselsByInstallation1 = genotype1.getVesselsByInstallation();
		HashMap<Integer, Set<Integer>> vesselsByInstallation2 = genotype2.getVesselsByInstallation();
		Set<Integer> installations = installationPatterns1.keySet();
		for (Integer installation : installations) {
			Set<Integer> departureDays1 = installationPatterns1.get(installation);
			Set<Integer> departureDays2 = installationPatterns2.get(installation);
			if (! departureDays1.equals(departureDays2)) {
				dayDiff++;
			}
			Set<Integer> vessels1 = vesselsByInstallation1.get(installation);
			Set<Integer> vessels2 = vesselsByInstallation2.get(installation);
			if (! vessels1.equals(vessels2)) {
				vesselDiff++;
			}
		}
		double numberOfInstallations = installations.size();
		
		return (dayDiff + vesselDiff)/(2*numberOfInstallations);
	}
	
	@Override
	public void updateBiasedFitness(ArrayList<Individual> individuals) {
		updateDiversityContribution(individuals);
		updatePenalizedCostRank(individuals);
		updateDiversityContributionRank(individuals);
		calculateBiasedFitness(individuals);
	}
	
	private void updatePenalizedCostRank(ArrayList<Individual> individuals) {
		Collections.sort(individuals, new Comparator<Individual>() {
			public int compare(Individual ind1, Individual ind2) {
				if (ind1.getPenalizedCost() < ind2.getPenalizedCost()) {
					return -1;
				}
				else if (ind1.getPenalizedCost() > ind2.getPenalizedCost()) {
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < individuals.size(); i++) {
			Individual individual = individuals.get(i);
			individual.setCostRank(i+1);
		}
	}
	
	private void updateDiversityContributionRank(ArrayList<Individual> individuals) {
		Collections.sort(individuals, new Comparator<Individual>() {
			public int compare(Individual ind1, Individual ind2) {
				if (ind1.getDiversityContribution() < ind2.getDiversityContribution()) {
					return -1;
				}
				else if (ind1.getDiversityContribution() > ind2.getDiversityContribution()) {
					return 1;
				}
				return 0;
			}
		});
		for (int i = 0; i < individuals.size(); i++) {
			Individual individual = individuals.get(i);
			individual.setDiversityRank(i+1);
		}
	}
	
	private void calculateBiasedFitness(ArrayList<Individual> individuals) {
		int nIndividuals = individuals.size(); 
		double nElite = (nIndividuals * nEliteProp);
		for (Individual individual : individuals) {
			double biasedFitness = individual.getCostRank() + (1 - (nElite/nIndividuals)) * individual.getDiversityRank();
			individual.setBiasedFitness(biasedFitness);
		}
	}
	
	
	private void updateDiversityContribution(ArrayList<Individual> individuals) {
		int nclose = (int) (individuals.size() * ncloseProp);
		for (Individual individual : individuals) {
			ArrayList<Individual> nclosest = getnClosest(individual, nclose);
			double distance = 0;
			for (Individual close : nclosest) {
				distance += hammingDistances.get(individual).get(close);
			}
			double diversityContribution = distance / nclose;
			individual.setDiversityContribution(diversityContribution);
		}
	}
	
	private ArrayList<Individual> getnClosest(Individual individual, int nclose) {
		ArrayList<Individual> nclosest = new ArrayList<Individual>();
		ArrayList<Map.Entry<Individual, Double>> otherIndividuals = new ArrayList<Map.Entry<Individual, Double>>(hammingDistances.get(individual).entrySet());
		Collections.sort(otherIndividuals, new Comparator<Map.Entry<Individual, Double>>() {
			public int compare (Map.Entry<Individual, Double> ind1, Map.Entry<Individual, Double> ind2) {
				return (ind1.getValue().compareTo(ind2.getValue()));
			}
		});
		for (int i = 0; i < nclose; i++) {
			nclosest.add(otherIndividuals.get(i).getKey());
		}
		return nclosest;
	}
	
	@Override
	public double getCapacityViolationPenalty() {
		return capacityViolationPenalty;
	}

	@Override
	public void setCapacityViolationPenalty(double penalty) {
		this.capacityViolationPenalty = penalty;
	}

	@Override
	public double getDurationViolationPenalty() {
		return durationViolationPenalty;
	}

	@Override
	public void setDurationViolationPenalty(double penalty) {
		this.durationViolationPenalty = penalty;
	}

}
