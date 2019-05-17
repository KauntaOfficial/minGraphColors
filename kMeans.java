// By Ivy Zhang and Co.

import java.lang.Math;
import org.jblas.*;
import java.util.*;
import java.io.*;

// Need to make sure that the jblas package is installed.
// Compile with statement javac -cp '.:jblas-1.2.4.jar' kMeans.java
// Run with statement java -cp '.:jblas-1.2.4.jar' kMeans file.txt

public class kMeans
{
    public static int randomInt(int min, int max)
    {
        //int range = (max  min) + 1;
        return (int)(Math.random() * max) + min;
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
    public static DoubleMatrix runkMeans(DoubleMatrix X, DoubleMatrix initialCentroids, int maxIters, int K)
    {
        // Initialize Values
        int m = X.rows;
        int n = X.columns;
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

            System.out.println("Iteration " + (i + 1) + "/" + maxIters + ".");
        }

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
    public static DoubleMatrix computeCentroids(DoubleMatrix X, DoubleMatrix idx, int K)
    {
        // Doesn't really matter which one is m and n, bc X is square, aka don't try to 
        // Generalize this because m and n might not be right and I don't care to check.
        int m = X.rows;
        int n = X.columns;
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
    public static DoubleMatrix findClosestCentroids(DoubleMatrix X, DoubleMatrix initCentroids, int K)
    {
        DoubleMatrix idx = DoubleMatrix.zeros(X.columns, 1);

        for (int i = 0; i < X.columns; i++)
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


    // Works perfectly fine afaik+
    public static DoubleMatrix initCentroids(DoubleMatrix X, int K)
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

            for (int i = 0; i < X.columns; i++)
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
            System.out.println("Centroid Calculated " + (centroidsComputed));
        }
        
        return centroids;
    }

    public static DoubleMatrix formAdjMatrix(boolean[][] adjMatrix)
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
    
    public static void main(String[] args) throws FileNotFoundException
    {
        File file = new File(args[0]);
        Graph inputGraph = new Graph(file);
        boolean[][] adjMatrix = inputGraph.adjacencyMatrix;
        int numberOfVertices = inputGraph.getVertexCount();
        
        // Select an intial Set of centroids
        // K is the amount of centroids we want - sqrt fo the amount of vertices right now.
        int K = 4;
        // The maximum iterations of the kMeans clustering, set at this value for safety.
        int maxIters = 20;
        // X represents the data that we are clustering, the adj matrix in this case.
        DoubleMatrix X = formAdjMatrix(adjMatrix);
        DoubleMatrix initialCentroids = initCentroids(X, K);
        System.out.println("Finished Initializing Centroids");

        for (int i = 0; i < K; i++)
        {
            System.out.println(initialCentroids.getRow(i));
        }

        // Find the closest centroids for each example using the initial centriods.
        DoubleMatrix idx = findClosestCentroids(X, initialCentroids, K);

        // Run K-Means Algorithm
        idx = runkMeans(X, initialCentroids, maxIters, K);

        for (int i = 0; i < idx.length; i++)
        {
            System.out.print(idx.get(i) + " ");
        }
        System.out.println();
    }
}
