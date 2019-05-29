// Joseph Seaton, Ivy Zhang, Neo Zhou, and Ben Chappell.

import java.util.Arrays;
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
    
    public static void main(String[] args) throws CloneNotSupportedException
    {
        //Create a new graph to manipulate.
        Graph inputGraph = new Graph();
        
        //Store the number of vertices in the graph to make arrays of vertex colors later.
        int vertexCount = inputGraph.getVertexCount();
        int edgeCount = inputGraph.edgeCount;
         
        //Create an array to store the best vertex color combination.
        int[] optimumVertexColors = new int[vertexCount];
        
        /* Eppley - Ivy Algorithm */
        
        /* ------------------------------------------------------------------------------------------------------------------------------------------------*/
        /* Djikstra's Algorithm but backwards AKA Ivy's Algorithm */
        int[][] nodeDegrees = new int[vertexCount][2];
        
        // Add the degrees to the array
        for (int i = 0; i < vertexCount; i++)
        {
            // {vertex, degree}
            int[] nodeStorage = {i, 0};
            for (int adjacentNodes = 0; adjacentNodes < inputGraph.adjacencyList[i].length; adjacentNodes++)
            {
                nodeStorage[1]++;
            }
            
            nodeDegrees[i] = nodeStorage;
        }
        
        // Bubble sort in order of the degrees- Just do this with a heap, you only need the max/min degree element one at a time
        // So no point in going through a whole sorting algorithm.
        int[] temp = {0, 0};
        int n = nodeDegrees.length;
        
        for(int i = 0; i < n; i++)
        {
            for(int j = 1; j < (n - i); j++)
            {
                 if(nodeDegrees[j-1][1] > nodeDegrees[j][1])
                 {
                      temp = nodeDegrees[j-1];  
                      nodeDegrees[j-1] = nodeDegrees[j];  
                      nodeDegrees[j] = temp;  
                 }
            }
        }   
        
        int[] vertexColors = new int[vertexCount];
        Arrays.fill(vertexColors, -1);
            
        //Iterate through every node in the adjacencyList.
        for (int counter = nodeDegrees.length - 1; counter >= 0; counter--)
        {
            //Create an arraylist to store vertex colors
            ArrayList<Integer> adjacentVertexColors = new ArrayList<Integer>();

            //Add adjacent node colors into the arraylist.
            for (int adjacentNode = 0; adjacentNode < inputGraph.adjacencyList[nodeDegrees[counter][0]].length; adjacentNode++)
            {
                adjacentVertexColors.add(vertexColors[inputGraph.adjacencyList[nodeDegrees[counter][0]][adjacentNode]]);
            }

            //Initialize the node's color to 0 because unprocessed nodes are originally -1.
            vertexColors[nodeDegrees[counter][0]] = 0;

            //If adjacent nodes have the same color as the current node, increment those colors by one.
            while (adjacentVertexColors.contains(vertexColors[nodeDegrees[counter][0]]))
            {
                vertexColors[nodeDegrees[counter][0]] = vertexColors[nodeDegrees[counter][0]] + 1;
            }
        }
        
        System.arraycopy(vertexColors, 0, optimumVertexColors, 0, vertexColors.length);
        
        /* -------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        // Weighted Limiting Randomness
        vertexColors = new int[vertexCount];
        ArrayList<Integer> adjacentVertexColors = new ArrayList<Integer>();
        ArrayList<Integer> storedNodes = new ArrayList<Integer>();
        ArrayList<Integer> storedNodesHypotheticalColors = new ArrayList<Integer>();
        ArrayList<Integer> listOfVertices = new ArrayList<Integer>();
        int limitingColorCount = countDistinct(optimumVertexColors, optimumVertexColors.length) - 1;
        int[][] storedWeights = new int[vertexCount][2];
        double totalWeight = 0;
        
        // Store the nodes with their weights which are the degrees 
        for (int node = 0; node < vertexCount; node++)
        {
            int[] temporary = {node, 0};
            for (int adjacentNodes = 0; adjacentNodes < inputGraph.adjacencyList[node].length; adjacentNodes++)
            {
                temporary[1]++;
            }
            totalWeight += temporary[1];
            storedWeights[node] = temporary;
        }
        
        int[][] temporaryStoredWeights = new int[vertexCount][2];
        
        for(int timesToRun = 0; timesToRun < edgeCount/vertexCount; timesToRun++)
        {
            System.arraycopy(storedWeights, 0, temporaryStoredWeights, 0, storedWeights.length);
            
            for (int i = 0 ; i < temporaryStoredWeights.length; i++)
            {
                int randomIndex = -1;
                double random = Math.random() * totalWeight;

                for (int j = 0; j < temporaryStoredWeights.length; j++)
                {
                    if (temporaryStoredWeights[j][0] > -1)
                    {
                        random -= temporaryStoredWeights[j][1];
                        if (random <= 0.0d)
                        {
                            randomIndex = j;
                            System.out.println(j);
                            break;
                        }
                    }
                }
                
                //
                listOfVertices.add(temporaryStoredWeights[randomIndex][0]);
                    
                //Remove the index once it has been used.
                temporaryStoredWeights[randomIndex][0] = -1;
            }
                
            for (int j = 0; j < vertexCount; j++)
            {
                if (!storedNodes.isEmpty())
                {
                    for (int i = 0; i < storedNodes.size(); i++)
                    {
                        int selectedElement = storedNodes.get(i);

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
                                break;
                            }
                        }
                    }
                }
                
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
                    
                    if (vertexColors[selectedElement] > limitingColorCount - 1 && adjacentVertexColors.contains(0))
                    {
                        vertexColors[selectedElement] = 0;
                        storedNodes.add(selectedElement);
                        break;
                    }
                } 
            }

            if (countDistinct(vertexColors, vertexColors.length) <= limitingColorCount)
            {
                //This array copy is good for large datasets.
                System.arraycopy(vertexColors, 0, optimumVertexColors, 0, vertexColors.length);
                System.out.println("Better");
                limitingColorCount--;
            }
        }
   
        
                      
        
        
        /* ----------------------------------------------------------------------------------------------------------------------------------------------------------------------*/
        // Limiting number of colors
    
    
        //Add integers that correspond to the nodes of a graph to an arraylist.
        listOfVertices = new ArrayList<Integer>();
    
        for (int i = 0; i < vertexCount; i++)
        {
            listOfVertices.add(i);
        }
        
        limitingColorCount = countDistinct(optimumVertexColors, optimumVertexColors.length) - 1;
        
        for(int timesToRun = 0; timesToRun < edgeCount/vertexCount; timesToRun++)
        {
            for (int node = 0; node < inputGraph.adjacencyList.length; node++)
            {
                vertexColors = new int[vertexCount];
                adjacentVertexColors = new ArrayList<Integer>();
                storedNodes = new ArrayList<Integer>();
                storedNodesHypotheticalColors = new ArrayList<Integer>();
                
                Collections.shuffle(listOfVertices);

                //Go through every vertex randomly.
                for (int j = 0; j < vertexCount; j++)
                {
                    if (!storedNodes.isEmpty())
                    {
                        for (int i = 0; i < storedNodes.size(); i++)
                        {
                            int selectedElement = storedNodes.get(i);

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
                                    break;
                                }
                            }
                        }
                    }
                    
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
                        
                        if (vertexColors[selectedElement] > limitingColorCount - 1 && adjacentVertexColors.contains(0))
                        {
                            vertexColors[selectedElement] = 0;
                            storedNodes.add(selectedElement);
                            break;
                        }
                    } 
                }

                if (countDistinct(vertexColors, vertexColors.length) <= limitingColorCount)
                {
                    //This array copy is good for large datasets.
                    System.arraycopy(vertexColors, 0, optimumVertexColors, 0, vertexColors.length);
                    System.out.println("Better");
                    limitingColorCount--;
                }
            }
        }
        
        /* ------------------------------------------------------------------------------------------------------------------------------------------------*/
        

        
        if (colorTest(optimumVertexColors, inputGraph))
            System.out.println("Success!");
            
        System.out.println(countDistinct(optimumVertexColors, optimumVertexColors.length));
        
        for(int color: optimumVertexColors)
            System.out.print(color + " ");                     
    }
}
