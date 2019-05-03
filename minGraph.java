// Ivy Zhang, Joe, Neo, and Ben

import java.*;

public class minGraph
{
    public static int randomInt(int min, int max) 
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;  
    }
    
    public static boolean colorTest(int[] colors, Graph g)
    {
        for(int u = 0; u < g.getVertexCount(); u++)
            for(int v = 0; v < g.getVertexCount(); v++)
                if(u != v && g.areAdjacent(u, v) && colors[u] == colors[v])
                {
                    return false;
                }
        
        //for(int color : colors)
        //    System.out.print(color + " ");
        //    
        //System.out.println();
            
        return true;
    }
    
    public static void main(String[] args)
    {
        Graph inputGraph = new Graph();
        int vertexCount = inputGraph.getVertexCount();
        int[] vertexColors = new int[vertexCount];
        
        int minColorChoices = vertexCount;
        int minColors[] = new int[vertexCount];
        
        for (int colorChoices = vertexCount - 1; colorChoices > 0; colorChoices--)
        {
            for (int attempt = 0; attempt <= 100000; attempt++)
            {
                for (int node = 0; node < vertexColors.length; node++)
                {
                    vertexColors[node] = randomInt(0, colorChoices);
                }
                
                if (colorTest(vertexColors, inputGraph))
                {
                    minColorChoices = colorChoices;
                    minColors = vertexColors;
                }
            }
        }
        
        
        System.out.println("There were " + minColorChoices + " colors");
        for(int color: minColors)
            System.out.print(color + " ");
            
           
    }
}
