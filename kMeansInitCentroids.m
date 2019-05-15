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
    z = bsxfun(@minus, centroids, X(i, :))
    zy = sum(z.^2, 2);
    [weights(i), ~] = min(zy);
  end 
  
  % Choose the vertex furthest away from all current centroids and add it
  % as the newest centroid.
  [m im] = max(weights);
  centroidsComputed = centroidsComputed + 1;
  centroids(centroidsComputed, :) = X(im, :);
  printf("Centroid Calculated %d\n", centroidsComputed);
endwhile

% =============================================================

end

