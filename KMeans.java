// By Ivy Zhang and Co.

import java.lang.Math;
import org.jblas.*;
import java.util.*;
import java.io.*;

// Need to make sure that the jblas package is installed.
// Compile with statement javac -cp '.:jblas-1.2.4.jar' kMeans.java
// Run with statement java -cp '.:jblas-1.2.4.jar' kMeans file.txt

public class KMeans
{
    public int K;
    public int maxIters;
    private Graph inputGraph;
    private boolean[][] adjMatrix;
    public int vertexCount;
    private File inputFile;
    public DoubleMatrix X;
    private DoubleMatrix initialCentroids;
    private DoubleMatrix initialIdx;
    private DoubleMatrix finalIdx; 

    // Second constructor without default values. Allows explicit definition of K and MaxIters.
    public KMeans(String file, int K_, int maxIters_) throws FileNotFoundException
    {
        inputFile = new File(file);
        inputGraph = new Graph(inputFile);

        adjMatrix = inputGraph.adjacencyMatrix;
        vertexCount = inputGraph.getVertexCount();

        //K is the amount of centroids we want.
        K = K_;

        // max iters the the maximum iterations of the algorithm.
        maxIters = maxIters_;

        //Create X from the adj matrix
        X = formDataMatrix(adjMatrix);

        // Get the initial centroids.
        initialCentroids = initCentroids(X, K);

        // Get the initial idx values.
        initialIdx = findClosestCentroids(X, initialCentroids, K);
    }

    //Default constructor, still requires a file.
    public KMeans(String file) throws FileNotFoundException
    {
        // Initialize the simple stuff
        inputFile = new File(file);
        inputGraph = new Graph(inputFile);
        adjMatrix = inputGraph.adjacencyMatrix;
        vertexCount = inputGraph.getVertexCount();

        //K is the amount of centroids we want.
        K = (int)Math.sqrt(vertexCount) * 2;

        // max iters the the maximum iterations of the algorithm.
        maxIters = vertexCount / 5;

        //Create X from the adj matrix
        X = formDataMatrix(adjMatrix);

        // Get the initial centroids.
        initialCentroids = initCentroids(X, K);

        // Get the initial idx values.
        initialIdx = findClosestCentroids(X, initialCentroids, K);
    }

    // Works as long as data is in the same format as it would be after makin the adj matrix.
    public KMeans(DoubleMatrix data)
    {
        // Initialize simple stuff
        X = data;
        vertexCount = X.getRows();

        // K is the amount of centroids we want.
        K = (int)Math.sqrt(vertexCount) * 2;

        maxIters = vertexCount / 5;

        // Initialize centroids
        initialCentroids = initCentroids(X, K);

        // Get the initial idx values.
        initialIdx = findClosestCentroids(X, initialCentroids, K);
    }

    private int randomInt(int min, int max)
    {
        //int range = (max  min) + 1;
        return (int)(Math.random() * max) + min;
    }
    
    //Shell for runKMeans
    public DoubleMatrix runkMeans()
    {
        return runkMeans(X, initialCentroids, maxIters, K);
    }

    /*RUNKMEANS runs the K-Means algorithm on data matrix X, where each row of X
    is a single example
    [centroids, idx] = RUNKMEANS(X, initial_centroids, max_iters, ...
    plot_progress) runs the K-Means algorithm on data matrix X, where each 
    row of X is a single example. It uses initial_centroids used as the
    initial centroids. max_iters specifies the total number of interactions 
    of K-Means to execute. plot_progress is a true/false flag that 
    indicates if the function should also plot its progress as the 
    learning happens. This is set to false by default. runkMeans returns 
    centroids, a Kxn matrix of the computed centroids and idx, a m x 1  
    vector of centroid assignments (i.e. each entry in range [1..K]) */
    private DoubleMatrix runkMeans(DoubleMatrix X, DoubleMatrix initialCentroids, int maxIters, int K)
    {
        // Initialize Values
        int m = X.getRows();
        int n = X.getColumns();
        DoubleMatrix centroids = initialCentroids;
        DoubleMatrix prevCentroids = centroids;
        DoubleMatrix idx = DoubleMatrix.zeros(m, 1);

        // Run K-Means
        for (int i = 0; i < maxIters; i++)
        {
            // For each example in X, assign it to the closest centroid.
            idx = findClosestCentroids(X, centroids, K);

            //Given the memberships, compute new centriods.
            centroids = computeCentroids(X, idx, K);

            // System.out.println("Iteration " + (i + 1) + "/" + maxIters + ".");
        }

        finalIdx = idx; //This should work?
        return idx;
    }

