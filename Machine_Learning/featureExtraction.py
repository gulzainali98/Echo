import numpy as np
import librosa 
import librosa.display 
import MFCC
import glob, os
 
def write_to_csv(location):
	 
	     
	print "omer"
	os.chdir(location)
	for file in glob.glob("*.wav"):
	    print "omer"
	    mfcc=MFCC.extract_mfcc(file)
	    mfcc=np.hstack([np.ones((mfcc.shape[0], 1)),mfcc])
	    
	    
	 
	    print mfcc.shape
	    with open("/home/omer/Desktop/Echo/Machine_Learning/Data_Gunshot.csv",'a') as f_handle:
	 	  np.savetxt(f_handle,mfcc,delimiter=",")


