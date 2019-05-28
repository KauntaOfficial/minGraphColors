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

        // This gets the colors determined by clusters smallest to largest and vertices linear within.
        int[] cllisOrder = cLtoSiLinear(clusterCount, graph, clusters, clusterSizes);
        int[] cllisColors = color(cllisOrder, graph);
        int cllisColorCount = determineSuccessAndCountDistict(cllisColors, graph, cllisColors.length);
        System.out.println("Colors found by clusters s to l vertices linear is " + cllisColorCount);

        // Purely linear order straight through the clusters in order, straight through the clusters in order.
        int[] linearTcOrder = linearThroughClusters(clusterCount, graph, clusters, clusterSizes);
        int[] linearTcColors = color(linearTcOrder, graph);
        int linearTcColorCount = determineSuccessAndCountDistict(linearTcColors, graph, linearTcColors.length);
        System.out.println("Colors found by linear through clusters and vertices is " + linearTcColorCount); 

        // purely linear order from vertex 0 to n.
        int[] linearOrder = pureLinear(graph);
        int[] linearColors = color(linearOrder, graph);
        int linearColorCount = determineSuccessAndCountDistict(linearColors, graph, linearColors.length);
        System.out.println("Colors found by linear is " + linearColorCount);

        // Degree from largest to smallest.
        int[] degreeLtoSOrder = degreeLtoS(graph);
        int[] degreeLtoSColors = color(degreeLtoSOrder, graph);
        int degreeLtoSCount = determineSuccessAndCountDistict(degreeLtoSColors, graph, degreeLtoSColors.length);
        System.out.println("Colors found by degree from largest to smallest is " + degreeLtoSCount);

        // Degree from smallest to largest.
        int[] degreeStoLOrder = degreeStoL(graph);
        int[] degreeStoLColors = color(degreeStoLOrder, graph);
        int degreeStoLCount = determineSuccessAndCountDistict(degreeStoLColors, graph, degreeStoLColors.length);
        System.out.println("Colors found by degree from smallest to largest is " + degreeStoLCount); 

        // Cluster Degree from largest to smallest.
        int[] cDegreeLS = degreeLtoS(graph);
        int[] cDegreeLSColors = color(cDegreeLS, graph);
        int cDegreeLSCount = determineSuccessAndCountDistict(cDegreeLSColors, graph, cDegreeLSColors.length);
        System.out.println("Colors found by cluster degree from largest to smallest is " + cDegreeLSCount); 

        // Cluster Degree from smallest to largest.
        int[] cDegreeSL = degreeStoL(graph);
        int[] cDegreeSLColors = color(cDegreeSL, graph);
        int cDegreeSLCount = determineSuccessAndCountDistict(cDegreeSLColors, graph, cDegreeSLColors.length);
        System.out.println("Colors found by cluster degree from smallest to largest is " + cDegreeSLCount); 

        // Cluster Degree from largest to smallest, vertex degree from largest to smallest
        int[] cDegreeLSvDegreeLS = cDegreesLSvDegreesLS(clusterCount, graph, clusters, clusterSizes);
        int[] cDegreeLSvDegreeLSColors = color(cDegreeLSvDegreeLS, graph);
        int cDegreeLSvDegreeLSCount = determineSuccessAndCountDistict(cDegreeLSvDegreeLSColors, graph, cDegreeLSvDegreeLSColors.length);
        System.out.println("Colors found by Cluster Degree from largest to smallest, vertex degree from largest to smallest is " + cDegreeLSvDegreeLSCount);

        // Cluster Degree from smallest to largest, vertex degree from smallest to largest.
        int[] cDegreeSLvDegreeSL = cDegreesSLvDegreesSL(clusterCount, graph, clusters, clusterSizes);
        int[] cDegreeSLvDegreeSLColors = color(cDegreeSLvDegreeSL, graph);
        int cDegreeSLvDegreeSLCount = determineSuccessAndCountDistict(cDegreeSLvDegreeSLColors, graph, cDegreeSLvDegreeSLColors.length);
        System.out.println("Colors found by Cluster Degree from smallest to largest, vertex degree from smallest to largest is " + cDegreeSLvDegreeSLCount);

        // Cluster Degree from largest to smallest, vertex degree from smallest to largest.
        int[] cDegreeLSvDegreeSL = cDegreesLSvDegreesSL(clusterCount, graph, clusters, clusterSizes);
        int[] cDegreeLSvDegreeSLColors = color(cDegreeLSvDegreeSL, graph);
        int cDegreeLSvDegreeSLCount = determineSuccessAndCountDistict(cDegreeLSvDegreeSLColors, graph, cDegreeLSvDegreeSLColors.length);
        System.out.println("Colors found by Cluster Degree from largest to smallest, vertex degree from smallest to largest is " + cDegreeLSvDegreeSLCount);

        // Cluster Degree from smallest to largest, vertex degree from largest to smallest.
        int[] cDegreeSLvDegreeLS = cDegreesSLvDegreesLS(clusterCount, graph, clusters, clusterSizes);
        int[] cDegreeSLvDegreeLSColors = color(cDegreeSLvDegreeLS, graph);
        int cDegreeSLvDegreeLSCount = determineSuccessAndCountDistict(cDegreeSLvDegreeLSColors, graph, cDegreeSLvDegreeLSColors.length);
        System.out.println("Colors found by Cluster Degree from largest to smallest, vertex degree from largest to smallest is " + cDegreeSLvDegreeLSCount);
    }

    public static int[] cDegreesLSvDegreesLS(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        int[] clusterDegrees = getClusterDegrees(clusterCount, graph, clusters, clusterSizes);
        int[] order = new int[graph.vertexCount];
        int orderTracker = 0;

        PriorityQueue<Integer[]> clusterDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> x[1] - y[1]);
        for (int i = 0; i < clusterDegrees.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = clusterDegrees[i];
            clusterDegreeAccess.offer(toOffer);
        }

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = clusterDegreeAccess.peek()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> x[1] - y[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
        return order;
    }

    public static int[] cDegreesSLvDegreesLS(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        int[] clusterDegrees = getClusterDegrees(clusterCount, graph, clusters, clusterSizes);
        int[] order = new int[graph.vertexCount];
        int orderTracker = 0;

        PriorityQueue<Integer[]> clusterDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> y[1] - x[1]);
        for (int i = 0; i < clusterDegrees.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = clusterDegrees[i];
            clusterDegreeAccess.offer(toOffer);
        }

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = clusterDegreeAccess.peek()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> x[1] - y[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
        return order;
    }

    public static int[] cDegreesSLvDegreesSL(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        int[] clusterDegrees = getClusterDegrees(clusterCount, graph, clusters, clusterSizes);
        int[] order = new int[graph.vertexCount];
        int orderTracker = 0;

        PriorityQueue<Integer[]> clusterDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> y[1] - x[1]);
        for (int i = 0; i < clusterDegrees.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = clusterDegrees[i];
            clusterDegreeAccess.offer(toOffer);
        }

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = clusterDegreeAccess.peek()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> y[1] - x[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
        return order;
    }

    public static int[] cDegreesLSvDegreesSL(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        int[] clusterDegrees = getClusterDegrees(clusterCount, graph, clusters, clusterSizes);
        int[] order = new int[graph.vertexCount];
        int orderTracker = 0;

        PriorityQueue<Integer[]> clusterDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> x[1] - y[1]);
        for (int i = 0; i < clusterDegrees.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = clusterDegrees[i];
            clusterDegreeAccess.offer(toOffer);
        }

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = clusterDegreeAccess.peek()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> y[1] - x[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
        return order;
    }

    public static int[] pureLinear(Graph graph)
    {
        int[] order = new int[graph.vertexCount];

        for (int i = 0; i < graph.vertexCount; i++)
        {
            order[i] = i;
        }

        return order;
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

    public static int[] clusterDegressLtoS(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        int[] order = new int[graph.vertexCount];
        int[] clusterDegrees = getClusterDegrees(clusterCount, graph, clusters, clusterSizes);
        int placeInOderTracker = 0;

        PriorityQueue<Integer[]> clusterDegreeAccess = new PriorityQueue<Integer[]>((Integer[] x, Integer[] y) -> y[1] - x[1]);
        for (int i = 0; i < clusterDegrees.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = clusterDegrees[i];
            clusterDegreeAccess.offer(toOffer);
        }

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = clusterDegreeAccess.peek()[0];

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = clusters.get(currentCluster).get(j);

                order[placeInOderTracker] = currentVertex;
                placeInOderTracker++;
            }
        }

        return order;
    }

    public static int[] clusterDegressStoL(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        int[] order = new int[graph.vertexCount];
        int[] clusterDegrees = getClusterDegrees(clusterCount, graph, clusters, clusterSizes);
        int placeInOderTracker = 0;

        PriorityQueue<Integer[]> clusterDegreeAccess = new PriorityQueue<Integer[]>((Integer[] x, Integer[] y) -> x[1] - y[1]);
        for (int i = 0; i < clusterDegrees.length; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = clusterDegrees[i];
            clusterDegreeAccess.offer(toOffer);
        }

        for (int i = 0; i < clusterCount; i++)
        {
            int currentCluster = clusterDegreeAccess.peek()[0];

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = clusters.get(currentCluster).get(j);

                order[placeInOderTracker] = currentVertex;
                placeInOderTracker++;
            }
        }

        return order;
    }

    public static int[] degreeLtoS(Graph graph)
    {
        int[] order = new int[graph.vertexCount];

        PriorityQueue<Integer[]> degreeAccess = new PriorityQueue<Integer[]>((Integer[] x, Integer[] y) -> y[1] - x[1]);
        for (int i = 0; i < graph.vertexCount; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = graph.degreeArray[i];
            degreeAccess.offer(toOffer);
        }

        for (int i = 0; i < graph.vertexCount; i++)
        {
            int currentVertex = degreeAccess.poll()[0];
            order[i] = currentVertex;
        }

        return order;
    }

    public static int[] degreeStoL(Graph graph)
    {
        int[] order = new int[graph.vertexCount];

        PriorityQueue<Integer[]> degreeAccess = new PriorityQueue<Integer[]>((Integer[] x, Integer[] y) -> x[1] - y[1]);
        for (int i = 0; i < graph.vertexCount; i++)
        {
            Integer[] toOffer = new Integer[2];
            toOffer[0] = i;
            toOffer[1] = graph.degreeArray[i];
            degreeAccess.offer(toOffer);
        }

        for (int i = 0; i < graph.vertexCount; i++)
        {
            int currentVertex = degreeAccess.poll()[0];
            order[i] = currentVertex;
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

    public static int[] cStoLiLinear(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        // Colors start at 1, a color of 0 means that no color has been assigned yet.
        int[] order = new int[graph.vertexCount];
        int placeInOderTracker = 0;

        // Create a priority Queue to store the cluster sizes. This is a min Heap. Uses a lambda function so that we can keep track of the 
        // Other part of the information using the first part of the tuple.
        PriorityQueue<Integer[]> clusterSizeAccess = new PriorityQueue<Integer[]>((Integer[] x, Integer[] y) -> x[1] - y[1]);
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
            
            // A node can never have a color of 0, so this is in place to prevent that.
            adjColors[0] = true;
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
    
    public static int[] getClusterDegrees(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        return new int[1];
    }
}