//Paul Eppley

import java.util.*;
import java.io.*;

public class ColorTest
{
    private static int colorCount(int[] colors)
    {
        //Bubble sort the array.
        for(int i = 0; i < colors.length - 1; i++)
            for(int j = i + 1; j < colors.length; j++)
                if(colors[i] > colors[j])
                {
                    int temp = colors[i];
                    colors[i] = colors[j];
                    colors[j] = temp;
                }

        //Now, count the number of unique colors.
        int count = 1;

        for(int i = 1; i < colors.length; i++)
            if(colors[i] != colors[i - 1])
                count++;

        return count;
    }

    public static void main(String[] args) throws FileNotFoundException
    {
        if(args.length != 2)
            System.out.println("Usage:  java ColorTest graphFile colorFile");

        //Read in the graph.
        File graphFile = new File(args[0]);
        Graph g = new Graph(graphFile);

        int[] colorList = new int[g.getVertexCount()];

        //Read in the colors.
        File colorFile = new File(args[1]);
        Scanner colorInput = new Scanner(colorFile);

        for(int i = 0; i < colorList.length; i++)
            colorList[i] = colorInput.nextInt();

        //Check that every pair of adjacent vertices have different colors.
        for(int u = 0; u < g.getVertexCount(); u++)
            for(int v = 0; v < g.getVertexCount(); v++)
                if(u != v && g.areAdjacent(u, v) && colorList[u] == colorList[v])
                {
                    System.out.println("Vertices " + u + " and " + v + " are adjacent and have color " + colorList[u] + ".");
                    return;
                }

        System.out.println("Valid coloring with " + colorCount(colorList) + " colors.");        
    }
}