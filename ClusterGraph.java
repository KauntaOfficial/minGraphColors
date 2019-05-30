// Benjamin Chappell

import java.lang.Math;
import org.jblas.*;
import java.util.*;
import javax.lang.model.util.ElementScanner6;
import java.io.*;

// Need to make sure that the jblas package is installed.
// Compile with statement javac -cp '.:jblas-1.2.4.jar' ClusterGraph.java
// Run with statement java -cp '.:jblas-1.2.4.jar' ClusterGraph file.txt

public class ClusterGraph
{
    public static void main(String[] args) throws FileNotFoundException
    {
        String file = args[0];
        File fFile = new File(file);
        int initType = 0;
        int initLimit = 5;

        for (int i = 0; i < initLimit; i++)
        {
            doThings(file, fFile, initType);
            System.out.println();
            System.out.println("---------------------------------------------------------------------------------------------");
            System.out.println();
        } 
    }

    public static void doThings(String file, File fFile, int initType) throws FileNotFoundException
    {
        KMeans colorGraph = new KMeans(file, initType);
        Graph graph =  new Graph(fFile);

        DoubleMatrix idx = colorGraph.runkMeans();
        int clusterCount = colorGraph.K;

        int averageClusterSize = colorGraph.vertexCount / colorGraph.K;

        // Duplicated idx for easy use since I want to save the original idx at least for now.
        DoubleMatrix newIdx = idx.dup();
        int reclusterCap = colorGraph.K / 2;
        int newClusterCount = clusterCount;

        // Continually recluster until all of the clusters are smaller than the average size, aka the square root of the amount of vertices.
        
        // Create the new Identification matrix using the recluster and assimilate algorithm.
        for (int i = 0; i < reclusterCap; i++)
        {
            newIdx = reclusterAndAssimilate(colorGraph, newIdx, averageClusterSize, newClusterCount, initType);
            newClusterCount = (int)newIdx.max() + 1;
        }

        DoubleMatrix[] idxLists = convertToGroupBasedLists(idx, clusterCount + 1);
        DoubleMatrix[] newIdxLists = convertToGroupBasedLists(newIdx, newClusterCount + 1);

        int nonZeroClusterCount = 0;
        for (int i = 0; i < newIdxLists.length; i++)
        {
            if (newIdxLists[i].length > 0)
                nonZeroClusterCount++;
        }

        ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>(clusterCount);
        for (int i = 0; i < nonZeroClusterCount; i++)
        {
            clusters.add(new ArrayList<Integer>());
        }

        int[] clusterSizes = new int[nonZeroClusterCount];
        // Converts the new idx list into the arraylist we need for Coloring the graph.
        int clusterTracker = 0;
        for (int i = 0; i < newIdxLists.length; i++)
        {
            for (int j = 0; j < newIdxLists[i].length; j++)
            {
                int currentVertex = (int)newIdxLists[i].get(j);
                clusters.get(clusterTracker).add(currentVertex);
            }
            if (newIdxLists[i].length > 0)
            {

                clusterSizes[clusterTracker] = newIdxLists[i].length;
                clusterTracker++;
            }
        }

        /*
        for (int i = 0; i < newIdxLists.length; i++)
        {
            for (int j = 0; j < newIdxLists[i].length; j++)
            {
                System.out.print((int)newIdxLists[i].get(j) + " ");
            }
            if (newIdxLists[i].length > 0)
                System.out.println();
        } 
        System.out.println(); */
        
        // Get the degrees of each of the clusters.
        int[] clusterDegrees = getClusterDegrees(nonZeroClusterCount, graph, clusters, clusterSizes);

        ////// Run all of the potential orderings //////

        // This gets the colors determined by clusters largest to smallest and vertices linear within.
        int[] clsilOrder = cLtoSiLinear(nonZeroClusterCount, graph, clusters, clusterSizes);
        int[] clsilColors = color(clsilOrder, graph);
        int clsilColorCount = determineSuccessAndCountDistict(clsilColors, graph, clsilColors.length);
        System.out.println("Colors found by clusters l to s vertices linear is " + clsilColorCount);

        // This gets the colors determined by clusters smallest to largest and vertices linear within.
        int[] cllisOrder = cStoLiLinear(nonZeroClusterCount, graph, clusters, clusterSizes);
        int[] cllisColors = color(cllisOrder, graph);
        int cllisColorCount = determineSuccessAndCountDistict(cllisColors, graph, cllisColors.length);
        System.out.println("Colors found by clusters s to l vertices linear is " + cllisColorCount);

        // Purely linear order straight through the clusters in order, straight through the clusters in order.
        int[] linearTcOrder = linearThroughClusters(nonZeroClusterCount, graph, clusters, clusterSizes);
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

        // Cluster Degree from largest to smallest, vertex degree from largest to smallest
        int[] cDegreeLSvDegreeLS = cDegreesLSvDegreesLS(nonZeroClusterCount, graph, clusters, clusterSizes, clusterDegrees);
        int[] cDegreeLSvDegreeLSColors = color(cDegreeLSvDegreeLS, graph);
        int cDegreeLSvDegreeLSCount = determineSuccessAndCountDistict(cDegreeLSvDegreeLSColors, graph, cDegreeLSvDegreeLSColors.length);
        System.out.println("Colors found by Cluster Degree from largest to smallest, vertex degree from largest to smallest is " + cDegreeLSvDegreeLSCount);

        // Cluster Degree from smallest to largest, vertex degree from smallest to largest.
        int[] cDegreeSLvDegreeSL = cDegreesSLvDegreesSL(nonZeroClusterCount, graph, clusters, clusterSizes, clusterDegrees);
        int[] cDegreeSLvDegreeSLColors = color(cDegreeSLvDegreeSL, graph);
        int cDegreeSLvDegreeSLCount = determineSuccessAndCountDistict(cDegreeSLvDegreeSLColors, graph, cDegreeSLvDegreeSLColors.length);
        System.out.println("Colors found by Cluster Degree from smallest to largest, vertex degree from smallest to largest is " + cDegreeSLvDegreeSLCount);

        // Cluster Degree from largest to smallest, vertex degree from smallest to largest.
        int[] cDegreeLSvDegreeSL = cDegreesLSvDegreesSL(nonZeroClusterCount, graph, clusters, clusterSizes, clusterDegrees);
        int[] cDegreeLSvDegreeSLColors = color(cDegreeLSvDegreeSL, graph);
        int cDegreeLSvDegreeSLCount = determineSuccessAndCountDistict(cDegreeLSvDegreeSLColors, graph, cDegreeLSvDegreeSLColors.length);
        System.out.println("Colors found by Cluster Degree from largest to smallest, vertex degree from smallest to largest is " + cDegreeLSvDegreeSLCount);

        // Cluster Degree from smallest to largest, vertex degree from largest to smallest.
        int[] cDegreeSLvDegreeLS = cDegreesSLvDegreesLS(nonZeroClusterCount, graph, clusters, clusterSizes, clusterDegrees);
        int[] cDegreeSLvDegreeLSColors = color(cDegreeSLvDegreeLS, graph);
        int cDegreeSLvDegreeLSCount = determineSuccessAndCountDistict(cDegreeSLvDegreeLSColors, graph, cDegreeSLvDegreeLSColors.length);
        System.out.println("Colors found by Cluster Degree from largest to smallest, vertex degree from largest to smallest is " + cDegreeSLvDegreeLSCount);
    }

