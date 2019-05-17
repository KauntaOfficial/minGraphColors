// Benjamin Chappell

import java.lang.Math;
import org.jblas.*;
import java.util.*;
import java.io.*;

// Need to make sure that the jblas package is installed.
// Compile with statement javac -cp '.:jblas-1.2.4.jar' kMeans.java
// Run with statement java -cp '.:jblas-1.2.4.jar' kMeans file.txt

public class ColorGraph
{
    public static void main(String[] args) throws FileNotFoundException
    {
        String file = args[0];

        KMeans graphToColor = new KMeans(file);

        DoubleMatrix idx = graphToColor.runkMeans();

        DoubleMatrix clusterCounts = graphToColor.countClusters();
        System.out.println((clusterCounts));
    }
}