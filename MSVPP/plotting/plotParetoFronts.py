import numpy as np
import pylab as pl
import sys
import glob

#run this file by typing "python pythonfolder.py <foldername>" in the command line. All .txt-files in the folder named "foldername" will be plotted and exported

#inputFoldername = sys.argv[1]
inputFolderpath = '../data/hgs/output/pareto fronts/' #+ inputFoldername +"/"
outputFolderpath = 'pareto front plots/'
outputFileFormat = 'pdf'
heuristicPlotNumber = 1
minCostDictionary = {
3: 243629, 4: 258071, 5: 271043, 6: 274204, 7: 341015, 8: 363891, 9: 374896, 10: 385820, 11: 405146, 12: 449252, 13: 469339, 14: 492392, 15: 537617,
16: 566264, 17: 581779, 18: 618460, 19: 657871, 20: 665070, 21: 669421, 22: 678889, 23: 699145, 24: 706557, 25: 710489, 26: 732325 ,27: 746008
}
markerColours = ['lawngreen', 'r', 'yellow','c','m']
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
	
	minCost = getMinCost(filename)

	#plotting
	if isExactSolution(filename):
		sortedData = data[data[:,3].argsort()]
		pl.plot(sortedData[:,3], getPercentageFromMin(minCost, sortedData[:,4]), color='blue', marker='s', linestyle='-', label="Exact front", ms=markerScale*10, zorder=1)
	else:
		heuristicLabel = "HGSADC run " + str(heuristicPlotNumber)
		sortedData = data[data[:,1].argsort()]
		pl.plot(sortedData[:,1], getPercentageFromMin(minCost, sortedData[:,0]), color=markerColours[heuristicPlotNumber-1], marker=markerShapes[heuristicPlotNumber-1], linestyle='-', label=heuristicLabel, ms=markerScale*markerSize[heuristicPlotNumber-1])
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

def getMinCost(filepath):
	splittedFilepath = filepath.split("_")
	firstPart = splittedFilepath[0].split(" ")
	problemSize = firstPart[len(firstPart)-1]
	return minCostDictionary[int(problemSize)]

def exportPlot(filename):
	#add labels
	pl.xlabel('# changes from baseline')
	pl.ylabel('% from minimal cost')
	pl.legend(loc=0, markerscale=(1.0/markerScale))

	#adjust axis
	axis = pl.gca()
	xlim = axis.get_xlim()
	ylim = axis.get_ylim()
	pl.axis([-0.5, xlim[1]*1.1, -0.5, ylim[1]*1.1])

	#save the plot
	exportFilename = getExportFilename(filename)
	pl.tight_layout()
	pl.savefig(outputFolderpath + exportFilename + '.' + outputFileFormat, format = outputFileFormat, bbox_inches='tight')
	pl.close()

listOfInputFiles = glob.glob(inputFolderpath +"*.txt")
plotAllFilesInList(listOfInputFiles)