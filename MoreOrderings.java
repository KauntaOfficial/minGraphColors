import java.util.PriorityQueue;
import java.util.*;
// Benjamin Chappell
// This is only for writing ordering programs until I get my code from at home.

public class MoreOrderings
{
    public static void main(String[] args)
    {
        // i dont think i need that.
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
            for (int j = 0; j < graph.degreeArray.length; j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = j;
                toOffer[1] = graph.degreeArray[j];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
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
            for (int j = 0; j < graph.degreeArray.length; j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = j;
                toOffer[1] = graph.degreeArray[j];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
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
            for (int j = 0; j < graph.degreeArray.length; j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = j;
                toOffer[1] = graph.degreeArray[j];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
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
            for (int j = 0; j < graph.degreeArray.length; j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = j;
                toOffer[1] = graph.degreeArray[j];
                clusterDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.peek()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
    }
}