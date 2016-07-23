
package jgraph.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;

/**
 * In the most common sense of the term, a graph is an ordered pair G = (V, E)
 * comprising a set V of vertices or nodes or points together with a set E of
 * edges or arcs or lines, which are 2-element subsets of V (i.e., an edge is
 * related with two vertices, and the relation is represented as an unordered
 * pair of the vertices with respect to the particular edge). To avoid
 * ambiguity, this type of graph may be described precisely as undirected,
 * weighted, and simple.
 *
 * @author Richard DeSilvey
 * @param <Data> The object representation of a vertex
 */
public abstract class Graph<Data extends Comparable> implements JGraphs<Vertex, Edge, Data> {

    /**
     * True if the graph is directed.
     */
    protected boolean isDirected;
    /**
     * True if the graph is weighted.
     */
    protected boolean isWeighted;
    
    /**
     * The number of vertices in this graph.
     */
    protected int numVertices;
    
    /**
     * The number of edges in this graph. If a graph is directed the number
     * of edges should be n(n-1), undirected graphs should have n(n-1)/2,
     * where n is the number of vertices.
     */
    protected int numEdges;
    
    /**
     * Sets up this graph as an undirected, and unweighted graph.
     */
    public Graph(){
        this(false, false);
    }
    
    /**
     * Specifies what kind if graph it is, direct and weighted.
     * @param directed If this graph is directed
     * @param weighted If this graph is weighted
     */
    public Graph(boolean directed, boolean weighted){
        isDirected = directed;
        isWeighted = weighted;
        numVertices = 0;
        numEdges = 0;
    }

    /**
     * Gets the internal value count of vertices for this graph.
     * @return The number of vertices in this graph.
     */
    public int getVertexCount() {
        return numVertices;
    }
    /**
     * Gets the internal value count of edges for this graph.
     * @return The number of edges in this graph.
     */
    public int getEdgeCount(){
        return numEdges;
    }
    
    /**
     * A weighted graph is a graph where edges have values bound to them.
     * @return True if this graph is weighted.
     */
    public boolean isWeighted() {
        return isWeighted;
    }

    /**
     * A directed graph is a graph where edges have direction. A - B means
     * that A goes to B but B can't go to A unless it is added to the graph.
     * @return True if this graph is directed.
     */
    public boolean isDirected() {
        return isDirected;
    }
    
    /**
     * Reads from a file using the fileName and constructs the graph performing
     * tests and printing out the graph before test functions are called and
     * prints the final graph after.
     *
     * @param fileName The data file
     * @param graph The graph being constructed
     * @param stream The print stream where output is sent
     * @see jgraph.graph.Graph#readGraph(java.lang.String, jgraph.graph.Graph,
     * java.io.PrintStream, jgraph.graph.StringInterpreter)
     */
    public static void readGraph(String fileName, Graph graph, PrintStream stream){
        StringInterpreter vertInterp = (StringInterpreter<String>) (String o) -> (o);
        readGraph(fileName, graph, stream, vertInterp);
    }
    
