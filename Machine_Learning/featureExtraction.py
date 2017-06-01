import numpy as np
import librosa 
import librosa.display 
import MFCC
import glob, os

def write_to_csv(fold0 ,fold_n ,location):
	count=0
	for i in range(fold0,fold_n):
	    path=location+st(i)			#"/home/Desktop/UrbanSound8K/UrbanSound8K/audio/fold"  +str(i)
	    os.chdir(path)
	     
		
	    print path
	    for file in glob.glob("*.wav"):
		if(file.split('-')[1]=='6'):
		    mfcc=MFCC.extract_mfcc(file)
		    mfcc=np.hstack([np.ones((mfcc.shape[0], 1)),mfcc])
		    
		    
		 
		    
		    with open("/home/omer/Desktop/UrbanSound8K/UrbanSound8K/audio/Data_Gunskhdot.csv",'a') as f_handle:
		  	  np.savetxt(f_handle,mfcc,delimiter=",")
    
        
