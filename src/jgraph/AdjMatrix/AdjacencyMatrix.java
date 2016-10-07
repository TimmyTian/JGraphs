package jgraph.AdjMatrix;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;
import jgraph.graph.Edge;
import jgraph.graph.Graph;
import jgraph.graph.Vertex;
import priorityqueue.PriorityQueue;

/**
 * AdjacencyMatrix representation for a graph. The matrix[1...n][1...n] are the
 * edges or the center portion of the matrix minus the diagonals matrix[a][a].
 * The matrix[0][1...n] and matrix[1...n][0] are the vertices or the outer
 * portions of the matrix. Each location stores a Node object which contains a
 * vertex and edge reference and an index corresponding to a vertex location in
 * the matrix for easier operations. Both vertex locations matrix[0][a] and
 * matrix[a][0] hold the same reference to their Node, making updates on
 * vertices easier.
 *
 * @author Richard DeSilvey
 * @param <Data> The data representing a vertex in this graph.
 */
public class AdjacencyMatrix<Data extends Comparable> extends Graph<Data> {

    public static final int DEFAULT_RATE = 5, DEFAULT_SIZE = 2;

    /**
     * The matrix[1...n][1...n] are the edges or the center portion of the
     * matrix minus the diagonals matrix[a][a]. The matrix[0][1...n] and
     * matrix[1...n][0] are the vertices or the outer portions of the matrix.
     * Each location stores a Node object which contains a vertex and edge
     * reference and an index corresponding to a location in the matrix for
     * easier operations. Both vertex locations matrix[0][a] and matrix[a][0]
     * hold the same reference to a Node making updates on vertices easier.
     */
    private Object[][] matrix;

    /**
     * The rate at which the matrix expands when the number of vertices reaches
     * the maximum size or length.
     */
    private int expansionRate;

    private class Node {

        /**
         * Possible data of the vertex
         */
        private Vertex<Data> vertex;
        /**
         * Possible data for the edge
         */
        private Edge edge;
        /**
         * A quick index reference
         */
        private int index;

        public Node() {
            vertex = null;
            edge = null;
            index = 0;
        }

        public Node(Vertex<Data> v, int index) {
            vertex = v;
            this.index = index;
        }

        public Node(Edge edge) {
            this.edge = edge;
        }

        public String toString() {
            String edgeStr = (edge == null) ? "   " : edge.toString();
            String weight = (isWeighted ? edgeStr : "x  ");

            return vertex == null ? weight
                    : (isWeighted ? vertex.toString() + "   " : vertex.toString());
        }
    }

    /**
     * Creates a matrix with a default expansion rate of 5.
     */
    public AdjacencyMatrix() {
        this(DEFAULT_RATE, DEFAULT_SIZE);
    }

    /**
     * Creates a new matrix with a specified initial size with a default 
     * expansion rate of 5.
     * @param initSize The size of this matrix
     */
    public AdjacencyMatrix(int initSize){
        this(DEFAULT_RATE, initSize);
    }
    
    /**
     * Creates a new matrix with a specified expansion rate. The expansion rate
     * is the rate at which the matrix grows at when a size limit is reached.
     * The matrix will not retract in size.
     *
     * @param expansionRate The rate at which the matrix expands when the number
     * of vertices reaches the maximum size or length.
     * @param initSize The size of the matrix when it is first initialized.
     */
    public AdjacencyMatrix(int expansionRate, int initSize) {
        super();
        matrix = new Object[initSize][initSize];
        this.expansionRate = expansionRate;
    }

    @Override
    public boolean hasEdge(Data a, Data b) {
        return (getEdge(a, b) != null);
    }

    @Override
    public Edge getEdge(Data a, Data b) {
        Node vertA = getVertexNode(a), vertB = getVertexNode(b);

        if (vertA != null && vertB != null) {
            int indexA = vertA.index, indexB = vertB.index;
            Node node = (Node) matrix[indexA][indexB];
            return (node == null) ? null : node.edge;
        }
        return null;
    }

    @Override
    public boolean hasVertex(Data vert) {
        return (getVertex(vert) != null);
    }

    public Vertex getVertex(Data v) {
        return getVertexNode(v).vertex;
    }

    @Override
    public boolean hasCircuit(Data vert) {
        return false;
    }

    @Override
    public boolean permuteShortestPaths(Data a, PrintStream stream) {

        Node from = getVertexNode(a);

        if (from == null) {
            return false;
        }

        if (from.vertex.dijkstra().getDistance() != 0) {
            if (!shortestPaths(a)) {
                return false;
            }
        }

        ArrayList<Vertex> path;
        Node to = (Node) matrix[0][1];
        Data r1 = from.vertex.getData(), r2;
        while (to != null) {
            r2 = to.vertex.getData();
            path = shortestPath(r1, r2);
            stream.println("shortestPath " + from + " to " + to);
            Graph.printPath(stream, path);

            int index = (to.index + 1 < matrix[0].length) ? to.index + 1 : -1;
            if (index == -1) {
                break;
            }
            to = (Node) matrix[0][index];
        }

        return true;
    }

