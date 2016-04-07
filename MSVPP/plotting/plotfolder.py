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

	#plotting
	pl.figure(figsize=(10,15))
	#pl.title(parameterString)
	#the first subplot plots the number of individuals
	pl.subplot(711);
	pl.plot(data[:,0], data[:,1], 'r',label=headerArray[1])
	pl.plot(data[:,0], data[:,2], 'b', label=headerArray[2])
	pl.xlabel('Iteration')
	pl.ylabel('Number of individuals')
	axes = pl.gca()
	axes.set_ylim(ymin=0)
	pl.legend(loc=0)
	#the second subplot plots the penalities
	pl.subplot(712);
	pl.plot(data[:,0], data[:,3], 'r', label=headerArray[3])
	pl.plot(data[:,0], data[:,4], 'b', label=headerArray[4])
	pl.plot(data[:,0], data[:,5], 'g', label=headerArray[5])
	pl.xlabel('Iteration')
	pl.ylabel('Penalty')
	pl.legend(loc=0)
	#the third subplot plots the best penalized cost and the best feasible cost
	pl.subplot(713)
	pl.plot(data[:,0], data[:,6], label=headerArray[6])
	pl.plot(data[:,0], data[:,7], label=headerArray[7])
	pl.xlabel('Iteration')
	pl.ylabel('Cost')
	axes = pl.gca()
	ylim = axes.get_ylim()
	axes.set_ylim(ymax=ylim[1]*1.1)#adjust the y-axis
	pl.legend(loc=0)
	#the fourth subplot plots the violations of the individual with the best penalized cost
	pl.subplot(714);
	pl.plot(data[:,0], data[:,8], 'r', label=headerArray[8])
	pl.plot(data[:,0], data[:,9], 'b', label=headerArray[9])
	pl.plot(data[:,0], data[:,10], 'g', label=headerArray[10])
	pl.xlabel('Iteration')
	pl.ylabel('Violation')
	pl.legend(loc=0)
	#the fifth subplot plots the average violations of the population
	pl.subplot(715);
	pl.plot(data[:,0], data[:,11], 'r', label=headerArray[11])
	pl.plot(data[:,0], data[:,12], 'b', label=headerArray[12])
	pl.plot(data[:,0], data[:,13], 'g', label=headerArray[13])
	pl.xlabel('Iteration')
	pl.ylabel('Violation')
	pl.legend(loc=0)
	#the sixth subplot plots the gap from the best feasible solution to the best known solution
	pl.subplot(716);
	pl.plot(data[:,0], data[:,14], 'r', label=headerArray[14])
	axes = pl.gca()
	axes.set_ylim([0,0.25])
	pl.xlabel('Iteration')
	pl.ylabel('Gap from BKS')
	pl.legend(loc=0)
	#the seventh subplot plots the average number of visits per voyage in the best feasible solution and the average of the population
	pl.subplot(717);
	pl.plot(data[:,0], data[:,15], 'r', label=headerArray[15])
	pl.plot(data[:,0], data[:,16], 'b', label=headerArray[16])
	pl.xlabel('Iteration')
	pl.ylabel('Visits pr voyage')
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