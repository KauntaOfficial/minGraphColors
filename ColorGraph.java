// Benjamin Chappell

import java.util.*;
import java.io.*;

public class ColorGraph
{
    // Takes two arguments - The results from the clustering and the graph file.
    // Run with java ColorGraph clusterFile graphFile
    public static void main(String[] args) throws FileNotFoundException
    {
        //For Debugging
        //String graphf = "2dsurface.txt";
        //String clusterf = "clusterResults.txt";

        File graphFile = new File(args[1]);
        File clusterFile = new File(args[0]);

        //File graphFile = new File(graphf);
        //File clusterFile = new File(clusterf);

        Graph graph = new Graph(graphFile);

        Scanner input = new Scanner(clusterFile);
        
        // First line holds the amount of clusters.
        int clusterCount = input.nextInt();
        input.nextLine();

        ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>(clusterCount);
        for (int i = 0; i < clusterCount; i++)
        {
            clusters.add(new ArrayList<Integer>());
        }

        int[] clusterSizes = new int[clusterCount];

        int clusterTracker = 0;
        while (input.hasNextLine())
        {
            Scanner line = new Scanner(input.nextLine());

            while (line.hasNextInt())
            {
                clusters.get(clusterTracker).add(line.nextInt());
                clusterSizes[clusterTracker]++;
            }
            clusterTracker++;
            line.close();
        }
        input.close();

        // This gets the colors determined by clusters largest to smallest and vertices linear within.
        int[] clsilOrder = cLtoSiLinear(clusterCount, graph, clusters, clusterSizes);
        int[] clsilColors = color(clsilOrder, graph);
        int clsilColorCount = determineSuccessAndCountDistict(clsilColors, graph, clsilColors.length);
        System.out.println("Colors found by clusters l to s vertices linear is " + clsilColorCount);

        // Purely linear order straight through the clusters in order, straight through the clusters in order.
        int[] linearTcOrder = linearThroughClusters(clusterCount, graph, clusters, clusterSizes);
        int[] linearTcColors = color(linearTcOrder, graph);
        int linearTcColorCount = determineSuccessAndCountDistict(linearTcColors, graph, linearTcColors.length);
        System.out.println("Colors found by linear through clusters and vertices is " + linearTcColorCount);
    }

    public static int determineSuccessAndCountDistict(int[] colors, Graph graph, int n)
    {
        if (colorTest(colors, graph))
        {
            System.out.println("Success!");
        }

        return countDistinct(colors, n);
    }

    public static int[] color(int[] order, Graph graph)
    {
        // Colors start at 1, a color of 0 means that no color has been assigned yet.
        int[] colors = new int[order.length];
        int colorsUsed = 0;

        for (int i = 0; i < order.length; i++)
        {
            int currentVertex = order[i];

            // Index 0 will be foo, if index 0 is true, there are adjacent vertices that do not have colors assigned to them yet.
            boolean[] adjColors = new boolean[colorsUsed + 1];
            
            // Place a true in the index of a color that is used by all adjacent vertices.
            for (int j = 0; j < graph.adjacencyList[currentVertex].length; j++)
            {
                adjColors[colors[graph.adjacencyList[currentVertex][j]]] = true;
            }
            
            // Find the first available color for the current vertex.
            int usableColor = 0;
            while((usableColor <= colorsUsed) && adjColors[usableColor])
            {
                usableColor++;
            } 

            // Increment the amount of colors used, if applicable.
            if (usableColor > colorsUsed)
            {
                colorsUsed = usableColor; // Should be the same as colorsUsed++, as usable color only increments by one at a time.
            }

            // Set the color of the current vertex to that of the first usable color.
            colors[currentVertex] = usableColor;
        }

        return colors;
    }

    public static boolean colorTest(int[] colors, Graph g)
    {
        for(int u = 0; u < g.adjacencyList.length; u++)
            for(int v = 0; v < g.adjacencyList[u].length; v++)
                if(colors[u] == colors[g.adjacencyList[u][v]] && u != v)
                {
                    return false;
                }

        return true;
    }

    static int countDistinct(int arr[], int n) 
    { 
        int res = 1; 
  
        // Pick all elements one by one 
        for (int i = 1; i < n; i++)  
        { 
            int j = 0; 
            
            for (j = 0; j < i; j++) 
                if (arr[i] == arr[j]) 
                    break; 

            // If not printed earlier,  
            // then print it 
            if (i == j) 
                res++; 
        } 
        
        return res;
    } 



    public static int[] linearThroughClusters(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        // Colors start at 1, a color of 0 means that no color has been assigned yet.
        int[] order = new int[graph.vertexCount];
        int placeInOderTracker = 0;

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = i;

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = clusters.get(currentCluster).get(j);

                order[placeInOderTracker] = currentVertex;
                placeInOderTracker++;
            }
        }

        return order;
    }

    // Takes the clusters in order from largest to smallest, and the inner nodes linearly.
    public static int[] cLtoSiLinear(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        // Colors start at 1, a color of 0 means that no color has been assigned yet.
        int[] order = new int[graph.vertexCount];
        int placeInOderTracker = 0;

        // Create a priority Queue to store the cluster sizes. This is a max Heap
        PriorityQueue<Integer[]> clusterSizeAccess = new PriorityQueue<Integer[]>((Integer[] x, Integer[] y) -> y[1] - x[1]);
        for (int i = 0; i < clusterCount; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = clusterSizes[i];
            clusterSizeAccess.offer(toOffer);
        }

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = clusterSizeAccess.poll()[0];
            //System.out.println(currentCluster);

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = clusters.get(currentCluster).get(j);

                order[placeInOderTracker] = currentVertex;
                placeInOderTracker++;
            }
        }

        return order;
    }
}