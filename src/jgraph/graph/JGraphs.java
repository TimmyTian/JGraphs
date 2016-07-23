package jgraph.graph;

import java.io.PrintStream;
import java.util.ArrayList;

/**
 * Interface for a graph. There are three objects that can be used to represent
 * different parts of the graph.
 *
 * @author Richard DeSilvey
 * @param <VertexObject> The vertex object.
 * @param <EdgeObject> The edge object.
 * @param <Data> The object representation for a vertex. (String, Character,
 * Person, City, etc.)
 *
 */
public interface JGraphs<VertexObject, EdgeObject, Data> {
    /**
     * Checks to see if there exists an edge between vertex A and B. If the
     * graph is directed then it checks from vertex A to vertex B using the
     * representation for the vertices.
     *
     * @param a From Vertex
     * @param b To Vertex
     * @return True if an edge exists, false otherwise.
     */
    public boolean hasEdge(Data a, Data b);

    /**
     * Fetches the edge between vertices A and B. If no edge exists then null
     * is returned.
     * @param a from vertex
     * @param b to vertex
     * @return The edge going from A to B
     */
    public EdgeObject getEdge(Data a, Data b);
    
    /**
     * Checks to see if there exists the vertex vert in this graph.
     * For simplicity a string is used to find the vertex.
     * @param vert The vertex being searched for
     * @return True if the vertex vert exists
     */
    public boolean hasVertex(Data vert);

    /**
     * Checks the graph to see if a circuit exists.
     * @param vert
     * @return True if a circuit exists.
     */
    public boolean hasCircuit(Data vert);
     
    /**
     * Finds the shortest paths from vertex A. This will preprocess the graph
     * and setup every vertex to have the shortest distance from vertex A.
     *
     * @param a from vertex
     * @return True if the algorithm finished successfully. Will fail if the
     * graph is not weighted and not connected or if the start vertex doesn't
     * exist.
     */
    public boolean shortestPaths(Data a);
    
    /**
     * Gets the shortest path from A to B as an ArrayList of vertices
     * where vertex A is the first in the list and vertex B is the last.
     * No path will be found if A == B
     * @param a from vertex
     * @param b to vertex
     * @return The list of vertices {A, ..., B} where ... are the 
     * vertices between A and B but may also be just {A, B}.
     */
    public ArrayList shortestPath(Data a, Data b);
    
    /**
     * Adds an edge between vertex A and B. If this is a digraph then the
     * edge will go from A to B.
     *
     * @param a From vertex
     * @param b To vertex 
     * @param edge The data for the edge (i.e. weights) between a and b
     * @return True if the edge was added.
     */
    public boolean addEdge(Data a, Data b, EdgeObject edge);

    /**
     * Adds a vertex, duplicates are ignored resulting in the method returning
     * false, otherwise it will return true;
     *
     * @param nvert The vertex being added
     * @return True if the vertex was added, false if duplicates are found.
     */
    public boolean addVertex(VertexObject nvert);

    /**
     * Gets the vertex V from the graph, null if no vertex exists.
     * @param v The vertex to fetch
     * @return The found vertex if V is in the graph.
     */
    public VertexObject getVertex(Data v);
    
    /**
     * Deletes the edge between vertex A and B using the representation 
     * for the vertices.
     *
     * @param a From Vertex
     * @param b To Vertex
     * @return True if the edge was deleted.
     */
    public boolean deleteEdge(Data a, Data b);

    /**
     * Deletes the vertex vert from the graph using the representation 
     * for the vertex.
     *
     * @param vert The vertex being deleted
     * @return True if the vertex was deleted.
     */
    public boolean deleteVertex(Data vert);

    /**
     * Checks the graph to see if the minimal number of edges exist between all
     * vertices (15% or less)
     *
     * @return True if the number of minimal edges exist (15% or less).
     */
    public boolean isSparse();

    /**
     * Checks the graph to see if the maximum number of edges exist between all
     * vertices (85% or more).
     *
     * @return True if the number of maximum edges exist (85% or more).
     */
    public boolean isDense();

    /**
     * Checks the graph to see if the graph is fully connected in a topological
     * space, i.e., there is a path from any point to any other point in the
     * graph
     *
     * @return True if the graph is connected
     */
    public boolean isConnected();

    /**
     * Checks the graph to see if every pair of distinct vertices is connected
     * by a unique edge
     *
     * @return True if the graph is fully connected.
     */
    public boolean isFullyConnected();

    /**
     * Returns the number of vertices in this graph
     * @return The number of vertices currently in this graph
     */
    public int getVertexCount();
    
    /**
     * Returns the number of edges in this graph
     * @return The number of edges currently in this graph
     */
    public int getEdgeCount();
    /**
     * Prints the graph in a text-based print out
     * @param stream The stream where the output is printed to.
     */
    public void printGraph(PrintStream stream);
    
    /**
     * Prints to the given print stream all the shortest paths from
     * A to every vertex in the graph.
     * @param a the from vertex
     * @param stream The print stream
     * @return false if there was a problem with the graph. A disjoint graph
     * will return false, or an unweighted graph will return false.
     */
    public boolean permuteShortestPaths(Data a, PrintStream stream);
}
