package hgsadc.implementations;

import java.util.ArrayList;

public class VoyageInsertion {
	public DayVesselCell dayVesselCell;
	public int positionInVoyageToInsertInto;
	public double insertionCost;
	public int installationNumber;
	
	public VoyageInsertion(DayVesselCell dayVesselCell, int installationNumber, int positionInVoyageToInsertInto, double insertionCost) {
		this.dayVesselCell = dayVesselCell;
		this.installationNumber = installationNumber;
		this.positionInVoyageToInsertInto = positionInVoyageToInsertInto;
		this.insertionCost = insertionCost;
	}
	
	public static double getTotalInsertionCost(ArrayList<VoyageInsertion> insertions){
		double sum = 0;
		for (VoyageInsertion insertion : insertions){
			sum += insertion.insertionCost;
		}
		return sum;
	}
}
