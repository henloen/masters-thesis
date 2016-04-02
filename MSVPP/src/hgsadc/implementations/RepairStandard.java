package hgsadc.implementations;

import hgsadc.Individual;
import hgsadc.protocols.RepairProtocol;

public class RepairStandard implements RepairProtocol {

	@Override
	public void repair(Individual individual) {
		System.out.println("Repairing individual " + individual);
	}

}
