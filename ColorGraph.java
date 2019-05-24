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

        int[] clusterSizes = new int[clusterCount];
        while(input.hasNextLine())
        {
            Scanner line = new Scanner(input.nextLine());
            while(line.hasNextInt())
            {
                int v = line.nextInt();
            }
        }
    }
}