    @Override
    public boolean shortestPaths(Data a) {

        Node from = getVertexNode(a);

        if (!isConnected() || !isWeighted() || from == null) {
            return false;
        }

        PriorityQueue<Node> queue = new PriorityQueue<>();

        for (int i = 1; i < matrix[0].length; i++) {
            if (matrix[0][i] != null) {
                ((Node) matrix[0][i]).vertex.dijkstra().setup();
            }
        }

        from.vertex.dijkstra().setDistance(0);
        queue.enqueue(from, 0);

        Node curVertex;

        while (!queue.isEmpty()) {

            curVertex = queue.dequeue();

            if (!curVertex.vertex.dijkstra().isVisited()) {

                for (int j = 1; j < matrix.length; j++) {
                    Node edgeNode = (Node) matrix[j][curVertex.index];

                    if (edgeNode != null) {
                        Vertex vertex = ((Node) matrix[j][0]).vertex;
                        if (!vertex.dijkstra().isVisited() && edgeNode.edge != null) {
                            float p = edgeNode.edge.getWeight()
                                    + curVertex.vertex.dijkstra().getDistance();

                            if (p < vertex.dijkstra().getDistance()) {
                                vertex.dijkstra().setDistance(p);
                                vertex.dijkstra().setPred(curVertex);
                            }
                            p = vertex.dijkstra().getDistance();
                            queue.enqueue((Node) matrix[j][0], p);
                        }
                    }
                }
                curVertex.vertex.dijkstra().setVisited(true);
            }
        }

        return true;
    }

    public ArrayList shortestPath(Data a, Data b) {

        ArrayList<Vertex> path = new ArrayList<>();
        Stack<Vertex> predStack = new Stack<>();

        Node from = getVertexNode(a);

        if (from == null) {
            return null;
        }

        if (from.vertex.dijkstra().getDistance() != 0) {
            if (!shortestPaths(a)) {
                return null;
            }
        }

        Node to = getVertexNode(b);

        if (to != null) {
            predStack.push(to.vertex);
            Node pred = (Node) to.vertex.dijkstra().getPredecessor();
            while (pred != null) {
                predStack.push(pred.vertex);
                pred = (Node) pred.vertex.dijkstra().getPredecessor();
            }
        }

        while (!predStack.isEmpty()) {
            path.add(predStack.pop());
        }

        return path;
    }

    @Override
    public boolean addEdge(Data a, Data b, Edge edge) {

        Node vertA = getVertexNode(a), vertB = getVertexNode(b);

        if (vertA != null && vertB != null) {
            int indexA = vertA.index, indexB = vertB.index;
            if (matrix[indexA][indexB] != null
                    && matrix[indexB][indexA] != null) {
                return false;
            }
            Node node = new Node(edge);
            if (isDirected) {
                matrix[indexA][indexB] = node;
            } else {
                matrix[indexA][indexB] = node;
                matrix[indexB][indexA] = node;
            }
            numEdges++;
            return true;
        }

        return false;
    }

    @Override
    public boolean addVertex(Vertex nvert) {
        int nIndex = -1;
        Node node;
        for (int i = 1; i < matrix[0].length; i++) {
            node = (Node) matrix[0][i];
            if (node == null) {
                nIndex = i;
                break;
            } else {
                Data r1 = node.vertex.getData();
                Data r2 = (Data) nvert.getData();
                if (r1.compareTo(r2) == 0) {
                    return false;
                }
            }
        }
        if (nIndex == -1) {
            expand(expansionRate);
            nIndex = matrix[0].length - expansionRate;
        }
        node = new Node(nvert, nIndex);
        matrix[0][nIndex] = node;
        matrix[nIndex][0] = node;
        numVertices++;
        return true;
    }

    @Override
    public boolean deleteEdge(Data a, Data b) {
        Node vertA = getVertexNode(a), vertB = getVertexNode(b);

        return deleteEdge(vertA, vertB);
    }

