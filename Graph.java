//Paul Eppley

import java.util.*;
import java.io.*;

public class Graph
{
    //This class stores an undirected graph in both adjacency list and adjacency matrix formats.
    //It also stores an array of degrees of vertices, a vertex-count and an edge-count.
    //Note that if (u, v) is in the edge set, both (u, v) and (v, u) will appear in the adjacency
    //list.  Also, both adjacencyMatrix[u][v] and adjacencyMatrix[v][u] will be true.
    protected boolean[][] adjacencyMatrix;
    protected int[][] adjacencyList;
    protected int[] degreeArray;
    protected int vertexCount;
    protected int edgeCount;

    //This constructor reads in an undirected graph from the specified file.
    //Precondition:  The input is correctly formatted as follows.
    //    The first line contains an integer n representing the number of
    //    vertices in the graph.  Subsequent lines contain edges.
    //    Vertices are numbered 0 through n - 1.
    public Graph(File graphFile) throws FileNotFoundException
    {
        Scanner input = new Scanner(graphFile);
        
        //Read the number of vertices from the first line.
        vertexCount = input.nextInt();
        
        //Skip the rest of the first line.  (Anything else is garbage.)
        input.nextLine();
        
        //Allocate space for the adjacency list and matrix.
        adjacencyMatrix = new boolean[vertexCount][vertexCount];
        adjacencyList = new int[vertexCount][];
        
        //Allocate space for the degree array.
        degreeArray = new int[vertexCount];

        //Read in the remaining lines.
        while(input.hasNextLine())
        {
            Scanner line = new Scanner(input.nextLine());
            
            //Get the first integer on the line.  This represents the u in each edge (u, v).
            int u = line.nextInt();
            
            //Get the remaining integers on the line.  This represent each v in (u, v).
            while(line.hasNextInt())
            {
                int v = line.nextInt();
                
                //We now have the edge (u, v).  Process accordingly.
                
                //If the edge hasn't already been read in somewhere else...
                if(!adjacencyMatrix[u][v])
                {
                    degreeArray[u]++;
                    degreeArray[v]++;
                    adjacencyMatrix[u][v] = adjacencyMatrix[v][u] = true;
                    edgeCount++;
                }
            }
        }    

        //We now have everything except the adjacency list.  Create that now.
        for(int u = 0; u < vertexCount; u++)
        {
            adjacencyList[u] = new int[degreeArray[u]];
           
            int degreeCounter = 0;
            
            //March through row u of the adjacency matrix, inserting v into u's 
            //adjacency list, if (u, v) is in the edge set.
            for(int v = 0; v < vertexCount; v++)
                if(adjacencyMatrix[u][v])
                    adjacencyList[u][degreeCounter++] = v;
        }
    }

    //This constructor reads in an undirected graph from System.in.
    //Precondition:  The input is correctly formatted as follows.
    //    The first line contains an integer n representing the number of
    //    vertices in the graph.  Subsequent lines contain edges.
    //    Vertices are numbered 0 through n - 1.
    public Graph()
    {
        Scanner input = new Scanner(System.in);
        
        //Read the number of vertices from the first line.
        vertexCount = input.nextInt();
        
        //Skip the rest of the first line.  (Anything else is garbage.)
        input.nextLine();
        
        //Allocate space for the adjacency list and matrix.
        adjacencyMatrix = new boolean[vertexCount][vertexCount];
        adjacencyList = new int[vertexCount][];
        
        //Allocate space for the degree array.
        degreeArray = new int[vertexCount];

        //Read in the remaining lines.
        while(input.hasNextLine())
        {
            Scanner line = new Scanner(input.nextLine());
            
            //Get the first integer on the line.  This represents the u in each edge (u, v).
            int u = line.nextInt();
            
            //Get the remaining integers on the line.  This represent each v in (u, v).
            while(line.hasNextInt())
            {
                int v = line.nextInt();
                
                //We now have the edge (u, v).  Process accordingly.
                
                //If the edge hasn't already been read in somewhere else...
                if(!adjacencyMatrix[u][v])
                {
                    degreeArray[u]++;
                    degreeArray[v]++;
                    adjacencyMatrix[u][v] = adjacencyMatrix[v][u] = true;
                    edgeCount++;
                }
            }
        }    

        //We now have everything except the adjacency list.  Create that now.
        for(int u = 0; u < vertexCount; u++)
        {
            adjacencyList[u] = new int[degreeArray[u]];
           
            int degreeCounter = 0;
            
            //March through row u of the adjacency matrix, inserting v into u's 
            //adjacency list, if (u, v) is in the edge set.
            for(int v = 0; v < vertexCount; v++)
                if(adjacencyMatrix[u][v])
                    adjacencyList[u][degreeCounter++] = v;
        }

    }

    //This methods writes the graph to System.out in adjacency list format.
    public void output()
    {
        System.out.println(vertexCount);
        
        for(int i = 0; i < vertexCount; i++)
        {
            System.out.print("" + i + " ");
            for(int degree = 0; degree < degreeArray[i]; degree++)
                System.out.print("" + adjacencyList[i][degree] + " ");
                
            System.out.println();
        }
    }

    //This method determines whether two vertices are adjacent.
    public boolean areAdjacent(int u, int v)
    {
        return u != v && adjacencyMatrix[u][v];
    }

    //This method returns the vertex count.
    public int getVertexCount()
    {
        return vertexCount;
    }
}