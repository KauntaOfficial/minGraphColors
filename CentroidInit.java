// Benjamin Chappell

import org.jblas.*;
import java.util.*;

// Compile with statement javac -cp '.:jblas-1.2.4.jar' CentroidInit.java
// Run with statement java -cp '.:jblas-1.2.4.jar' CentroidInit - however, this is a class, so do not run.

public class CentroidInit
{
    public DoubleMatrix X;
    public int K;
    public Graph graph;
    public DoubleMatrix centroids;
    public int initType;
    public int power;

    // Init type designates the type of initialization we want for this.
    public CentroidInit(Graph graph_, int K_, DoubleMatrix X_, int initType_, int power_)
    {
        graph = graph_;
        K = K_;
        X = X_;
        initType = initType_;
        power = power_;

        switch (initType)
        {
            case 0: 
                centroids = initCentroids();
                break;
            case 1:
                centroids = weightedInitCentroids();
                break;
            case 2:
                centroids = highestDegreeStart();
                break;
            case 3:
                centroids = lowestDegreeStart();
                break;
            case 4:
                centroids = weightedLowestDegreeStart();
                break;
            case 5:
                centroids = weightedHighestDegreeStart();
                break;
            case 6:
                centroids = largestDegrees();
                break;
            case 7:
                centroids = smallestDegrees();
                break;
        }
    }

    private int randomInt(int min, int max)
    {
        //int range = (max  min) + 1;
        return (int)(Math.random() * max) + min;
    }

    // Basic Initialization - easiest one to do. Chooses the vertex furthest away from all of the centroids to be the next one.
    private DoubleMatrix initCentroids()
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
    private DoubleMatrix weightedInitCentroids()
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
                weights.put(i, Math.pow(weights.get(i), power));
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

    private DoubleMatrix lowestDegreeStart()
    {
        // Find the vertex with the lowest degree.
        int minDegree = 0;
        for (int i = 0; i < graph.degreeArray.length; i++)
        {
            if (graph.degreeArray[i] > minDegree)
            {
                minDegree = graph.degreeArray[i];
            }
        }

        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        // Initialize the very first row of the centroids.
        centroids.putRow(0, X.getRow(minDegree));
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

    private DoubleMatrix highestDegreeStart()
    {
        // Find the vertex with the highest degree.
        int maxDegree = 0;
        for (int i = 0; i < graph.degreeArray.length; i++)
        {
            if (graph.degreeArray[i] > maxDegree)
            {
                maxDegree = graph.degreeArray[i];
            }
        }

        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        // Initialize the very first row of the centroids.
        centroids.putRow(0, X.getRow(maxDegree));
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

    // First centroid is the one with the lowest degree. Chooses next centroid by combination of distance and degree - multiplication
    private DoubleMatrix weightedLowestDegreeStart()
    {
        // Find the vertex with the lowest degree.
        int minDegree = 0;
        for (int i = 0; i < graph.degreeArray.length; i++)
        {
            if (graph.degreeArray[i] > minDegree)
            {
                minDegree = graph.degreeArray[i];
            }
        }

        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        // Initialize the very first row of the centroids.
        centroids.putRow(0, X.getRow(minDegree));
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
                // put back into weights - (distance * degree) ^ power (normally 3)
                weights.put(i, Math.pow((weights.get(i) * graph.degreeArray[i]), power));
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

    // First centroid is the one with the highest degree. Chooses next centroid by combination of distance and degree - multiplication
    private DoubleMatrix weightedHighestDegreeStart()
    {
        // Find the vertex with the highest degree.
        int maxDegree = 0;
        for (int i = 0; i < graph.degreeArray.length; i++)
        {
            if (graph.degreeArray[i] > maxDegree)
            {
                maxDegree = graph.degreeArray[i];
            }
        }

        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        // Initialize the very first row of the centroids.
        centroids.putRow(0, X.getRow(maxDegree));
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
                // put back into weights - (distance * degree) ^ power (normally 3)
                weights.put(i, Math.pow((weights.get(i) * graph.degreeArray[i]), power));
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

    // Take the vertices with the K largest degrees and use them as the initial centroids. - very very fast compared to other initializations.
    private DoubleMatrix largestDegrees()
    {
        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        PriorityQueue<Integer[]> degreeAccess = new PriorityQueue<>((Integer[] x, Integer[] y) -> y[1] - x[1]);
        for (int i = 0; i < graph.degreeArray.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = graph.degreeArray[i];
            degreeAccess.offer(toOffer);
        }

        int centroidsComputed = 0;

        while (centroidsComputed < K)
        {
            int nextVertex = degreeAccess.poll()[0];
            centroids.putRow(centroidsComputed, X.getRow(nextVertex));
            centroidsComputed++;
        }

        return centroids;
    }

    // Takes K vertices with the smallest degrees and uses them.
    private DoubleMatrix smallestDegrees()
    {
        DoubleMatrix centroids = new DoubleMatrix(K, X.columns);

        PriorityQueue<Integer[]> degreeAccess = new PriorityQueue<>((Integer[] x, Integer[] y) -> x[1] - y[1]);
        for (int i = 0; i < graph.degreeArray.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = graph.degreeArray[i];
            degreeAccess.offer(toOffer);
        }

        int centroidsComputed = 0;

        while (centroidsComputed < K)
        {
            int nextVertex = degreeAccess.poll()[0];
            centroids.putRow(centroidsComputed, X.getRow(nextVertex));
            centroidsComputed++;
        }

        return centroids;
    }
}

