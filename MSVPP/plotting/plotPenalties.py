import numpy as np
import pylab as pl
import sys
import glob

#run this file by typing "python pythonfolder.py <foldername>" in the command line. All .txt-files in the folder named "foldername" will be plotted and exported

#inputFoldername = sys.argv[1]
inputFolderpath = '../data/hgs/output/' #+ inputFoldername +"/"
outputFolderpath = 'plots/'
outputFileFormat = 'pdf'

def plotAllFilesInList(listOfFilenames):
	for filename in listOfFilenames:
		plotSingleFile(filename)
	return

def getParameterString(parameter):
	parameterArray = parameter.strip().split('\t')
	return ", ".join(parameterArray)

def getNumberOfRowsToSkip(numberString):
	return int(numberString.split(' ')[-1])

def plotSingleFile(filename):
	
	#reads the two first rows to get the parameter settings and the headers of the data
	with open(filename, 'r') as f:
		numberOfRowsToSkip = getNumberOfRowsToSkip(f.readline())
		for num in range(numberOfRowsToSkip-1):
			headers = f.readline()
	f.close()

	data = np.loadtxt(filename, skiprows=numberOfRowsToSkip) #loads the data with built-in method, but skips the first rows

	headerArray = headers.strip().split('\t')

	trimmedFilename = trimFilename(filename) #used to create a plot with the filename without the entire filepath

	#if the dimensions of the loaded data is 1, the algorithm terminated after the construction heuristic
	if (len(data.shape) == 1):
		print "Solution was found in construction heuristic, " + trimmedFilename + " was not plotted"
		return
	
	pl.subplot(211);
	pl.plot(data[:,0], data[:,1], 'r',label=headerArray[1])
	pl.plot(data[:,0], data[:,2], 'b', label=headerArray[2])
	pl.xlabel('Iteration')
	pl.ylabel('Number of individuals')
	axes = pl.gca()
	axes.set_ylim(ymin=0)
	pl.legend(loc=1)
	#the second subplot plots the penalities
	pl.subplot(212);
	pl.plot(data[:,0], data[:,3], 'r', label=headerArray[3])
	pl.plot(data[:,0], data[:,4], 'b', label=headerArray[4])
	pl.plot(data[:,0], data[:,5], 'g', label=headerArray[5])
	pl.xlabel('Iteration')
	pl.ylabel('Penalty')
	pl.legend(loc=1)
	
	
	pl.tight_layout()
	pl.savefig(outputFolderpath + trimmedFilename + '.' + outputFileFormat, format = outputFileFormat, bbox_inches='tight')
	pl.close()
	
	print("Plotted " + trimmedFilename)
	return

def dontPlotZero(array):
	for i in range(len(array)-1):
		if array[i] == 0.0:
			array[i] = np.nan
	return array

def trimFilename(filepath):
	splittedFilepath = filepath.split("/")
	return splittedFilepath[len(splittedFilepath)-1][:-4]


listOfInputFiles = glob.glob(inputFolderpath +"*.txt")
plotAllFilesInList(listOfInputFiles)