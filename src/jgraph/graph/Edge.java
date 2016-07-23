package jgraph.graph;

import java.text.DecimalFormat;

/**
 * A simple object representing an edge for a graph that contains an optional
 * weight value. Weight values that are equal to zero are not part of a weighted
 * graph since weights must be greater than 0.
 *
 * @author Richard DeSilvey
 */
public class Edge {

    /**
     * The weight of the edge.
     */
    private float weight;
    private static DecimalFormat df = new DecimalFormat("0.0");
    /**
     * Creates an edge with no weight
     */
    public Edge() {
        weight = 0;// For non-weighted graphs
    }

    /**
     * Creates an edge with a specified weight
     *
     * @param weight The weight for this edge
     */
    public Edge(float weight) {
        this.weight = weight;
    }
    
    /**
     * Gets the weight of the edge.
     *
     * @return The weight value for this edge.
     */
    public float getWeight() {
        return weight;
    }
    
    
    public String toString(){
        return df.format(weight);
    }

}
