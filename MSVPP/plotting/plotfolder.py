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
	pl.subplot(611);
	pl.plot(data[:,0], data[:,1], 'r',label=headerArray[1])
	pl.plot(data[:,0], data[:,2], 'b', label=headerArray[2])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the second subplot plots the penalities
	pl.subplot(612);
	pl.plot(data[:,0], data[:,3], 'r', label=headerArray[3])
	pl.plot(data[:,0], data[:,4], 'b', label=headerArray[4])
	pl.plot(data[:,0], data[:,5], 'g', label=headerArray[5])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the third subplot plots the best penalized cost and the best feasible cost
	pl.subplot(613)
	pl.plot(data[:,0], data[:,6], label=headerArray[6])
	pl.plot(data[:,0], data[:,7], label=headerArray[7])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the fourth subplot plots the violations of the individual with the best penalized cost
	pl.subplot(614);
	pl.plot(data[:,0], data[:,8], 'r', label=headerArray[8])
	pl.plot(data[:,0], data[:,9], 'b', label=headerArray[9])
	pl.plot(data[:,0], data[:,10], 'g', label=headerArray[10])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the fifth subplot plots the average violations of the population
	pl.subplot(615);
	pl.plot(data[:,0], data[:,11], 'r', label=headerArray[11])
	pl.plot(data[:,0], data[:,12], 'b', label=headerArray[12])
	pl.plot(data[:,0], data[:,13], 'g', label=headerArray[13])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)
	#the sixth subplot plots the gap from the best feasible solution to the best known solution
	pl.subplot(616);
	pl.plot(data[:,0], data[:,14], 'r', label=headerArray[14])
	pl.xlabel('Iteration')
	pl.ylabel('Value')
	pl.legend(loc=0)


	pl.tight_layout()
	pl.savefig(outputFolderpath + trimmedFilename + '.' + outputFileFormat, format = outputFileFormat, bbox_inches='tight')
	pl.close()
	
	print("Plotted " + trimmedFilename)
	return

def trimFilename(filepath):
	splittedFilepath = filepath.split("/")
	return splittedFilepath[len(splittedFilepath)-1][:-4]


listOfInputFiles = glob.glob(inputFolderpath +"*.txt")
plotAllFilesInList(listOfInputFiles)