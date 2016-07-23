

package jgraph.graph;

/**
 * The string interpreter helps the graph parser deal with different object
 * types in a data file. The name of the vertex is passed into the translate
 * method where the user can handle the string and return the desired object
 * type to the graph. 
 *
 * @author Richard DeSilvey
 * @param <R> The object being returned for a vertex in a graph
 */
public interface StringInterpreter<R> {
    public R translate(String str);
}
