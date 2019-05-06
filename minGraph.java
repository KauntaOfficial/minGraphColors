// Ivy Zhang, Joe, Neo, and Ben

import java.util.ArrayList;
import java.lang.Math;

public class minGraph
{
    public static int randomInt(int min, int max) 
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;  
    }
    
    //Determine if the graph passes the color test.
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
    
    public static void main(String[] args)
    {
        //Create a new graph to manipulate.
        Graph inputGraph = new Graph();
        
        //Create an array to store the color for each corresponding vertex.
        int vertexCount = inputGraph.getVertexCount();
        int[] vertexColors = new int[vertexCount];
        
        //Iterate through every node in the adjacencyList.
        for (int i = 0; i < inputGraph.adjacencyList.length; i++)
        {
            //Create an arraylist to store vertex colors
            ArrayList<Integer> adjacentVertexColors = new ArrayList<Integer>();
            
            //Initialize adjacent nodes into the arraylist.
            for (int j = 0; j < inputGraph.adjacencyList[i].length; j++)
            {
                adjacentVertexColors.add(vertexColors[j]);
            }
            
            //Initialize the current node's color to 0.
            vertexColors[i] = 0;
            
            //If adjacent nodes have the same color as the current node, increment those               //colors by one.
            while (adjacentVertexColors.contains(vertexColors[i]))
            {
                vertexColors[i] = vertexColors[i] + 1;
            }
        }
        
        
        
        /*for (int colorChoices = vertexCount - 1; colorChoices > 0; colorChoices--)
        {
            for (int attempt = 0; attempt <= 100000; attempt++)
            {
                for (int node = 0; node < vertexColors.length; node++)
                {
                    vertexColors[node] = randomInt(0, colorChoices);
                }
                
                if (colorTest(vertexColors, inputGraph))
                {
                    minColorChoices = colorChoices + 1;
                    System.arraycopy(vertexColors, 0, minColors, 0, vertexColors.length);
                }
            }
        }*/
        
        inputGraph.colorTest
        
        System.out.println("There were " + countDistinct(vertexColors, vertexColors.length) + " colors");
        for(int color: vertexColors)
            System.out.print(color + " ");
            
           
    }
}
