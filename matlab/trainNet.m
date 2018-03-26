clear all;
[~, labels] = getMNISTdata(); % gets images and labels
labels = labels(1:50);
imds = imageDatastore('netImages', 'Labels', categorical(labels));
[imdsTrain,imdsValidation] = splitEachLabel(imds,0.7,'randomized');

numTrain = numel(imdsTrain);
numValid = numel(imdsValidation);

net = alexnet;
layersTransfer = net.Layers(1:end-3);
numClasses = numel(unique(imdsTrain.Labels));
layers = [
    layersTransfer
    fullyConnectedLayer(numClasses,'WeightLearnRateFactor',20,'BiasLearnRateFactor',20)
    softmaxLayer
    classificationLayer];
options = trainingOptions('sgdm', ...
    'MiniBatchSize',10, ...
    'MaxEpochs',6, ...
    'InitialLearnRate',1e-4, ...
    'Verbose',true);
disp('Done setting up parameters');
netTransfer = trainNetwork(imdsTrain,layers,options);
return


