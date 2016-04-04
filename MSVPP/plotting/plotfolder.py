import numpy as np
import pylab as pl
import sys
import glob

#run this file by typing "python pythonfolder.py <foldername>" in the command line. All .txt-files in the folder named "foldername" will be plotted and exported

#inputFoldername = sys.argv[1]
inputFolderpath = '../data/hgs/output/' #+ inputFoldername +"/"
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
	pl.figure(figsize=(10,15))
	pl.title(parameterString)
	#the first subplot plots the number of individuals
	pl.subplot(411);
	pl.plot(data[:,0], data[:,1], label=headerArray[1])
	pl.plot(data[:,0], data[:,2], label=headerArray[2])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the second subplot plots the penalities
	pl.subplot(412);
	pl.plot(data[:,0], data[:,3], label=headerArray[3])
	pl.plot(data[:,0], data[:,4], label=headerArray[4])
	pl.plot(data[:,0], data[:,5], label=headerArray[5])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the third subplot plots the best penalized cost and the best feasible cost
	pl.subplot(413)
	pl.plot(data[:,0], data[:,6], label=headerArray[6])
	pl.plot(data[:,0], data[:,7], label=headerArray[7])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the fourth subplot plots the violations of the individual with the best penalized cost
	pl.subplot(414);
	pl.plot(data[:,0], data[:,8], label=headerArray[8])
	pl.plot(data[:,0], data[:,9], label=headerArray[9])
	pl.plot(data[:,0], data[:,10], label=headerArray[10])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)

	pl.tight_layout()
	pl.savefig(outputFolderpath + trimmedFilename + outputFileFormat, bbox_inches='tight')
	pl.close()
	
	print("Plotted " + trimmedFilename)
	return

def trimFilename(filepath):
	splittedFilepath = filepath.split("/")
	return splittedFilepath[len(splittedFilepath)-1][:-4]


listOfInputFiles = glob.glob(inputFolderpath +"*.txt")
plotAllFilesInList(listOfInputFiles)