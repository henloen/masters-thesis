import numpy as np
import pylab as pl
import sys
import glob

inputFoldername = sys.argv[1]
inputFolderpath = '../data/hgs/output/pareto fronts/' + inputFoldername +"/"
inputFile = inputFoldername.split('-')[1] + '-Plotfile'
# Foldername on format Robustness-X, inputfile on format X-Plotfile, where X=number of installations
outputFolderpath = 'pareto front plots/'
outputFileFormat = 'pdf'
heuristicPlotNumber = 1
minCostDictionary = {
3: {'': 243629}, 4: {'': 258071}, 5: {'': 271043}, 6: {'': 274204}, 7: {'': 341015}, 8: {'': 363891}, 9: {'': 374896}, 10: {'': 385820}, 11: {'': 405146, 'Gullfaks increase': 451249},
12: {'': 449252, 'Load reduction project': 389441}, 13: {'': 469339}, 14: {'': 492392}, 15: {'': 537617}, 16: {'': 566264}, 17: {'': 581779}, 18: {'': 618460}, 19: {'': 657871}, 20: {'': 665070}, 21: {'': 669421}, 
22: {'': 678889}, 23: {'': 699145}, 24: {'': 706557}, 25: {'': 710489}, 26: {'': 732325} , 27: {'': 746008, 'Gullfaks increase': 791933, 'Load reduction project': 663183},
30: {'': 864329}
}
# '' empty string indicates standard case of problem

markerColours = ['lawngreen', 'r', 'gold','c','m']
markerShapes = ['^', 'v', 'o', 'p', '*']
markerSize = [10, 10, 7, 10, 10]
markerScale = 2




def exportPlot(filename):
	#add labels
	pl.xlabel('# changes from baseline')
	pl.ylabel('% increase from minimum sailing cost')
	pl.legend(loc=0, markerscale=(1.0/markerScale))

	#adjust axis
	axis = pl.gca()
	xlim = axis.get_xlim()
	ylim = axis.get_ylim()


	#pl.axis([-0.5, xlim[1]*1.1, -0.01, ylim[1]*1.1]) # dynamic scale
	#pl.axis([-0.5, 41, -0.15, 6]) # scale for instance 11 or 12
	#pl.axis([-0.5, 47, -0.15, 11]) # scale for instance 22, 27 and 30
	pl.axis([-0.5, xlim[1]*1.1, -0.05, ylim[1]*1.1]) # dynamic scale

	#save the plot
	exportFilename = getExportFilename(filename)
	pl.tight_layout()
	pl.savefig(outputFolderpath + exportFilename + '.' + outputFileFormat, format = outputFileFormat, bbox_inches='tight')
	pl.close()