    /* COMPUTECENTROIDS returns the new centroids by computing the means of the 
    data points assigned to each centroid.
    centroids = COMPUTECENTROIDS(X, idx, K) returns the new centroids by 
    computing the means of the data points assigned to each centroid. It is
    given a dataset X where each row is a single data point, a vector
    idx of centroid assignments (i.e. each entry in range [1..K]) for each
    example, and K, the number of centroids. You should return a matrix
    centroids, where each row of centroids is the mean of the data points
    assigned to it. */
    private DoubleMatrix computeCentroids(DoubleMatrix X, DoubleMatrix idx, int K)
    {
        // Doesn't really matter which one is m and n, bc X is square, aka don't try to 
        // Generalize this because m and n might not be right and I don't care to check.
        int m = X.getRows();
        int n = X.getColumns();
        DoubleMatrix centroids = DoubleMatrix.zeros(K, n);

        // k is a double for easy comparison with stuff
        for (double k = 0; k < K; k++)
        {
            // Follow what's done in computeCentroids.m. I'm far too dead to do it right now.
            DoubleMatrix inThisCluster = idx.eq(k);
            DoubleMatrix pointsInThisCluster = inThisCluster.columnSums();
            DoubleMatrix inThisClusterMatrix = DoubleMatrix.zeros(m, n);

            for (int i = 0; i < n; i++)
            {
                inThisClusterMatrix.putColumn(i, inThisCluster);
            }

            DoubleMatrix XofThoseInMatrix = X.mul(inThisClusterMatrix);
            centroids.putRow((int)k, (XofThoseInMatrix.columnSums().div(pointsInThisCluster)));
        }

        return centroids;
    }

    // Need better variable names for z and zy
    private DoubleMatrix findClosestCentroids(DoubleMatrix X, DoubleMatrix initCentroids, int K)
    {
        DoubleMatrix idx = DoubleMatrix.zeros(X.rows, 1);

        for (int i = 0; i < X.rows; i++)
        {
            // Compute the distance between the row of X and each of the centroids.
            DoubleMatrix z = initCentroids.subRowVector(X.getRow(i));
                
            DoubleMatrix zy = DoubleMatrix.zeros(K);
            // Iterate through each of the rows of z. Sums the element-wise distances from 
            // each centroid, computing overall distance.
            for (int k = 0; k < K; k++)
            {
                // Iterate through the columns.
                for (int j = 0; j < z.columns; j++)
                {
                    zy.put(k, zy.get(k) + Math.pow(z.get(k, j), 2));
                }
            }

            // Choose the weight to be the distance from the centroid closest to the
            // Current vertex
            idx.put(i, zy.argmin());
        }

        return idx;
    }

