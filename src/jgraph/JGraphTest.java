
package jgraph;

import java.util.Stack;
import jgraph.AdjList.City;
import jgraph.AdjMatrix.AdjacencyMatrix;
import jgraph.graph.Graph;
import jgraph.graph.ParamCollector;
import jgraph.graph.StringInterpreter;

/**
 *
 * @author Richard DeSilvey
 */
public class JGraphTest {

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        /**
         * Creates a Lambda expression that handles each city name that is
         * found in a data file. Any parameter found will be used to
         * initialize the population of the city.
         */
        StringInterpreter<City> interp = (StringInterpreter<City>) (String str) -> {
            City city = null;
            for (int c = 0; c < str.length(); c++){
                if (str.charAt(c) == '('){
                    city = new City(str.substring(0, c));
                    String strParams = str.substring(c, str.length());
                    Stack<String> params = ParamCollector.getParams(strParams);
                    float pop = Float.parseFloat(params.pop());
                    city.setPopulation(pop);
                }
            }
            
            return (city == null)? new City(str) :city;            
        };

//        AdjacencyList<City> graph2 = new AdjacencyList<>();
//        AdjacencyList<String> graph2 = new AdjacencyList<>();
        
        AdjacencyMatrix<City> graph2 = new AdjacencyMatrix<>();
        
//        PrintStream stream = null;
//        try {
//            stream = new PrintStream(new File("Output.txt"));
//        } catch (FileNotFoundException fileNotFoundException) {
//            return;
//        }
        
//        Graph.readGraph("RichardCGP.txt", graph2, System.out, interp);
        Graph.readGraph("Richard3.txt", graph2, System.out);

    }
}
