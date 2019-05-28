import java.io.IOException;

// Benjamin Chappell

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RunThings
{
    // First arg is the graph file
    public static void main(String[] args) throws IOException
    {
        String graphFile = args[0];

        // Compile and run the clustering - don't need compiling for the final version, will just slow us down
        Runtime.getRuntime().exec("javac -cp '.:jblas-1.2.4.jar' ClusterGraph.java");
        Runtime.getRuntime().exec("java -cp '.:jblas-1.2.4.jar' ClusterGraph " + graphFile + " >clusterResults.txt");

        Runtime.getRuntime().exec("javac ColorGraph.java");
        //runProcess("java ColorGraph clusterResults.txt " + graphFile);
        Runtime.getRuntime().exec("java ColorGraph clusterResults.txt " + graphFile + " >output.txt");
    }

    /*private static void printLines(String cmd, InputStream ins) throws IOException 
    {
        String line = null;
        BufferedReader in = new BufferedReader(
            new InputStreamReader(ins));
        while ((line = in.readLine()) != null) 
        {
            System.out.println(cmd + " " + line);
        }
      }

      private static void runProcess(String command) throws IOException 
      {
        Process pro = Runtime.getRuntime().exec(command);
        //printLines(command + " stdout:", pro.getInputStream());
        //printLines(command + " stderr:", pro.getErrorStream());
        printLines("", pro.getInputStream());
        System.out.println(command + " exitValue() " + pro.exitValue());
      }*/
}