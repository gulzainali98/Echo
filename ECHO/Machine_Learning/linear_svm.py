import numpy as np
from random import shuffle

def svm_loss_naive(W, X, y, reg):
  """

  """
  dW = np.zeros(W.shape) # initialize the gradient as zero

  # compute the loss and the gradient
  num_classes = W.shape[1]
  num_train = X.shape[0]
  loss = 0.0
  for i in xrange(num_train):
    scores = X[i].dot(W)
    correct_class_score = scores[y[i]]
    for j in xrange(num_classes):
      if j == y[i]:
        continue
      margin = scores[j] - correct_class_score + 1 # note delta = 1
      if margin > 0:
        loss += margin
        dW[:,j]+=X[i,:]
        dW[:,y[i]]-=X[i,:]
        

  loss /= num_train
  dW=dW/num_train

  # Add regularization to the loss.
  loss += 0.5 * reg * np.sum(W * W)


  return loss, dW


