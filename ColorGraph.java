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
        int greaterThanAverageCount = (int)greaterThanAverage.sum();

        // Create a list of datasets to act as the new data to create more, smaller clusters.
        DoubleMatrix[] dataSets = new DoubleMatrix[colorGraph.K];

        // Get all of the data sets for all of the clusters, putting them into data sets.
        // Due to the way the data is stored, the lesser data points will be first, meaning that if vertex 2 is the first one in
        // cluter 5, dataSet[5].get(0) will be the data for vertex 2 (since the clusters start at 0). This is why going through the list in 
        // alphabetical order is very important, otherwise the data will be messed up.
        // As a result, remember to add a method to recreate a singular idx.
        for (int i = 0; i < idx.length; i++)
        {
            dataSets[(int)idx.get(i)].putRow(dataSets[(int)idx.get(i)].rows, colorGraph.X.getRow(i));
        }

        //Create a list to store each of the resultant idxs, for later assimilation
        DoubleMatrix[] identities = new DoubleMatrix[greaterThanAverageCount];

        for (int i = 0; i < greaterThanAverage.length; i++)
        {
            identities[i] = recluster(averageClusterSize, dataSets[(int)greaterThanAverage.get(i)]);
        }
        // Now that it's reclutered once, i have to do it recursively until all of them are below the average.
        // Actually, reassimilate first, and then recluster. Should be easier.
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
                DoubleMatrix[] reclusteredGroupList = convertToGroupBasedLists(identities[idenTracker], identities[idenTracker].max() + 1);

                // Go through each of the vertices in the group, and set their group to the sum of their current group, the active cluster count, 
                // and the subcluster.
                // Main Cluster is located at idx[vertex number]
                // Active cluster count is cluster count
                // Subcluster number located at the group list that has the vertex in it.
                int subNumber;
                for (int subGroup = 0; subGroup < reclusteredGroupList.length; subGroup++)
                {
                    if (bSearch(reclusteredGroupList[subGroup], reclusteredGroupList[subGroup].length, target))
                    {
                        
                    }
                }
            }
        }
    }

    public static DoubleMatrix[] convertToGroupBasedLists(DoubleMatrix idx, int K)
    {
        DoubleMatrix[] groupLists = new DoubleMatrix[K];

        for (int i = 0; i < idx.length; i++)
        {
            groupLists[idx.get(i)].put(groupLists[idx.get(i)].length, i);
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