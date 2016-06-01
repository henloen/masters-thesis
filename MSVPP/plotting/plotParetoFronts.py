import numpy as np
import pylab as pl
import sys
import glob

#run this file by typing "python pythonfolder.py <foldername>" in the command line. All .txt-files in the folder named "foldername" will be plotted and exported
# First argument is folder name, second argument determines if robustness or persistence is plotted
# Use "rob" as second argument to plot robustness vs cost, everything else will plot persistence
inputFoldername = sys.argv[1]
robustness = sys.argv[2] == "rob"
inputFolderpath = '../data/hgs/output/pareto fronts/' + inputFoldername +"/"
outputFolderpath = 'pareto front plots/'
outputFileFormat = 'pdf'
heuristicPlotNumber = 1

# '' empty string indicates standard case of problem
minCostDictionary = {
3: {'': 243629}, 4: {'': 258071}, 5: {'': 271043}, 6: {'': 274204}, 7: {'': 341015}, 8: {'': 363891}, 9: {'': 374896}, 10: {'': 385820}, 11: {'': 405146, 'Gullfaks increase': 451249},
12: {'': 449252, 'Load reduction project': 389441}, 13: {'': 469339}, 14: {'': 492392}, 15: {'': 537617}, 16: {'': 566264}, 17: {'': 581779}, 18: {'': 618460}, 19: {'': 657871}, 20: {'': 665070}, 21: {'': 669421}, 
22: {'': 678889}, 23: {'': 699145}, 24: {'': 706557}, 25: {'': 710489}, 26: {'': 732325} , 27: {'': 746008, 'Gullfaks increase': 791933, 'Load reduction project': 663183},
30: {'': 864329}
}

fleetCostDictionary = {
	3: 2800000, 4: 2800000, 5: 2800000, 6: 2800000, 7: 2800000, 8: 2800000, 9: 2800000, 10: 2800000, 11: 2800000, 12: 2800000, 13: 4200000, 14: 4200000, 15: 4200000, 16: 4200000, 17: 4200000, 18: 4200000, 19: 4200000,
	20: 5600000, 21: 5600000, 22: 5600000, 23: 5600000, 24: 5600000, 25: 5600000, 26: 5600000, 27: 5600000
}


markerColours = ['lawngreen', 'r', 'gold','c','m']
markerShapes = ['^', 'v', 'o', 'p', '*']
markerSize = [10, 10, 7, 10, 10]
markerScale = 2

def plotAllFilesInList(listOfFilenames):
	for filename in listOfFilenames:
		plotSingleFile(filename)
	exportPlot(filename)
	return


def plotSingleFile(filename):
	print (filename)
	with open(filename, 'r') as f:
		rowText = f.readline()
		if isExactSolution(filename):
			numberOfRowsToSkip = 2
			while ("!Pareto Front:" not in rowText):
				numberOfRowsToSkip += 1 
				rowText = f.readline()
		else:
			numberOfRowsToSkip = 2
			while ("Non-dominated solutions" not in rowText):
				numberOfRowsToSkip+= 1 
				rowText = f.readline()
	f.close()
	data = np.loadtxt(filename, skiprows=numberOfRowsToSkip) #loads the data with built-in method, but skips the first rows
	trimmedFilename = trimFilename(filename) #used to create a plot with the filename without the entire filepath
	
	(minCost, fleetCost) = getMinCostAndFleetCost(filename)

	#plotting
	if isExactSolution(filename):
		if robustness:
			xAxisCol = 2
		else:
			xAxisCol = 3

		sortedData = data[data[:,xAxisCol].argsort()]
		pl.plot(sortedData[:,xAxisCol], getPercentageFromMin(minCost+fleetCost, sortedData[:,4]+fleetCost), color='blue', marker='s', linestyle='-', label="Optimal front", ms=markerScale*10, zorder=1)
	else:
		heuristicLabel = "HGSADC run " + str(heuristicPlotNumber)

		if robustness:
			xAxisCol = 2
		else:
			xAxisCol = 1
		 
		sortedData = data[data[:,xAxisCol].argsort()]
		pl.plot(sortedData[:,xAxisCol], getPercentageFromMin(minCost+fleetCost, sortedData[:,0]+fleetCost), color=markerColours[heuristicPlotNumber-1], marker=markerShapes[heuristicPlotNumber-1], linestyle='-', label=heuristicLabel, ms=markerScale*markerSize[heuristicPlotNumber-1])
		global heuristicPlotNumber	
		heuristicPlotNumber += 1
	print("Plotted " + trimmedFilename)
	return

def getPercentageFromMin(minValue, value):
	 return ((value/minValue)-1)*100

def trimFilename(filepath):
	splittedFilepath = filepath.split("/")
	return splittedFilepath[len(splittedFilepath)-1][:-4]

def getExportFilename(filepath):
	splittedFilepath = filepath.split("_")
	firstPart = splittedFilepath[0].split(" ")
	problemSize = firstPart[len(firstPart)-1]
	return problemSize+"_"+splittedFilepath[1][:-4]

def isExactSolution(filename):
	splittedFilename = filename.split("_")
	return "EXACT" in splittedFilename[len(splittedFilename)-1]

def getMinCostAndFleetCost(filepath):
	(problemSize, variationCase) = getProblemSizeAndCase(filepath)
	minCost = minCostDictionary[int(problemSize)][variationCase]
	fleetCost = fleetCostDictionary[int(problemSize)]
	print(str(problemSize) + ", " + variationCase +  " has minimum cost " + str(minCost) + " and fleetCost " + str(fleetCost))
	return (minCost, fleetCost)

def getProblemSizeAndCase(filepath):
	splittedFilepath = filepath[0:-4] # Filepath on format: 2016-05-15 11.37 11_Gullfaks increase_EXACT.txt
	splittedFilepath = splittedFilepath.split("_") 
	firstPart = splittedFilepath[0].split(" ")
	problemSize = firstPart[len(firstPart)-1]
	variationCase = splittedFilepath[len(splittedFilepath)-1]
	if 'EXACT' in variationCase:
		variationCase = splittedFilepath[len(splittedFilepath)-2]

	if variationCase != 'Load reduction project' and variationCase != 'Gullfaks increase':
		variationCase = ''

	return (problemSize, variationCase)

def exportPlot(filename):
	#add labels
	if robustness:
		pl.xlabel('Robustness')
	else:
		pl.xlabel('# changes from baseline')

	pl.ylabel('% increase from minimum total cost')
	pl.legend(loc=2, markerscale=(1.0/markerScale))

	#adjust axis
	axis = pl.gca()
	xlim = axis.get_xlim()
	ylim = axis.get_ylim()

	#pl.axis([-0.5, xlim[1]*1.1, -0.01, ylim[1]*1.1]) # dynamic scale
	#pl.axis([-0.5, 41, -0.15, 6]) # scale for instance 11 or 12
	#pl.axis([-0.5, 47, -0.15, 11]) # scale for instance 22, 27 and 30
	if robustness:
		pl.axis([-0.01, xlim[1]*1.1, -0.05, ylim[1]*1.1]) # dynamic scale
	else:
		pl.axis([-0.5, xlim[1]*1.1, -0.05, ylim[1]*1.1]) # dynamic scale

	#save the plot
	exportFilename = getExportFilename(filename)
	pl.tight_layout()
	pl.savefig(outputFolderpath + exportFilename + '.' + outputFileFormat, format = outputFileFormat, bbox_inches='tight')
	pl.close()

listOfInputFiles = glob.glob(inputFolderpath +"*.txt")
plotAllFilesInList(listOfInputFiles)