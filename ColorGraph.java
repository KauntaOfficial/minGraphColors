// Benjamin Chappell

import java.util.*;
import java.io.*;

public class ColorGraph
{
    // Takes two arguments - The results from the clustering and the graph file.
    // Run with java ColorGraph clusterFile graphFile
    public static void main(String[] args) throws FileNotFoundException
    {
        File graphFile = new File(args[1]);
        File clusterFile = new File(args[0]);

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

        // Run the colorings from here, as there will be multiple ones while we test for the best one.
        int[] colors = cLtoSiLinear(clusterCount, graph, clusters, clusterSizes);

        if (colorTest(colors, graph))
        {
            System.out.println("Success!");
        }

        System.out.println(countDistinct(colors, colors.length));
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
    }+

    // Takes the clusters in order from largest to smallest, and the inner nodes linearly.
    public static int[] cLtoSiLinear(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        // Colors start at 1, a color of 0 means that no color has been assigned yet.
        int[] colors = new int[graph.vertexCount];
        int colorsUsed = 0;

        // Heap method doesn't work bc thsi is hella gay

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

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = clusters.get(currentCluster).get(j);

                // Index 0 will be foo, if index 0 is true, there are adjacent vertices that do not have colors assigned to them yet.
                boolean[] adjColors = new boolean[colorsUsed + 1];
                
                // Place a true in the index of a color that is used by all adjacent vertices.
                for (int k = 0; k < graph.adjacencyList[currentVertex].length; k++)
                {
                    adjColors[colors[graph.adjacencyList[currentVertex][k]]] = true; 
                    System.out.println(colors[graph.adjacencyList[currentVertex][k]]);
                }

                // Find the first available color for the current vertex.
                int usableColor = 1;
                while (usableColor <= colorsUsed && adjColors[usableColor]);
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
        }

        return colors;
    }
}