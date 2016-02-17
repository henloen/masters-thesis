package ea;

import java.util.ArrayList;

public interface AdultSelectionProtocol {
	public ArrayList<Individual> selectAdults(ArrayList<Individual> children, ArrayList<Individual> adults,int nAdults);
}