    @Override
    public boolean deleteVertex(Data vert) {

        for (int i = 1; i < matrix[0].length; i++) {
            if (matrix[0][i] != null) {
                if (((Node) matrix[0][i]).vertex.getData().compareTo(vert) == 0) {
                    matrix[0][i] = null;
                    matrix[i][0] = null;
                    numVertices--;

                    for (int j = 1; j < matrix.length; j++) {
                        matrix[j][i] = null;
                        matrix[i][j] = null;
                        numEdges--;
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private float maxEdges(){
        float denom = numVertices * (numVertices - 1);
        return (isDirected ? denom : denom / 2f);
    }
    
    @Override
    public boolean isSparse() {

        if (numVertices == 1) {
            return false;
        }

        return (numEdges / maxEdges()) <= 0.15f;
    }

    @Override
    public boolean isDense() {

        if (numVertices == 1) {
            return true;
        }

        return (numEdges / maxEdges()) >= 0.85f;
    }

    @Override
    public boolean isConnected() {

        if (numVertices < 2) {
            return true;
        }
        ArrayList<Node> visited = new ArrayList<>();
        int fromIndex = getNext(1);
        int toIndex = getNext(fromIndex + 1);
        Node fromVert = (Node) matrix[0][fromIndex], toVert = (Node) matrix[0][toIndex];

        for (int i = toIndex; i < matrix[0].length; i++) {
            if (hasEdge(fromVert, toVert)) {
                continue;
            } else {
                visited.add(fromVert);
                boolean found = find(visited, fromVert, toVert);
                if (!found) {
                    return false;
                } else {
                    visited.clear();
                }
            }
            toVert = (Node) matrix[0][i];
        }

        return true;
    }

    @Override
    public boolean isFullyConnected() {
        if (numVertices == 0) {
            return false;
        }

        return (numEdges == maxEdges());
    }

    @Override
    public void printGraph(PrintStream stream) {
        StringBuilder graph = new StringBuilder();

        graph.append(isWeighted ? "Weighted\n" : "Unweighted\n");
        graph.append(isDirected ? "Digraph\n" : "Undigraph\n");
        Node node;
        String nodeStr;
        
        for (Object[] outer : matrix){
            for (Object inner : outer){
                node = (Node) inner;
                if (node == null){
                    graph.append("   ");
                }else{
                    nodeStr = node.toString();
                    if (isWeighted) {
                        graph.append(node == null ? "   "
                                : nodeStr.charAt(0) + ""
                                + (nodeStr.charAt(1) != '.' ? nodeStr.charAt(1) : ' ')
                                + "" + (nodeStr.charAt(2) != '.' ? nodeStr.charAt(2) : ' '));
                    } else {
                        graph.append(node == null ? "   "
                                : nodeStr.charAt(0) + "  ");
                    }
                }
            }
            graph.append("\n");
        }
        stream.println(graph.toString());
    }

    private int getNext(int startFrom) {

        for (int i = startFrom; i < matrix[0].length; i++) {
            if (matrix[0][i] != null) {
                return i;
            }
        }
        return -1;
    }

    private boolean deleteEdge(Node vertA, Node vertB) {
        if (vertA != null && vertB != null) {
            int indexA = vertA.index, indexB = vertB.index;
            matrix[indexA][indexB] = null;
            numEdges--;
            return true;
        }
        return false;
    }

    /**
     * Expands the matrix by a specified size.
     *
     * @param byVal The number of locations to add to the matrix
     */
    private void expand(int byVal) {

        Object[][] temp = new Object[matrix.length + byVal][matrix.length + byVal];

        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, temp[i], 0, matrix.length);
        }
        matrix = temp;
    }

    /**
     * Fetches a vertex in the range matrix[0][1...n]
     *
     * @param v The data used to find a vertex
     * @return The node containing the vertex, null if nothing found
     */
    private Node getVertexNode(Data v) {
        for (int i = 1; i < matrix[0].length; i++) {
            if (matrix[0][i] != null) {
                if (((Node) matrix[0][i]).vertex.getData().compareTo(v) == 0) {
                    return (Node) matrix[0][i];
                }
            }
        }
        return null;
    }

    /**
     * Finds a path from start to finish.
     *
     * @param visited The list of vertices already visited
     * @param start The start vertex
     * @param finish The end vertex
     * @return True if there exists a path from start to finish.
     */
    private boolean find(ArrayList<Node> visited, Node start, Node finish) {

        Node edge;
        boolean found = false;
        for (int i = 0; i < matrix.length; i++) {
            edge = (Node) matrix[i][0];
            if (edge == null) {
                continue;
            }
            Data r1 = edge.vertex.getData();
            Data r2 = start.vertex.getData();
            if (r1.compareTo(r2) != 0) {
                if (hasEdge(start, edge)) {
                    if (hasEdge(edge, finish)) {
                        return true;
                    }
                    if (!visited.contains(edge)) {
                        visited.add(edge);
                        found = find(visited, edge, finish);
                        if (found) {
                            break;
                        }
                    }
                }
            }
        }
        return found;
    }

    /**
     * Performs faster than hasEdge(Data, Data) because each Node has an index
     * reference to their locations in the matrix. Searching a node based on
     * the data takes longer because it's a sequential search algorithm.
     *
     * @param vertA
     * @param vertB
     * @return
     */
    private boolean hasEdge(Node vertA, Node vertB) {
        if (vertA != null && vertB != null) {
            int indexA = vertA.index, indexB = vertB.index;
            Node a = (Node) matrix[indexA][indexB];
            Node b = (Node) matrix[indexB][indexA];
            return a != null || b != null;
        }
        return false;
    }

}
