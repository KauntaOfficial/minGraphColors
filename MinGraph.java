// By Co. and Ivy Zhang

import java.util.ArrayList;
import java.lang.Math;
import java.util.Collections;

public class MinGraph
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
    
    // What does this do again? Why are we here? Just to suffer? T^T
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
        int edgeCount = inputGraph.edgeCount;
         
        //Create an array to store the best vertex color combination.
        int[] optimumVertexColors = new int[vertexCount];
        
        /*Linear Runthroughs*/
        
        //Choose different nodes to start on.
        for (int startingNode = 0; startingNode < inputGraph.adjacencyList.length; startingNode++)
        {
            //Create an array to temporarily store the color for each corresponding vertex.
            int[] vertexColors = new int[vertexCount];
            
            //Iterate through every node in the adjacencyList.
            for (int counter = 0; counter < inputGraph.adjacencyList.length; counter++)
            {
                //Loop back to the first node if you're on an index larger than the number of vertices.
                int i = (counter + startingNode) % vertexCount;
                
                //Create an arraylist to store vertex colors
                ArrayList<Integer> adjacentVertexColors = new ArrayList<Integer>();

                //Add adjacent node colors into the arraylist.
                for (int adjacentNode = 0; adjacentNode < inputGraph.adjacencyList[i].length; adjacentNode++)
                {
                    adjacentVertexColors.add(vertexColors[inputGraph.adjacencyList[i][adjacentNode]]);
                }

                //Initialize every element's color to 0.
                vertexColors[i] = 0;

                //If adjacent nodes have the same color as the current node, increment those colors by one.
                while (adjacentVertexColors.contains(vertexColors[i]))
                {
                    vertexColors[i] = vertexColors[i] + 1;
                }     
            }
            
            if (startingNode == 0 || countDistinct(vertexColors, vertexColors.length) < countDistinct(optimumVertexColors, optimumVertexColors.length))
            {
                //This array copy is good for large datasets.
                System.arraycopy(vertexColors, 0, optimumVertexColors, 0, vertexColors.length);
            }
        }
        
        /* Random thingy ma bob runthroughs */
        for (int timesToRun = 0; timesToRun < edgeCount/500; timesToRun++)
        {
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
        
        int limitingColorCount = countDistinct(optimumVertexColors, optimumVertexColors.length);
        
        for(int timesToRun = 0; timesToRun < edgeCount/500; timesToRun++)
        {
            for (int node = 0; node < inputGraph.adjacencyList.length; node++)
            {
                int[] vertexColors = new int[vertexCount];
                ArrayList<Integer> adjacentVertexColors = new ArrayList<Integer>();
                ArrayList<Integer> storedNodes = new ArrayList<Integer>();
                boolean lastCheckNodeStored = false;
                
                //Add integers that correspond to the nodes of a graph to an arraylist.
                for (int i = 0; i < vertexCount; i++)
                {
                    listOfVertices.add(i);
                }

                Collections.shuffle(listOfVertices);

                //Go through every vertex randomly.
                for (int j = 0; j < vertexCount; j++)
                {
                    if 
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
                        
                        if (vertexColors[selectedElement] > limitingColorCount)
                        {
                            vertexColors[selectedElement] = 0;
                            storedNodes.add(selectedElement);
                            lastCheckNodeStored = true;
                            break;
                        }
                    } 
                }

                if (countDistinct(vertexColors, vertexColors.length) < countDistinct(optimumVertexColors, optimumVertexColors.length))
                {
                    //This array copy is good for large datasets.
                    System.arraycopy(vertexColors, 0, optimumVertexColors, 0, vertexColors.length);
                }
            }
        }
        
        //Display results.
        if(colorTest(optimumVertexColors, inputGraph))
            System.out.println("Success!");
        
        System.out.println("There were " + countDistinct(optimumVertexColors, optimumVertexColors.length) + " colors");
        for(int color: optimumVertexColors)
            System.out.print(color + " ");                     
    }
}
