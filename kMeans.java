// By Ivy Zhang and Co.

import org.jblas.*;

// Need to make sure that the jblas package is installed.

public class kMeans
{
    public static int randomInt(int min, int max)
    {
        //int range = (max  min) + 1;
        return (int)(Math.random() * range) + min;
    }
    
    public static DoubleMatrix initCentroids(DoubleMatrix X, int K)
    {
        int randidx = randomInt(0, adjacencyMatrix.length);
        
    }

    public static DoubleMatrix formAdjMatrix(boolean[][] adjMatrix)
    {   
        // Initialize the length and the new adj matrix.
        int len = adjMatrix.length;
        DoubleMatrix newAdjMatrix = new DoubleMatrix.zeros(len, len);

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
        int K = Math.sqrt(numberOfVertices);
        
        DoubleMatrix X = formAdjMatrix(adjMatrix);
        DoubleMatrix centroids = initCentroids();
        
        initCentroids();
    }
}