    // Same thing as init centroids, but the next centroids are chosen using a weighted random selection, rather than the one farthest away.
    private DoubleMatrix weightedInitCentroids(DoubleMatrix X, int K)
    {
        // Get a random row to start as the first centroid.
        int randidx = (int)randomInt(0, X.rows);
        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        // Initialize the very first row of the centroids.
        centroids.putRow(0, X.getRow(randidx));
        //Start this 0 for convenience's sake.
        int centroidsComputed = 1;

        while (centroidsComputed < K)
        {
            DoubleMatrix weights = DoubleMatrix.zeros(X.columns, 1);

            for (int i = 0; i < X.rows; i++)
            {
                // Compute the distance between the row of X and each of the centroids.
                DoubleMatrix z = centroids.subRowVector(X.getRow(i));
                
                DoubleMatrix zy = DoubleMatrix.zeros(K);
                // Iterate through each of the rows of z. Sums the element-wise distances from 
                // each centroid, computing overall distance.
                for (int k = 0; k < K; k++)
                {
                    // Iterate through the columns.
                    for (int j = 0; j < z.columns; j++)
                    {
                        zy.put(k, zy.get(k) + Math.pow(z.get(k, j), 2));
                    }
                }

                // Choose the weight to be the distance from the centroid closest to the
                // Current vertex
                weights.put(i, zy.min());
            }

            for (int i = 0; i < weights.length; i++)
            {
                weights.put(i, Math.pow(weights.get(i), 3));
            }

            // This is where this method differs from the other initializtion method.
            // Sum the weights, then choose a random number between 0 and the sum of the weights.
            double weightSum = weights.sum();
            int rnd = randomInt(0, (int)weightSum);
            int newCentroid = 0;

            // Subtract consecutive weights from the random number until one of them is bigger than the random number.
            for (int i = 0; weights.get(i) < rnd; i++)
            {
                rnd -= weights.get(i); //Might be able to make this more efficient by eliminating the redundant weights.get call.
                newCentroid = i; //Check to make sure this method works with Neo or Ivy.
            }

            centroidsComputed++;
            centroids.putRow(centroidsComputed - 1, X.getRow(newCentroid));
            // System.out.println("Centroid Calculated " + (centroidsComputed));
        }
        
        return centroids;
    }

    // Works perfectly fine afaik+
    private DoubleMatrix initCentroids(DoubleMatrix X, int K)
    {
        //return weightedInitCentroids(X, K);
        // Get a random row to start as the first centroid.
        int randidx = (int)randomInt(0, X.rows);
        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        // Initialize the very first row of the centroids.
        centroids.putRow(0, X.getRow(randidx));
        //Start this 0 for convenience's sake.
        int centroidsComputed = 1;

        while (centroidsComputed < K)
        {
            DoubleMatrix weights = DoubleMatrix.zeros(X.columns, 1);

            for (int i = 0; i < X.rows; i++)
            {
                // Compute the distance between the row of X and each of the centroids.
                DoubleMatrix z = centroids.subRowVector(X.getRow(i));
                
                DoubleMatrix zy = DoubleMatrix.zeros(K);
                // Iterate through each of the rows of z. Sums the element-wise distances from 
                // each centroid, computing overall distance.
                for (int k = 0; k < K; k++)
                {
                    // Iterate through the columns.
                    for (int j = 0; j < z.columns; j++)
                    {
                        zy.put(k, zy.get(k) + Math.pow(z.get(k, j), 2));
                    }
                }

                // Choose the weight to be the distance from the centroid closest to the
                // Current vertex
                weights.put(i, zy.min());
            }

            // Choose the vertex furthest away from all the current centroids and add
            // it as the newest centroid.
            double maxDist = weights.argmax();


            centroidsComputed++;
            centroids.putRow(centroidsComputed - 1, X.getRow((int)maxDist));
            //System.out.println("Centroid Calculated " + (centroidsComputed));
        }
        
        return centroids;  
    }

    private DoubleMatrix formDataMatrix(boolean[][] adjMatrix)
    {   
        // Initialize the length and the new adj matrix.
        int len = adjMatrix.length;
        DoubleMatrix newAdjMatrix = DoubleMatrix.zeros(len, len);

        // Create the new adjacency matrix from ones and zeros.
        for (int i = 0; i < len; i++)
        {
            for (int j = 0; j < adjMatrix[i].length; j++)
            {
                if (adjMatrix[i][j])
                    newAdjMatrix.put(i, j, 1.0);
            }
        }

        return newAdjMatrix;
    }
    
    public DoubleMatrix countClusters()
    {
        DoubleMatrix counts = DoubleMatrix.zeros(K);
        // Gets the amount 
        for (int i = 0; i < finalIdx.length; i++)
        {
            int currentCluster = (int)finalIdx.get(i);
            counts.put(currentCluster, counts.get(currentCluster) + 1);
        }

        return counts;
    }

    public DoubleMatrix clustersGreaterThanAverage()
    {
        DoubleMatrix counts = countClusters();
        int averageValue = vertexCount / K;

        DoubleMatrix greaterThan = DoubleMatrix.zeros(K);
        for (int i = 0; i < counts.length; i++)
        {
            if (counts.get(i) > (double)averageValue)
                greaterThan.put(i, 1);
        }

        return greaterThan;
    }
}