    public static DoubleMatrix reclusterAndAssimilate(KMeans colorGraph, DoubleMatrix idx, int averageClusterSize, int clusterCount, int initType) 
    {
        
        DoubleMatrix greaterThanAverage = clustersGreaterThanAverage(clusterCount, idx.length, idx);

        // // // System.out.println(greaterThanAverage);
        int greaterThanAverageCount = (int)greaterThanAverage.sum();
        // // System.out.println(greaterThanAverageCount);

        // Create a list of datasets to act as the new data to create more, smaller clusters.
        DoubleMatrix[] dataSets = new DoubleMatrix[clusterCount];
        for (int i = 0; i < dataSets.length; i++)
        {
            dataSets[i] = DoubleMatrix.zeros(1, colorGraph.X.columns);
        }

        // Get all of the data sets for all of the clusters, putting them into data sets.
        // Due to the way the data is stored, the lesser data points will be first, meaning that if vertex 2 is the first one in
        // cluter 5, dataSet[5].get(0) will be the data for vertex 2 (since the clusters start at 0). This is why going through the list in 
        // alphabetical order is very important, otherwise the data will be messed up.
        // As a result, remember to add a method to recreate a singular idx.

        // This for loop is working correctly.
        for (int i = 0; i < idx.length; i++)
        {
            //// // System.out.println("Dataset " + i + " found.");
            int groupAtLocation = (int)idx.get(i);
            // Create a temporary matrix for this.
            DoubleMatrix temp = dataSets[groupAtLocation].dup();
            // Resize the data sets matrix for this group.
            dataSets[groupAtLocation].resize(dataSets[groupAtLocation].rows + 1, dataSets[groupAtLocation].columns);

            for (int j = 0; j < temp.rows; j++)
            {
                dataSets[groupAtLocation].putRow(j, temp.getRow(j));
            }

            dataSets[groupAtLocation].putRow(dataSets[groupAtLocation].rows - 1, colorGraph.X.getRow(i));
        }

        //Create a list to store each of the resultant idxs, for later assimilation
        DoubleMatrix[] identities = new DoubleMatrix[greaterThanAverageCount];

        // Need to make it so that it only puts in the right ones into the right identities slots.
        int idenTracker = 0;
        for (int i = 0; i < greaterThanAverage.length; i++)
        {
            if (greaterThanAverage.get(i) == 1.0)
                identities[idenTracker] = recluster(averageClusterSize, dataSets[i], initType).dup();
        }
        // Now that it's reclutered once, i have to do it recursively until all of them are below the average.
        // Actually, reassimilate first, and then recluster. Should be easier.

        DoubleMatrix newIdx = assimilate(identities, idx.length, greaterThanAverage, clusterCount, idx).dup();

        return newIdx;
    }

