import numpy as np
import pylab as pl
import sys
import glob


filename = sys.argv[1]
filepath = '../data/hgs/output/Cost+Persistence+Robustness/' + filename + '.txt'

outputFolderpath = 'pareto front plots/'
outputFileFormat = 'pdf'
plotNumber = 1

# '' empty string indicates standard case of problem
minCostDictionary = {
3: {'': 243629}, 4: {'': 258070}, 5: {'': 271043}, 6: {'': 274204}, 7: {'': 341015}, 8: {'': 363891}, 9: {'': 374896}, 10: {'': 385820}, 11: {'': 405146, 'Gullfaks increase': 451249},
12: {'': 449252, 'Load reduction': 389441}, 13: {'': 469339}, 14: {'': 492392}, 15: {'': 537617}, 16: {'': 566264}, 17: {'': 581481}, 18: {'': 618460}, 19: {'': 657871}, 20: {'': 662276}, 21: {'': 667860}, 
22: {'': 678692}, 23: {'': 699145}, 24: {'': 703762}, 25: {'': 710489}, 26: {'': 727524} , 27: {'': 753134, 'Gullfaks increase': 780153, 'Load reduction': 663183},
30: {'': 854239}
}

fleetCostDictionary = {
	3: 2800000, 4: 2800000, 5: 2800000, 6: 2800000, 7: 2800000, 8: 2800000, 9: 2800000, 10: 2800000, 11: 2800000, 12: 2800000, 13: 4200000, 14: 4200000, 15: 4200000, 16: 4200000, 17: 4200000, 18: 4200000, 19: 4200000,
	20: 5600000, 21: 5600000, 22: 5600000, 23: 5600000, 24: 5600000, 25: 5600000, 26: 5600000, 27: 5600000, 30: 5600000
}

averageCostDictionary = {
	15: {'':  537684}, 16: {'': 567888}, 17: {'':  582530}, 18: {'': 626968}, 19: {'': 659061}, 20: {'': 665321}, 21: {'': 671154}, 
	22: {'': 680756}, 23: {'': 699145}, 24: {'': 705637}, 25: {'': 711997}, 26: {'': 730891} , 27: {'': 759404, 'Gullfaks increase': 789763, 'Load reduction': 663183},
	30: {'': 861983}
}

markerColours = ['blue', 'lawngreen', 'r', 'gold','c','m']
markerShapes = ['s', '^', 'v', 'o', 'p', '*']
markerSize = [10, 10, 10, 7, 10, 10]
markerScale = 2



def plotFront(filepath):
	with open(filepath, 'r') as f:
		line = f.readline()
		title = line

		line = f.readline()
		while  line != '':
			frontType = line
			isExactSolution = 'Optimal front' in line
			
			if isExactSolution:
				costCol = 2			
				robCol = 0
				changeCol = 1
			else:
				costCol = 0
				robCol = 2
				changeCol = 1

			line = f.readline()
			cost = []
			changes = []
			robustness = []
			while '-' not in line: # '-' denotes end of front
				splitline = line.split('\t')
				print(splitline)
				cost.append(int(splitline[costCol]))
				changes.append(int(splitline[changeCol]))
				robustness.append(float(splitline[robCol]))
				line = f.readline()
			
			frontType = frontType.rstrip()
			plotSingleRun(cost, changes, robustness, filename, frontType)
			line = f.readline()
			

	exportPlot(filename)


def plotSingleRun(cost, changes, robustness, filename, frontType):
	(minCost, fleetCost) = getMinCostAndFleetCost(filename)

	print(repr(frontType))

	print(cost)
	print(changes)
	print(robustness)

	global plotNumber

	
	dataPoint = 0
	frontForRunNum = 0

	while dataPoint < len(cost):
		rob = robustness[dataPoint]
		costForRob = []
		changesForRob = []

		while dataPoint < len(cost) and robustness[dataPoint] == rob:
			costForRob.append(cost[dataPoint])
			changesForRob.append(changes[dataPoint])
			dataPoint += 1

		costForRob = np.array(costForRob)
		changesForRob = np.array(changesForRob)

		incFromMinCost = np.array(getPercentageFromMin(minCost+fleetCost, costForRob+fleetCost))

		if frontForRunNum == 0:
			pl.plot(changesForRob, incFromMinCost, color=markerColours[plotNumber-1], marker=markerShapes[plotNumber-1], linestyle='-', label=frontType, ms=markerScale*markerSize[plotNumber-1], zorder=1)
		else:
			pl.plot(changesForRob, incFromMinCost, color=markerColours[plotNumber-1], marker=markerShapes[plotNumber-1], linestyle='-', label=None, ms=markerScale*markerSize[plotNumber-1], zorder=1)
		
		if plotNumber == 1:
			bbox_props = dict(boxstyle="round,pad=0.4", fc="white", ec="black", lw=1)
			pl.text(changesForRob[-1]-1.75, incFromMinCost[-1], str(rob), bbox=bbox_props)

		frontForRunNum += 1

	plotNumber += 1

def getPercentageFromMin(minValue, value):
	 return ((value/minValue)-1)*100

def getProblemSizeAndCase(filepath):
	splitPath = filepath.split('_')
	problemSize = splitPath[0]
	variationCase = splitPath[1]

	if variationCase != 'Load reduction' and variationCase != 'Gullfaks increase':
		variationCase = ''
	return (problemSize, variationCase)

def getMinCostAndFleetCost(filepath):
	(problemSize, variationCase) = getProblemSizeAndCase(filepath)
	minCost = minCostDictionary[int(problemSize)][variationCase]
	fleetCost = fleetCostDictionary[int(problemSize)]
	print(str(problemSize) + ", " + variationCase +  " has minimum cost " + str(minCost) + " and fleetCost " + str(fleetCost))
	return (minCost, fleetCost)

def exportPlot(filename):
	#add labels
	pl.xlabel('# changes from baseline')
	pl.ylabel('% increase from minimum total cost')
	pl.legend(loc=0, markerscale=(1.0/markerScale))

	#adjust axis
	axis = pl.gca()
	xlim = axis.get_xlim()
	ylim = axis.get_ylim()

	(problemSize, variationCase) = getProblemSizeAndCase(filename)
	problemSize = int(problemSize)

	if problemSize > 15:
		averageCost = averageCostDictionary[problemSize][variationCase]
		minCost = minCostDictionary[problemSize][variationCase]
		fleetCost = fleetCostDictionary[problemSize]
		averageCost = getPercentageFromMin(minCost+fleetCost, averageCost+fleetCost)
		pl.axhline(y=averageCost, xmin=0, xmax=xlim[1]*1.1, ls='--', color="red") # Horisontal dashed line 
		
		textXPos = 0.5

		pl.text(textXPos, averageCost*1.15, "Avg cost - single-objective runs")

	 # pl.axis([-0.5, xlim[1]*1.1, -0.01, 0.8]) #  22-shutdown 5 installations
	pl.axis([-0.5, xlim[1]*1.1, -0.05, ylim[1]*1.1]) # dynamic scale

	bbox_props = dict(boxstyle="round,pad=0.4", fc="white", ec="black", lw=1)
	# pl.text(13, 0.65, "Robustness value for front", bbox=bbox_props) #  22-shutdown 5 installations
	pl.text(0, 1.05, "Robustness value for front", bbox=bbox_props) #  11-add 3 installations
	
	#save the plot
	pl.tight_layout()
	filename = filename.replace(" ", "_")
	pl.savefig(outputFolderpath + filename + '.' + outputFileFormat, format = outputFileFormat, bbox_inches='tight')
	pl.close()

plotFront(filepath)