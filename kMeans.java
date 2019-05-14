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
    
    public static int[][] initCentroids()
    {
        int randidx = randomInt(0, adjacencyMatrix.length);
        
    }
    
    public static void main(String[] args)
    {
        Graph inputGraph = new Graph();
        int numberOfVertices = inputGraph.getVertexCount();
        int X = inputGraph.adjacencyMatrix.length;
        int K = Math.sqrt(numberOfVertices);
        
        //INDArray adjacencyMatrix = Nd4j.zeros(inputGraph.adjacencyMatrix.length);
        //INDArray centroids = Nd4j.zeros(K, X);
        
        initCentroids();
    }
}
