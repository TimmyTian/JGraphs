

package jgraph.graph;

/**
 * An object used to wrap data about a vertex when Dijkstra's algorithm is
 * applied to a graph. It contains whether a vertex has been visited or not,
 * gives the shortest distance from the destination, and the predecessor. 
 * @author Richard DeSilvey
 */
public class DijkstrasVertex {

    private boolean visited;
    private float dist;
    private Object pred;

    public DijkstrasVertex() {
        visited = false;
        dist = Float.MAX_VALUE;
        pred = null;
    }

    public Object getPred(){
        return pred;
    }
    
    public void setPred(Object pred){
        this.pred = pred;
    }
    
    public float getDistance() {
        return dist;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public void setDistance(float dist) {
        this.dist = dist;
    }

    public void setup() {
        visited = false;
        dist = Float.MAX_VALUE;
        pred = null;
    }

    
}
