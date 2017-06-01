import librosa 
import numpy as np
import wave


def extract_mfcc(wav_file):
    y,sr=librosa.load(wav_file)
    feature=librosa.feature.mfcc(y=y,sr=sr,n_mfcc=60).T
    mfcc=np.mean(feature,0).reshape(1,60)
    return mfcc

