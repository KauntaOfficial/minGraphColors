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
   
    public static int[] colorGraph(ArrayList<Integer> colorOrdering, int vc, Graph inputGraph)
    {
        int[] vertexColors = new int[vc];
        
        for (int i = 0; i < colorOrdering.size(); i++) 
        {
            int selectedElement = colorOrdering.get(i);

            ArrayList<Integer> adjacentVertexColors = new ArrayList<Integer>();

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
        
        return vertexColors;
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
        int[][][] allDegreeLists = new int[vertexCount][vertexCount][2];
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
        
        // Sort the numbers into groups by their degrees, Step 1
        int currentDegree = -1;
        int currentIndex = -1;
        ArrayList<ArrayList<Integer>> nodesStoredByDegrees = new ArrayList<ArrayList<Integer>>();
        
        for (int i = 0; i < nodeDegrees.length; i++)
        {   
            // If the degree count of the vertex is diferent from the current, move onto the next index to store it in
            if(nodeDegrees[i][1] != currentDegree)
            {
                currentDegree = nodeDegrees[i][1];
                currentIndex++;
                nodesStoredByDegrees.add(new ArrayList<Integer>());
            }
            nodesStoredByDegrees.get(currentIndex).add(nodeDegrees[i][0]);
        }   
        
        // randomly shuffle the groups to make new orderings multiple times
        for (int timesToRun = 0; timesToRun < 10; timesToRun++)
        {
            ArrayList<Integer> runOrdering = new ArrayList<Integer>();
            for (int i = 0; i < nodesStoredByDegrees.size(); i++)
            {    
                Collections.shuffle(nodesStoredByDegrees.get(i));
                runOrdering.addAll(nodesStoredByDegrees.get(i));
            }
            int[] runVertexColors = colorGraph(runOrdering, vertexCount, inputGraph);
            if (countDistinct(runVertexColors, runVertexColors.length) < countDistinct(optimumVertexColors, optimumVertexColors.length))
            {
                System.arraycopy(runVertexColors, 0, optimumVertexColors, 0, runVertexColors.length);
            }
        }
        
        if (colorTest(optimumVertexColors, inputGraph))
            System.out.println("Success!");
            
        System.out.println(countDistinct(optimumVertexColors, optimumVertexColors.length));
        
        for(int color: optimumVertexColors)
            System.out.print(color + " ");                     
    }
}
