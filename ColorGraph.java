// Benjamin Chappell

import java.lang.Math;
import org.jblas.*;
import java.util.*;
import javax.lang.model.util.ElementScanner6;
import java.io.*;

// Need to make sure that the jblas package is installed.
// Compile with statement javac -cp '.:jblas-1.2.4.jar' kMeans.java
// Run with statement java -cp '.:jblas-1.2.4.jar' kMeans file.txt

public class ColorGraph
{
    public static void main(String[] args) throws FileNotFoundException
    {
        String file = args[0];

        KMeans colorGraph = new KMeans(file);

        DoubleMatrix idx = colorGraph.runkMeans();
        int clusterCount = colorGraph.K;

        int averageClusterSize = colorGraph.K / colorGraph.maxIters;

        DoubleMatrix greaterThanAverage = colorGraph.clustersGreaterThanAverage();
        // // // System.out.println(greaterThanAverage);
        int greaterThanAverageCount = (int)greaterThanAverage.sum();
        // // System.out.println(greaterThanAverageCount);

        // Create a list of datasets to act as the new data to create more, smaller clusters.
        DoubleMatrix[] dataSets = new DoubleMatrix[colorGraph.K];
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
                identities[idenTracker] = recluster(averageClusterSize, dataSets[i]).dup(); //Something wrong with the second half of this, with the gettin gof the stuff from greater than average.
        }
        // Now that it's reclutered once, i have to do it recursively until all of them are below the average.
        // Actually, reassimilate first, and then recluster. Should be easier.

        //////// WORKS UP TO HERE //////////

        DoubleMatrix newIdx = assimilate(identities, idx.length, greaterThanAverage, clusterCount, idx).dup();

        for (int i = 0; i < newIdx.length; i++)
        {
            System.out.println(newIdx.get(i));
        }
    }

    public static DoubleMatrix recluster(int avg, DoubleMatrix data)
    {
        KMeans coloring = new KMeans(data);
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

                    idx.put(i, mainIdx.get(i) + clusterCount + subNumber);
                }
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
            groupLists[i] = DoubleMatrix.zeros(1);
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

    // Unneeded.
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
}