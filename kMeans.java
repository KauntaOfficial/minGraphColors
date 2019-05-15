// By Ivy Zhang and Co.

import java.lang.Math;
import org.jblas.*;

// Need to make sure that the jblas package is installed.
// Compile with statement javac -cp '.:jblas-1.2.4.jar' kMeans.java

public class kMeans
{
    public static int randomInt(int min, int max)
    {
        //int range = (max  min) + 1;
        return (int)(Math.random() * max) + min;
    }
    
    public static DoubleMatrix initCentroids(DoubleMatrix X, int K)
    {
        // Get a random row to start as the first centroid.
        int randidx = (int)randomInt(0, X.rows);
        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        // Initialize the very first row of the centroids.
        centroids.putRow(0, X.getRow(randidx));
        //Start this 0 for convenience's sake.
        int centroidsComputed = 0;

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
            double maxDist = weights.max();
            centroidsComputed++;
            centroids.putRow(centroidsComputed, X.getRow((int)maxDist));
            System.out.println("Centroid Calculated " + (centroidsComputed + 1));
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
            for (int j = 0; j < adjMatrix[i].length; i++)
            {
                if (adjMatrix[i][j])
                    newAdjMatrix.put(i, j, 1.0);
            }
        }

        return newAdjMatrix;
    }
    
    public static void main(String[] args)
    {
        Graph inputGraph = new Graph();
        boolean[][] adjMatrix = inputGraph.adjacencyMatrix;
        int numberOfVertices = inputGraph.getVertexCount();
        int K = (int)Math.sqrt(numberOfVertices);
        
        DoubleMatrix X = formAdjMatrix(adjMatrix);
        DoubleMatrix centroids = initCentroids(X, K);
        System.out.println("Finished Initializing Centroids");
    }
}
