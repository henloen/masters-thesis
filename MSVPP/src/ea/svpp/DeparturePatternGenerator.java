package ea.svpp;

import java.util.ArrayList;
import java.util.HashMap;

public class DeparturePatternGenerator {
	/*
	 * hf ::	  [6, 2, 2, 3, 1, 0, 0] ! Horizon in which we need to constrain number of departures, given f required visits
	 * Pf_min ::  [0, 0, 1, 2, 1, 0, 0] ! Minimum no of departures to an installation in the horizon hf
	 * Pf_max ::  [1, 1, 3, 4, 2, 1, 1] ! Maximum no of departures to an installation in the horizon hf
	 */
	
	public static int[] hf = {6, 2, 2, 3, 1, 0, 0};
	public static int[] Pf_min = {0, 0, 1, 2, 1, 0, 0};
	public static int[] Pf_max = {1, 1, 3, 4, 2, 1, 1};
	
	/*public static void main(String[] args){
		
		HashMap<Integer, ArrayList<int[]>> feasibleDeparturePatterns = new HashMap<>();
		
		for (int f = 1; f <= 7; f++){ // f = frequency of visits per week
			
			for (int dep = 1; dep < f; dep++){ // dep = departure number
				for ()
			
			
			
			// Add loop: for all possible departurePatterns? 
				
			}
		}
		
	}*/
	
	private static long binomial(int n, int k)
    {
        if (k>n-k)
            k=n-k;
 
        long b=1;
        for (int i=1, m=n; i<=k; i++, m--)
            b=b*m/i;
        return b;
    }
}
