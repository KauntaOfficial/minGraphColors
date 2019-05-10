function centroids = kMeansInitCentroids(X, K)
%KMEANSINITCENTROIDS This function initializes K centroids that are to be 
%used in K-Means on the dataset X
%   centroids = KMEANSINITCENTROIDS(X, K) returns K initial centroids to be
%   used with the K-Means on the dataset X
%

% You should return this values correctly
centroids = zeros(K, size(X, 2));

% ====================== YOUR CODE HERE ======================
% Instructions: You should set centroids to randomly chosen examples from
%               the dataset X
%

% Initialize the centroids to be random examples
% Randomly reorder the indices of examples
randidx = randperm(size(X, 1));
% Take the first K examples as centroids
centroids(1, :) = X(randidx(1), :);
centroidsComputed = 1;

while (centroidsComputed < K)
  weights = zeros(size(X,1), 1);

  for i = 1:size(X,1)
    % z = centroids .- X(i, :); // Only Octave compatible
    z = bsxfun(@minus, centroids, X(i, :));
    zy = sum(z.^2, 2);
    [~, weights(i)] = min(zy);
  end 
 
  %%// Spread out centroids to the third dimension so that the singleton
  %%// second dimension thus created could be used with bsxfun for expansion in
  %%// that dimension
  %centroids1 = permute(centroids,[3 2 1]);
  
  %%// Perform the much-needed subtraction 
  %t1 = bsxfun(@minus, X, centroids)

  %%// Perform element-wise squaring and then min-finding as required too
 % t2 = t1.^2
  %t3 = sum(t2,2)

  %%// Since the expansion resulted in data in third dimension, min-finding
  %%// must be along it
  %[mini_array, weights] = min(t3,[],3)
  
  % Compute the weights based on the distances from the nearest vertex
  %for ex=1:size(X,1)
  %  distances = zeros(1, K);

  %  for k=1:centroidsComputed
  %    distances(k) = (sum((X(ex,:) - centroids(k,:)).^2));
  %  end%
  
  %  [m weights(ex)] = min(distances);
  %end
  
  %weights = weights .^ 3;
  
  % Choose a random, weighted node based on the distances from the centroids.
  % Sum the weights of the nodes.
  %sumOfWeights = sum(weights);
  
  % Get a random number between 1 and the sum of the weights.
  %rnd = randi(sumOfWeights);
  
  % Iterate through the list of items, subtracting the weight from the random number 
  % Until a weight is greater than the remaining numbers.
  %for i=1:size(X,1)
  %  if rnd < weights(i)
  %    centroidsComputed = centroidsComputed + 1;
  %    centroids(centroidsComputed, :) = X(i, :);
  %    break
  %  end
  %  rnd = rnd - weights(i);
  %end
  
  % Choose the vertex furthest away from all current centroids and add it
  % as the newest centroid.
  [m im] = max(weights);
  centroidsComputed = centroidsComputed + 1;
  centroids(centroidsComputed, :) = X(im, :);
  printf("Centroid Calculated %d\n", centroidsComputed);
endwhile

% =============================================================

end

