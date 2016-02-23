package voyageGenerationDP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class SVPPProblemData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	ArrayList<Installation> installations;
	ArrayList<Vessel> vessels;
	double[][] distances;
	ArrayList<ArrayList<Vessel>> vesselSets;
	HashMap<Integer, ArrayList<Installation>> installationSetsByFrequency;
	ArrayList<Voyage> voyageSet;
	HashMap<Vessel, ArrayList<Voyage>> voyageSetByVessel;
	HashMap<Vessel, HashMap<Installation, ArrayList<Voyage>>> voyageSetByVesselAndInstallation;
	HashMap<Vessel, HashMap<Integer, ArrayList<Voyage>>> voyageSetByVesselAndDuration;
	HashMap<Vessel, HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>> voyageSetByVesselAndDurationAndSlack;

	static String inputFileName = "data/input/Input data.xls",
			outputFileName = "data/output/"; //sets the folder, see the constructor of IO for the filename format
	//heuristic parameters
	static int removeLongestArcs = 0, minInstallationsHeur = 0, maxArcsRemovedInstallation = 2;
	static double capacityFraction = 0;
	
	
	public SVPPProblemData(ArrayList<Installation> installations, ArrayList<Vessel> vessels, double[][] distances,
			ArrayList<ArrayList<Vessel>> vesselSets,
			HashMap<Integer, ArrayList<Installation>> installationSetsByFrequency, ArrayList<Voyage> voyageSet,
			HashMap<Vessel, ArrayList<Voyage>> voyageSetByVessel,
			HashMap<Vessel, HashMap<Installation, ArrayList<Voyage>>> voyageSetByVesselAndInstallation,
			HashMap<Vessel, HashMap<Integer, ArrayList<Voyage>>> voyageSetByVesselAndDuration,
			HashMap<Vessel, HashMap<Integer, HashMap<Integer, ArrayList<Voyage>>>> voyageSetByVesselAndDurationAndSlack) {
		
		this.installations = installations;
		this.vessels = vessels;
		this.distances = distances;
		this.vesselSets = vesselSets;
		this.installationSetsByFrequency = installationSetsByFrequency;
		this.voyageSet = voyageSet;
		this.voyageSetByVessel = voyageSetByVessel;
		this.voyageSetByVesselAndInstallation = voyageSetByVesselAndInstallation;
		this.voyageSetByVesselAndDuration = voyageSetByVesselAndDuration;
		this.voyageSetByVesselAndDurationAndSlack = voyageSetByVesselAndDurationAndSlack;
	}
	
}
