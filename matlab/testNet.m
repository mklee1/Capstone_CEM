for ind = 1:5
    I = imread(sprintf('netImages/img%d.jpg', ind));

    % Classify the image using AlexNet 
    label = classify(net, I)
    % Show the image and the classification results 
    figure 
    imshow(I) 
    text(10,20,char(label),'Color','white')
end
accuracy = 0;