    /**
     * Reads from a file using the fileName and constructs the graph performing
     * tests and printing out the graph before test functions are called and
     * prints the final graph after. The method also takes a StringInterpreter
     * which guides the method on how to contsruct the data that is used to
     * represent a vertex. The interpreter can return any kind of Object, later
     * it will be already casted to the proper object type when you perform
     * operations on the graph.
     *
     * @param fileName The name of the data file
     * @param graph The graph being constructed.
     * @param stream The print stream where output is sent
     * @param vertInterp Generic to help the interpreter translate vertex
     * objects
     * @see jgraph.graph.Graph#readGraph(java.lang.String, jgraph.graph.Graph,
     * java.io.PrintStream)
     */
    public static void readGraph(String fileName, Graph graph, 
                        PrintStream stream, StringInterpreter vertInterp) {

        Stack<String> commands = parseToStack(fileName);

        checkSyntax((Stack<String>) commands.clone());
        
        boolean directed, weighted;
        String nextCommand;
        
        weighted = commands.pop().equalsIgnoreCase("weighted");
        directed = commands.pop().equalsIgnoreCase("directed");
        
        graph.isDirected = directed;
        graph.isWeighted = weighted;
        
        nextCommand = commands.pop();
        Object vertA, vertB;
        boolean noDup = true;
        float weight;
        if (nextCommand.equalsIgnoreCase("begin")) {
            // Collect the vertices
            while (noDup) {
                nextCommand = commands.pop();
                if (nextCommand.equalsIgnoreCase("end")) {
                    noDup = false;
                }else{
                    vertA = vertInterp.translate(nextCommand);
                    noDup = graph.addVertex(new Vertex((Comparable) vertA));
                }
            }
            commands.push(nextCommand);
            // Build Edges
            while (!commands.isEmpty()) {
                if (commands.peek().equalsIgnoreCase("end")) {
                    commands.pop();
                    break;
                }
                vertA = vertInterp.translate(commands.pop());
                vertB = vertInterp.translate(commands.pop());
                if (weighted) {
                    nextCommand = commands.pop();
                    weight = Float.parseFloat(nextCommand);
                    graph.addEdge(vertA, vertB, new Edge(weight));
                } else {
                    graph.addEdge(vertA, vertB, new Edge());
                }
            }

            graph.printGraph(stream);
            
            boolean fb;
            String test;
            // Process commands
            while (!commands.isEmpty()) {
                nextCommand = commands.pop();
                
                if (nextCommand.charAt(0) == '*'){
                    stream.println(nextCommand);
                    continue;
                }
                
                switch (nextCommand) {

                    case "hasEdge":
                        vertA = vertInterp.translate(commands.pop());
                        vertB = vertInterp.translate(commands.pop());
                        boolean hasEdge = graph.hasEdge(vertA, vertB);
                        stream.println("hasEdge " + vertA + " " + vertB);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + hasEdge);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((hasEdge == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;

                    case "hasVertex":
                        vertA = vertInterp.translate(commands.pop());
                        boolean hasVertex = graph.hasVertex(vertA);
                        stream.println("hasVertex " + vertA);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + hasVertex);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((hasVertex == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;

                    case "hasCircuit":
                        vertA = vertInterp.translate(commands.pop());
                        boolean hasCircuit = graph.hasCircuit(vertA);
                        stream.println("hasCircuit " + vertA);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + hasCircuit);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((hasCircuit == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;

                    case "addEdge":
                        vertA = vertInterp.translate(commands.pop());
                        vertB = vertInterp.translate(commands.pop());
                        boolean addEdge;
                        if (weighted) {
                            nextCommand = commands.pop();
                            weight = Float.parseFloat(nextCommand);
                            addEdge = graph.addEdge(vertA, vertB, new Edge(weight));
                        } else {
                            addEdge = graph.addEdge(vertA, vertB, new Edge());
                        }
                        stream.println("addEdge " + vertA + " " + vertB);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + addEdge);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((addEdge == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;

                    case "addVertex":
                        vertA = vertInterp.translate(commands.pop());
                        boolean addVertex;
                        addVertex = graph.addVertex(new Vertex((Comparable) vertA));
                        stream.println("addVertex " + vertA);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + addVertex);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((addVertex == fb) ? "PASS" : "FAIL");
                        }
                        System.out.println();
                        break;

                    case "deleteVertex":
                        vertA = vertInterp.translate(commands.pop());
                        boolean deleteVertex;
                        deleteVertex = graph.deleteVertex(vertA);
                        stream.println("deleteVertex " + vertA);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + deleteVertex);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((deleteVertex == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                    case "deleteEdge":
                        vertA = vertInterp.translate(commands.pop());
                        vertB = vertInterp.translate(commands.pop());
                        boolean deleteEdge;
                        deleteEdge = graph.deleteEdge(vertA, vertB);
                        stream.println("deleteEdge " + vertA + " " + vertB);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + deleteEdge);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((deleteEdge == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                    case "isSparse":
                        boolean isSparse = graph.isSparse();
                        stream.println("isSparse");
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + isSparse);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((isSparse == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                    case "isDense":
                        boolean isDense = graph.isDense();
                        stream.println("isDense");
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + isDense);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((isDense == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                    case "isConnected":
                        boolean isConnected = graph.isConnected();
                        stream.println("isConnected");
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + isConnected);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((isConnected == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                    case "isFullyConnected":
                        boolean isFullyConnected = graph.isFullyConnected();
                        stream.println("isFullyConnected");
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + isFullyConnected);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((isFullyConnected == fb) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                        
                    case "getEdge":
                        vertA = vertInterp.translate(commands.pop());
                        vertB = vertInterp.translate(commands.pop());
                        Object edge = graph.getEdge(vertA, vertB);
                        stream.println("getEdge " + vertA + " " + vertB);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + (edge != null) + " -> " 
                                    + vertA + " to " + vertB + " " + edge);
                        }else{
                            fb = Boolean.parseBoolean(test);
                            stream.print(fb + " -> ");
                            stream.println((((edge != null) == fb) ? "PASS" : "FAIL")
                            + " -> " + vertA + " to " + vertB + " " + edge);
                        }
                        stream.println();
                        break;
                    case "shortestPath":
                        vertA = vertInterp.translate(commands.pop());
                        vertB = vertInterp.translate(commands.pop());
                        ArrayList<Vertex> path = graph.shortestPath(vertA, vertB);
                        stream.println("shortestPath " + vertA + " to "+vertB);
                        printPath(stream, path);
                        
                        break;
                        
                    case "shortestPaths":
                        vertA = vertInterp.translate(commands.pop());
                        boolean shortestPaths = graph.shortestPaths(vertA);
                        stream.print("shortestPaths "+vertA+" -> ");
                        stream.print(shortestPaths ? "DONE" : "FAIL");
                        stream.println("\n");
                        break;
                    case "shortestDist":
                        vertA = vertInterp.translate(commands.pop());
                        if (((Vertex)graph.getVertex(vertA)).dijkstra() == null){
                            stream.println("shortestDist " + vertA + " -> NULL");
                            test = commands.pop();
                            break;
                        }
                        float dist = ((Vertex)graph.getVertex(vertA)).dijkstra().getDistance();
                        stream.print("shortestDist " + vertA);
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println(" = " + dist);
                        }else{
                            float f = Float.parseFloat(test);
                            stream.println();
                            stream.print(f + " = (" + dist + ") -> ");
                            stream.println((dist == f) ? " PASS" : " FAIL");
                        }
                        stream.println();
                        break;    
                    case "getVertexCount":
                        int vertCount = graph.getVertexCount();
                        stream.println("getVertexCount");
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + vertCount);
                        }else{
                            int testCount = Integer.parseInt(test);
                            stream.print(testCount + " -> ");
                            stream.println((testCount == vertCount) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                    case "permuteShortestPaths":
                        vertA = vertInterp.translate(commands.pop());
                        stream.println("permuteShortestPaths " + vertA);
                        graph.permuteShortestPaths(vertA, stream);
                        stream.println();
                        break;   
                    case "getEdgeCount":
                        int edgeCount = graph.getEdgeCount();
                        stream.println("getEdgeCount");
                        test = commands.pop();
                        if (test.equals("?")){
                            stream.println("? = " + edgeCount);
                        }else{
                            int testCount = Integer.parseInt(test);
                            stream.print(testCount + " -> ");
                            stream.println((testCount == edgeCount) ? "PASS" : "FAIL");
                        }
                        stream.println();
                        break;
                        
                    default:
                        stream.println("Unable to process command: " + nextCommand);
                        
                }
            }

        }
        stream.println();
        graph.printGraph(stream);

    }
    
    /**
     * Prints the path given in the form |{v1, v2, ..., vn}| = Total Distance.
     * @param stream The stream being printed to
     * @param path The list of vertices
     */
    public static void printPath(PrintStream stream, ArrayList<Vertex> path){
        if (path != null && path.size() > 1){
            stream.print("|{");
            for (int i = 0; i < path.size() - 1; i++){
                stream.print(path.get(i) + ", ");
            }
            stream.print(path.get(path.size() - 1));
            stream.print("}| = " + path.get(path.size() - 1).dijkstra().getDistance());
            stream.println();
        }else{
            stream.println("No Path Found");
        }
    }
    
    /**
     * Gets the contents of a file and pushes all relevant commands onto the
     * stack and then flips the stack on return.
     * @param fileName The file name for the datafile
     * @return The command stack
     */
    private static Stack<String> parseToStack(String fileName){
        
        Stack<String> commands = new Stack<>();
        String line = null;
        File file = new File(fileName);
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(file));
            char cur;
            String word = "";
            while ((line = reader.readLine()) != null) {
                
                for (int c = 0; c < line.length(); c++){
                    cur = line.charAt(c);
                    
                    if (cur == ' ') {
                        if (!word.isEmpty()) {
                            commands.push(word);
                            word = "";
                        }
                        continue;
                    }
                    if (cur == '*') {
                        if (!word.isEmpty()) {
                            commands.push(word);
                            word = "";
                        }else{
                        
                            if ((c + 1) < line.length()){
                                if (line.charAt(c + 1) == '*'){
                                    commands.push(line);
                                }
                            }
                        }
                        break;
                    }
                    word += cur;
                }
                if (!word.isEmpty()) {
                    commands.push(word);
                    word = "";
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
            }
        }
        
        Stack<String> temp = new Stack<>();
        
        while(!commands.isEmpty()){
            temp.push(commands.pop());
        }
        
        return temp;
        
    }
    
    /**
     * Quickly checks to make sure the data file is valid.
     * @param commands The copy of the original command stack.
     */
    private static void checkSyntax(Stack<String> commands) {
        
        String nextCommand = commands.pop();
        
        if (!nextCommand.equalsIgnoreCase("weighted") &&
                !nextCommand.equalsIgnoreCase("unweighted")){
            throw new RuntimeException("Invalid graph modifier: " + nextCommand);
        }
        
        nextCommand = commands.pop();
        if (!nextCommand.equalsIgnoreCase("directed") &&
                !nextCommand.equalsIgnoreCase("undirected")){
            throw new RuntimeException("Invalid graph modifier: " + nextCommand);
        }
        
        nextCommand = commands.pop();
        
        if (!nextCommand.equalsIgnoreCase("begin")){
            throw new RuntimeException("Expected 'begin', found: " + nextCommand);
        }
        nextCommand = commands.pop();
        while(!nextCommand.equalsIgnoreCase("end")){
            
            if (commands.isEmpty()){
                throw new RuntimeException("End of stack, 'end' not found");
            }
            nextCommand = commands.pop();
        }
        
    }
    
    
}
