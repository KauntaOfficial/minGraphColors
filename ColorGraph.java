// Benjamin Chappell

import java.util.*;
import java.io.*;

public class ColorGraph
{
    // Takes two arguments - The results from the clustering and the graph file.
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

        
    }
}