    public static DoubleMatrix recluster(int avg, DoubleMatrix data, int initType)
    {
        KMeans coloring = new KMeans(data, initType);
        DoubleMatrix idx = coloring.runkMeans();
        return idx;
    }

    // Takes a list of idxs and turns it into just one idx.
    public static DoubleMatrix assimilate(DoubleMatrix[] identities, int vertexCount, DoubleMatrix greaterThanAverage, int clusterCount, DoubleMatrix mainIdx)
    {
        DoubleMatrix[] groupLists = convertToGroupBasedLists(mainIdx, clusterCount);
        DoubleMatrix idx = mainIdx.dup();
        int idenTracker = 0;

        for (int group = 0; group < greaterThanAverage.length; group++)
        {
            if (greaterThanAverage.get(group) == 1.0)
            {
                // Get the reclustered group list, using the identity tracker to find the right set of identities.
                DoubleMatrix[] reclusteredGroupList = convertToGroupBasedLists(identities[idenTracker], (int)identities[idenTracker].max() + 1);

                // Go through each of the vertices in the group, and set their new cluster to: the sum of their current group, the active cluster count, 
                // and the subcluster.
                // Main Cluster is located at idx[vertex number]
                // Active cluster count is cluster count
                // Subcluster number located at the group list that has the vertex in it.
                // Need to find the node I am looking for?
                for (int i = 0; i < groupLists[group].length; i++)
                {
                    int subNumber = 0;
                    for (int subGroup = 0; subGroup < reclusteredGroupList.length; subGroup++)
                    {
                        if (bSearch(reclusteredGroupList[subGroup], reclusteredGroupList[subGroup].length, groupLists[group].get(i)))
                        {
                            subNumber = subGroup;
                            break;
                        }
                    }
                    idx.put((int)groupLists[group].get(i), clusterCount + subNumber);
                }
                clusterCount += reclusteredGroupList.length;
            }
        }

        return idx;
    }

    // Like the loop that groups the data, but instead creates groups of 
    public static DoubleMatrix[] convertToGroupBasedLists(DoubleMatrix idx, int K)
    {
        // Create a list of the groups to store everything.
        DoubleMatrix[] groupLists = new DoubleMatrix[K];
        for (int i = 0; i < groupLists.length; i++)
        {
            groupLists[i] = DoubleMatrix.zeros(0);
        }

        for (int i = 0; i < idx.length; i++)
        {
            // Get the group this particular vertex is in.
            int groupAtLocation = (int)idx.get(i);
            // Create a temporary matrix for this.
            DoubleMatrix temp = groupLists[groupAtLocation].dup();
            // Resize the group matrix for this group.
            groupLists[groupAtLocation].resize(groupLists[groupAtLocation].rows + 1, 1);

            for (int j = 0; j < temp.rows; j++)
            {
                groupLists[groupAtLocation].put(j, temp.get(j));
            }

            groupLists[groupAtLocation].put(groupLists[groupAtLocation].rows - 1, i);
        }

        return groupLists;
    }

