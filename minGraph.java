// By Co. and Ivy Zhang

import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;

public class minGraph
{
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
    
    public static int randomInt(int min, int max) 
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;  
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
    
    public static void main(String[] args)
    {        
        //Create a new graph to manipulate.
        Graph inputGraph = new Graph();
        
        //Store the number of vertices in the graph to make arrays of vertex colors later.
        int vertexCount = inputGraph.getVertexCount();
        
        //Create an array to store the best vertex color combination.
        int[] optimumVertexColors = new int[vertexCount];            
                    
        for (int timesToRun = 0; timesToRun < 10; timesToRun++)
        {   
            long start = System.nanoTime() / 1000000;     

            /* Random thingy ma bob runthroughs */
            for (int runNumber = 0; runNumber < timesToRun; runNumber++)
            {
                optimumVertexColors = new int[vertexCount];
                
                for (int node = 0; node < inputGraph.adjacencyList.length; node++)
                {
                    ArrayList<Integer> listOfVertices = new ArrayList<Integer>();
                    int[] vertexColors = new int[vertexCount];

                    ArrayList<Integer> adjacentVertexColors = new ArrayList<Integer>();

                    //Add integers that correspond to the nodes of a graph to an arraylist.
                    for (int i = 0; i < vertexCount; i++)
                    {
                        listOfVertices.add(i);
                    }

                    Collections.shuffle(listOfVertices);

                    //Go through every vertex randomly.
                    for (int j = 0; j < vertexCount; j++)
                    {
                        int selectedElement = listOfVertices.get(j);

                        adjacentVertexColors = new ArrayList<Integer>();

                        //Adding adjacent node colors to arraylist.
                        for (int adjacentNode = 0; adjacentNode < inputGraph.adjacencyList[selectedElement].length; adjacentNode++)
                        {
                            adjacentVertexColors.add(vertexColors[inputGraph.adjacencyList[selectedElement][adjacentNode]]);
                        }

                        //Initialize every element's color to 0.
                        vertexColors[selectedElement] = 0;

                        //If adjacent nodes have the same color as the current node, increment those colors by one.
                        while (adjacentVertexColors.contains(vertexColors[selectedElement]))
                        {
                              vertexColors[selectedElement] = vertexColors[selectedElement] + 1;
                        } 
                    }

                    if (countDistinct(vertexColors, vertexColors.length) < countDistinct(optimumVertexColors, optimumVertexColors.length))
                    {
                        //This array copy is good for large datasets.
                        System.arraycopy(vertexColors, 0, optimumVertexColors, 0, vertexColors.length);
                    }
                }
            }

            long end = System.nanoTime() / 1000000;

            //Print the number of times the random algorithm was run, the runtime, and the number of colors
            System.out.println((timesToRun + 1) + "," + (end - start) + "," + countDistinct(optimumVertexColors, optimumVertexColors.length));
        }                    
    }
}
