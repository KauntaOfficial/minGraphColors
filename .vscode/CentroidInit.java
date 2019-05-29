// Benjamin Chappell

import org.jblas.*;
import java.util.*;

// Compile with statement javac -cp '.:jblas-1.2.4.jar' CentroidInit.java
// Run with statement java -cp '.:jblas-1.2.4.jar' CentroidInit - however, this is a class, so do not run.

public class CentroidInit
{
    public DoubleMatrix X;
    public int clusterCount;
    public Graph graph;
    public DoubleMatrix centroids;

    // Init type designates the type of initialization we want for this.
    public CentroidInit(Graph graph_, int clusterCount_, DoubleMatrix X_, int initType)
    {
        graph = graph_;
        clusterCount = clusterCount_;
        X = X_;

        switch (initType)
        {
            case 0: 
                initCentroids(X, clusterCount);
                break;
            case 1:
                
                break;
        }
    }

    // Basic Initialization - easiest one to do.
    private DoubleMatrix initCentroids(DoubleMatrix X, int K, boolean weighted)
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

            // Choose the vertex furthest away from all the current centroids and add
            // it as the newest centroid.
            double maxDist = weights.argmax();


            centroidsComputed++;
            centroids.putRow(centroidsComputed - 1, X.getRow((int)maxDist));
            //System.out.println("Centroid Calculated " + (centroidsComputed));
        }
        
        return centroids;  
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
}