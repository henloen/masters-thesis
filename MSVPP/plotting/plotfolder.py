import numpy as np
import pylab as pl
import sys
import glob

#run this file by typing "python pythonfolder.py <foldername>" in the command line. All .txt-files in the folder named "foldername" will be plotted and exported

inputFoldername = sys.argv[1]
inputFolderpath = '../data/output/' + inputFoldername +"/"
outputFolderpath = 'plots/'
outputFileFormat = '.png'

def plotAllFilesInList(listOfFilenames):
	for filename in listOfFilenames:
		plotSingleFile(filename)
	return

def getParameterString(parameter):
	parameterArray = parameter.strip().split('\t')
	return ", ".join(parameterArray)

def plotSingleFile(filename):
	
	data = np.loadtxt(filename, skiprows=2) #loads the data with built-in method, but skips the first two rows
	
	#reads the two first rows to get the parameter settings and the headers of the data
	with open(filename, 'r') as f:
		parameterSettings = f.readline()
		headers = f.readline()
	f.close()

	parameterString = getParameterString(parameterSettings)
	headerArray = headers.strip().split('\t')

	trimmedFilename = trimFilename(filename) #used to create a plot with the filename without the entire filepath

	#plotting
	pl.figure()
	pl.plot(data[:,0], data[:,1], 'r-', label=headerArray[1])
	pl.plot(data[:,0], data[:,2], 'b-', label=headerArray[2])
	pl.plot(data[:,0], data[:,3], 'g-', label=headerArray[3])
	pl.xlabel('Generation')
	pl.ylabel('Value')
	pl.title(parameterString)
	pl.legend(loc=0)
	pl.savefig(outputFolderpath + trimmedFilename + outputFileFormat, bbox_inches='tight')
	pl.close()
	
	print("Plotted " + trimmedFilename)
	return

def trimFilename(filepath):
	splittedFilepath = filepath.split("/")
	return splittedFilepath[len(splittedFilepath)-1][:-4]


listOfInputFiles = glob.glob(inputFolderpath +"*.txt")
plotAllFilesInList(listOfInputFiles)