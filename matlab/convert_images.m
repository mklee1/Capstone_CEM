clear all; close all;
[images, labels] = getMNISTdata(); % gets images and labels
close;

net = alexnet;
inputSize = net.Layers(1).InputSize;

%for i = 1:length(images)
for i = 1:50
    disp(i)
    img = images(:,i);
    resizeFactor = inputSize(1)/length(img)^0.5;
    img = reshape(img, 28, 28);
    img = imresize(img, resizeFactor);
    img = repmat(img, 1, 1, 3);
    imwrite(img, sprintf('netImages/img%d.jpg', i)); %save as image for alexnet
end