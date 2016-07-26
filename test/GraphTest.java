

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Stack;
import jgraph.AdjList.AdjacencyList;
import jgraph.AdjList.City;
import jgraph.AdjMatrix.AdjacencyMatrix;
import jgraph.graph.Graph;
import jgraph.graph.ParamCollector;
import jgraph.graph.StringInterpreter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Richard
 */
public class GraphTest {
    
    /**
     * Creates a Lambda expression that handles each city name that is found in
     * a data file. Any parameter found will be used to initialize the
     * population of the city.
     */
    private static StringInterpreter<City> cityString = (StringInterpreter<City>) (String str) -> {
        City city = null;
        for (int c = 0; c < str.length(); c++) {
            if (str.charAt(c) == '(') {
                city = new City(str.substring(0, c));

                String strParams = str.substring(c, str.length());
                Stack<String> params = ParamCollector.getParams(strParams);

                if (!params.isEmpty()) {
                    float pop = Float.parseFloat(params.pop());
                    city.setPopulation(pop);
                }

            }
        }

        return (city == null) ? new City(str) : city;
    };
    
    
    public GraphTest() {
        
    }
    
    @Test
    public void adjListCityTest(){
        Graph<City> graph = new AdjacencyList<>();
        PrintStream output = null;
        try {
            output = new PrintStream(new File("adjListCityTest.txt"));
        } catch (FileNotFoundException fileNotFoundException) {
            fail();
        }
        Graph.readGraph("RichardCGP.txt", graph, output, cityString);
        output.close();
    }
    
    
    @Test
    public void adjMatrixCityTest(){
        Graph<City> graph = new AdjacencyMatrix<>();
        PrintStream output = null;
        try {
            output = new PrintStream(new File("adjMatrixCityTest.txt"));
        } catch (FileNotFoundException fileNotFoundException) {
            fail();
        }
        Graph.readGraph("RichardCGP.txt", graph, output, cityString);
        output.close();
    }
 
    @Test
    public void connectedAdjListTest(){
        Graph<City> graph = new AdjacencyList<>();
        PrintStream output = null;
        try {
            output = new PrintStream(new File("adjListConnectionTest.txt"));
        } catch (FileNotFoundException fileNotFoundException) {
            fail();
        }
        Graph.readGraph("connected.txt", graph, output);
        output.close();
    }
    
    @Test
    public void connectedAdjMatrixTest(){
        Graph<City> graph = new AdjacencyMatrix<>();
        PrintStream output = null;
        try {
            output = new PrintStream(new File("adjMatrixConnectionTest.txt"));
        } catch (FileNotFoundException fileNotFoundException) {
            fail();
        }
        Graph.readGraph("connected.txt", graph, output);
        output.close();
    }
    
}