    // Simple binary search.
    public static boolean bSearch(DoubleMatrix toSearch, int n, double target)
    {
        int l = 0;
        int r = n - 1;

        while (l <= r)
        {
            int m = (l + r) / 2;

            if (toSearch.get(m) < target)
            {
                l = m + 1;
            }
            else if(toSearch.get(m) > target)
            {
                r = m - 1;
            }
            else
            {
                return true;
            }
        }

        return false;
    }

    public static DoubleMatrix clustersGreaterThanAverage(int K, int vertexCount, DoubleMatrix idx)
    {
        DoubleMatrix counts = countClusters(K, idx);
        int averageValue = vertexCount / K;

        DoubleMatrix greaterThan = DoubleMatrix.zeros(K);
        for (int i = 0; i < counts.length; i++)
        {
            if (counts.get(i) > (double)averageValue)
                greaterThan.put(i, 1);
        }

        return greaterThan;
    }

    public static DoubleMatrix countClusters(int K, DoubleMatrix idx)
    {
        DoubleMatrix counts = DoubleMatrix.zeros(K);
        // Gets the amount 
        for (int i = 0; i < idx.length; i++)
        {
            int currentCluster = (int)idx.get(i);
            counts.put(currentCluster, counts.get(currentCluster) + 1);
        }

        return counts;
    }


    public static int[] cDegreesLSvDegreesLS(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes, int[] clusterDegrees)
    {
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
            int currentCluster = clusterDegreeAccess.poll()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> y[1] - x[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                vertexDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.poll()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
        return order;
    }

    public static int[] cDegreesSLvDegreesLS(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes, int[] clusterDegrees)
    {
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
            int currentCluster = clusterDegreeAccess.poll()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> y[1] - x[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                vertexDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.poll()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
        return order;
    }

    public static int[] cDegreesSLvDegreesSL(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes, int[] clusterDegrees)
    {
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
            int currentCluster = clusterDegreeAccess.poll()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> x[1] - y[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                vertexDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.poll()[0];
                order[orderTracker] = currentVertex;
                orderTracker++;
            }
        }
        return order;
    }

    public static int[] cDegreesLSvDegreesSL(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes, int[] clusterDegrees)
    {
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
            int currentCluster = clusterDegreeAccess.poll()[0];

            PriorityQueue<Integer[]> vertexDegreeAccess = new PriorityQueue<Integer[]>((Integer x[], Integer y[]) -> x[1] - y[1]);
            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                Integer[] toOffer = new Integer[2];
                toOffer[0] = clusters.get(currentCluster).get(j);
                toOffer[1] = graph.degreeArray[clusters.get(currentCluster).get(j)];
                vertexDegreeAccess.offer(toOffer);
            }

            for (int j = 0; j < clusters.get(currentCluster).size(); j++)
            {
                int currentVertex = vertexDegreeAccess.poll()[0];
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

        System.out.println();

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
    
    // For some reason this method does not return the right degree for the first cluster in the current clustering of 2dsurface
    
    public static int[] getClusterDegrees(int clusterCount, Graph graph, ArrayList<ArrayList<Integer>> clusters, int[] clusterSizes)
    {
        int[] degrees = new int[clusterCount];

        for (int i = 0; i < clusterCount; i++)
        {
            boolean[] connected = new boolean[graph.vertexCount];
            int degreeCounter = 0;

            for (int j = 0; j < clusters.get(i).size(); j++)
            {
                int cVertex = clusters.get(i).get(j);
                for (int k = 0; k < graph.adjacencyList[cVertex].length; k++)
                {
                    if (!connected[graph.adjacencyList[cVertex][k]])
                    {
                        connected[graph.adjacencyList[cVertex][k]] = true;
                        degreeCounter++;
                    }
                }
            }

            // Remove any of the nodes in this cluster from the nodes it's adjacent to.
            for (int j = 0; j < clusters.get(i).size(); j++)
            {
                connected[clusters.get(i).get(j)] = false;
            }

            degrees[i] = degreeCounter;
        }

        return degrees;
    }
}