
package jgraph.graph;

/**
 * The vertex holds data representing this vertex. The object can be a City,
 * Person, String, Character, etc.
 *
 * @author Richard DeSilvey
 * @param <R> The type of data being used
 */
public class Vertex<R extends Comparable>  {
    
    private R rep;
    private DijkstrasVertex dijkstra;
    private boolean connected;
    
    public Vertex(R name){
        rep = name;
        dijkstra = new DijkstrasVertex();
        connected = false;
    }
    
    public boolean isConnected(){
        return connected;
    }
    
    public void setConnection(boolean connected){
        this.connected = connected;
    }
    
    public DijkstrasVertex dijkstra(){
        return dijkstra;
    }
    
    public R getData(){
        return rep;
    }
    
    public String toString(){
        return rep.toString();
    }
}
