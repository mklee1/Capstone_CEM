import numpy
from keras.datasets import mnist
from keras.models import Sequential
from keras.layers import Dense
from keras.layers import Dropout
from keras.layers import Flatten
from keras.layers.convolutional import Conv2D
from keras.layers.convolutional import MaxPooling2D
from keras.utils import np_utils
from keras import backend as K


from keras.models import load_model

model = load_model('') # model location in directory


# load data
(X_train, y_train), (X_test, y_test) = mnist.load_data()
# reshape to be [samples][pixels][width][height]
X_train = X_train.reshape(X_train.shape[0], 1, 28, 28).astype('float32')
X_test = X_test.reshape(X_test.shape[0], 1, 28, 28).astype('float32')
# normalize inputs from 0-255 to 0-1
X_train = X_train / 255
X_test = X_test / 255


for i in range(20):
	img = X_test[i]
	img = img.reshape(1, 1, 28, 28)
	pred = model.predict_classes(img)
	pred_prob = model.predict_proba(img)
	print("predicted ", pred[0], " with probability ", pred_prob[0][pred])


