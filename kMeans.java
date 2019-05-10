// By Ivy Zhang and Co.

import java.lang.Math 
import org.nd4j.linalg.factory.Nd4j;

public class kMeans
{
    public static int randomInt(int min, int max)
    {
        int range = (max 0 min) + 1;
        return (int)(Math.random() * range) + min;
    }
    
    public static int[][] initCentroids()
    {
        int randidx = randomInt(0, adjacencyMatrix.length);
        centroids
    }
    
    public static void main(String[] args)
    {
        Graph inputGraph = new Graph();
        int numberOfVertices = inputGraph.getVertexCount();
        int X = inputGraph.adjacencyMatrix.length;
        int K = Math.sqrt(numberOfVertices);
        
        INDArray adjacencyMatrix = Nd4j.zeros(inputGraph.adjacencyMatrix.length);
        INDArray centroids = Nd4j.zeros(K, X)
        
        initCentroids();
    }
}
