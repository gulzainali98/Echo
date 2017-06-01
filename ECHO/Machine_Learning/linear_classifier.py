import numpy as np
from linear_svm import *


class LinearClassifier(object):

  def __init__(self):
    self.W = None

  def train(self, X, y, learning_rate=1e-3, reg=1e-5, num_iters=100,
            batch_size=200, verbose=False):
  
    num_train, dim = X.shape
    num_classes = np.max(y) + 1 # assume y takes values 0...K-1 where K is number of classes
    if self.W is None:
        # lazily initialize W
        self.W = 0.001 * np.random.randn(dim, num_classes)

    # Run stochastic gradient descent to optimize W
    loss_history = []
    for it in xrange(num_iters):
        X_batch = None
        y_batch = None
        
        choice= np.random.choice(num_train,batch_size,replace=False)
        print choice
        X_batch=X[choice,:]
        y_batch=y[choice]


        # evaluate loss and gradient
        loss, grad = self.loss(X_batch, y_batch, reg)
        loss_history.append(loss)
        self.W-=learning_rate*grad

      
        #if verbose and it % 100 == 0:
        print 'iteration %d / %d: loss %f' % (it, num_iters, loss)

    return loss_history

  def predict(self, X):
  
    y_pred = np.zeros(X.shape[1])
    ###########################################################################
    # TODO:                                                                   #
    # Implement this method. Store the predicted labels in y_pred.            #
    ###########################################################################
    pass
    ###########################################################################
    #                           END OF YOUR CODE                              #
    ###########################################################################
    return y_pred
  
  def loss(self, X_batch, y_batch, reg):
  
    pass


class LinearSVM(LinearClassifier):
  
  """ A subclass that uses the Multiclass SVM loss function """

  def loss(self, X_batch, y_batch, reg):
    print "omer JAvaid LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL3"  
    print reg
    
    return svm_loss_naive(self.W, X_batch, y_batch, reg)


