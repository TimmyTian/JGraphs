package jgraph.AdjList;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Stack;
import jgraph.graph.Edge;
import jgraph.graph.Graph;
import jgraph.graph.Vertex;
import priorityqueue.PriorityQueue;

/**
 * A graph represented as an adjacency list. Each node can be represented as a
 * vertex or an edge to a vertex. Vertex nodes in the edge list of each vertex
 * is not the same node object but an edge vertex node holds a reference to
 * itself in the vertex list.
 *
 * @author Richard DeSilvey
 * @param <Data> The type of data being used to represent each vertex of this
 * graph.
 */
public class AdjacencyList<Data extends Comparable> extends Graph<Data> {

    /**
     * Node object used for constructing the adjacency list. By default,
     * internally, a directed graph looks the same as an undirected graph but
     * the separation is with the edge object. If the edge is null then there is
     * no edge but an edge can be added later without allocation of a new node.
     */
    private class Node {

        /**
         * The next node in the vertex list.
         */
        private Node nextVertex;
        /**
         * The next node in the edge list connected to this vertex.
         */
        private Node nextEdge;
        /**
         * A helper reference to the node in the vertex list.
         */
        private Node self;

        /**
         * The edge object.
         */
        private Edge edge;
        /**
         * The vertex this node represents.
         */
        private Vertex<Data> vertex;

        /**
         * New node with no vertex or link.
         */
        public Node() {
            nextVertex = nextEdge = self = null;
            vertex = null;
            edge = null;
        }

        /**
         * Creates a vertex node.
         *
         * @param vert The vertex for this node
         */
        public Node(Vertex<Data> vert) {
            this.vertex = vert;
            nextVertex = nextEdge = self = null;
            edge = null;
        }

        /**
         * Creates a new edge node with a vertex and edge.
         *
         * @param original The original vertex to point to
         * @param edge The edge going to this node
         */
        public Node(Node original, Edge edge) {
            this.edge = edge;
            this.vertex = original.vertex;
            self = original;
            nextVertex = nextEdge = null;
        }

        public String toString() {
            return vertex.toString();
        }
    }

    /**
     * The head of the vertex list.
     */
    private Node vertexListHead;
    
    /**
     * The state of the graph determines if the graph is empty, connected or
     * disjoint. The state keeps track of the number of disjoint subgraphs that
     * exist and/or the number of disjoint vertices. A disjoint subgraph is a
     * graph with one or more vertices that have no path to the root vertex.
     * If the state is less than zero then the graph is empty.
     */
    private int state;
    
    /**
     * Boolean flag to determine the state of the graph if Dijkstra's was
     * applied. Changing the graph by adding a new vertex or deleting
     * edge/vertex will turn the flag to false.
     */
    private boolean dijkstrasApplied;
    
    /**
     * The ratios for determining the density or sparseness of the graph.
     */
    public static final float DENSE_RATIO, SPARSE_RATIO;
    
    static {
        DENSE_RATIO = 0.85f;
        SPARSE_RATIO = 0.15f;
    }
    
    public AdjacencyList() {
        super();
        vertexListHead = null;
        dijkstrasApplied = false;
        state = -1;
    }
    
    @Override
    public boolean hasEdge(Data a, Data b) {
        return (getEdge(a, b) != null);
    }
    
    @Override
    public Edge getEdge(Data a, Data b) {
        
        Node fromVert = getVertexNode(a), toVert = getVertexNode(b);
        if (fromVert != null && toVert != null) {
            
            Node curEdge = fromVert.nextEdge;
            
            if (curEdge == null) return null;
            
            Data r1 = curEdge.vertex.getData();
            Data r2 = fromVert.vertex.getData();
            
            if (r1.compareTo(r2) == 0){
                return null;
            }
            r2 = toVert.vertex.getData();
            while (curEdge != null) {
                r1 = curEdge.vertex.getData();
                if (r1.compareTo(r2) == 0) {
                    return curEdge.edge;
                }
                curEdge = curEdge.nextEdge;
            }
        }
        return null;
    }

    @Override
    public boolean hasVertex(Data vertex) {
        return getVertex(vertex) != null;
    }

    public Vertex getVertex(Data v){
        Node vert = getVertexNode(v);
        return (vert != null) ? vert.vertex: null;
    }
    
    public boolean permuteShortestPaths(Data a, PrintStream stream){
        Node from = getVertexNode(a);
        
        if (from == null){
            return false;
        }
        
        if (from.vertex.dijkstra().getDistance() != 0 || !dijkstrasApplied){
            if (!shortestPaths(a)){
                return false;
            }
        }
        
        ArrayList<Vertex> path;
        Node to = vertexListHead;
        Data r1 = from.vertex.getData(), r2;
        
        while(to != null){
            r2 = to.vertex.getData();
            path = shortestPath(r1, r2);
            stream.println("shortestPath " + from + " to " + to);
            Graph.printPath(stream, path);
    
            to = to.nextVertex;
        }
        
        return true;
    }
     
    @Override
    public ArrayList<Vertex> shortestPath(Data a, Data b){
        
        ArrayList<Vertex> path = new ArrayList<>();
        Stack<Vertex> predStack = new Stack<>();
        
        Node from = getVertexNode(a);
        
        if (from == null){
            return null;
        }
        
        if (from.vertex.dijkstra().getDistance() != 0){
            if (!shortestPaths(a)){
                return null;
            }
        }
        
        Node to = getVertexNode(b);
        
        if (to != null){
            predStack.push(to.vertex);
            Node pred = (Node)to.vertex.dijkstra().getPredecessor();
            while(pred != null){
                predStack.push(pred.vertex);
                pred = (Node) pred.vertex.dijkstra().getPredecessor();
            }
        }
        
        while (!predStack.isEmpty()){
            path.add(predStack.pop());
        }
        
        return path;
    }
    
    @Override
    public boolean shortestPaths(Data a) {
        
        Node from = getVertexNode(a);
        
        if (!isConnected() || !isWeighted() || from == null){
            return false;
        }
        
        PriorityQueue<Node> queue = new PriorityQueue<>();
        
        Node curVert = vertexListHead;
        while(curVert != null){
            curVert.vertex.dijkstra().setup();
            curVert = curVert.nextVertex;
        }
        
        from.vertex.dijkstra().setDistance(0);
        queue.enqueue(from, 0);
        
        // Run through until the queue is empty.
        while(!queue.isEmpty()){
            curVert = queue.dequeue();
            
            if (!curVert.vertex.dijkstra().isVisited()){
                
                Node edgeNode = curVert.nextEdge, temp;
                
                while(edgeNode != null){
                    if (!edgeNode.vertex.dijkstra().isVisited() && edgeNode.edge != null){
                        
                        float p = edgeNode.edge.getWeight() 
                                   + curVert.vertex.dijkstra().getDistance();
                        temp = edgeNode.self;
                        
                        if (p < temp.vertex.dijkstra().getDistance()){
                            temp.vertex.dijkstra().setDistance(p);
                            temp.vertex.dijkstra().setPred(curVert);
                        }
                        p = temp.vertex.dijkstra().getDistance();
                        queue.enqueue(temp, p);
                    }
                    edgeNode = edgeNode.nextEdge;
                }
                curVert.vertex.dijkstra().setVisited(true);
            }
        }
        dijkstrasApplied = true;
        return true;
    }
    
    @Override
    public boolean hasCircuit(Data start) {

        Node vert = getVertexNode(start);
        ArrayList<Node> visited = new ArrayList<>();

        return findCircuit(visited, vert, vert);
    }
    
    @Override
    public boolean addEdge(Data a, Data b, Edge edge) {

        if (isWeighted && edge.getWeight() < 1) {
            throw new RuntimeException("Can't add a non-weighted edge to a "
                    + "weighted graph");
        } 
        boolean b1;
        Node vertA = getVertexNode(a), vertB = getVertexNode(b), node;
        if (vertA != null && vertB != null) {
            if (!hasChild(vertA, vertB)) {
                node = new Node(vertB, edge);
                b1 = putLast(vertA, node);

                if (!isDirected){
                    node = new Node(vertA, edge);
                    b1 &= putLast(vertB, node);
                }
                if (b1){

                    boolean c1 = vertA.vertex.isConnected(),
                            c2 = vertB.vertex.isConnected();
                    
                    if ((c1 && !c2) || (!c1 && c2)){
                        if (!c1){
                            if (connectedToRoot(vertA)){
                                markAllConnected(vertA);
                            }
                        }else if (!c2){
                            if (connectedToRoot(vertB)){
                                markAllConnected(vertB);
                            }
                        }
                    }
                    
                    numEdges++;
                    dijkstrasApplied = false;
                    return true;
                }
            }
        }

        return false;
    }
    
    
    /**
     * Helper method for adding a new vertex without needing to pass
     * a new vertex object.
     * @param v The new vertex being added/
     * @return True if the vertex was added, false otherwise.
     */
    public boolean addVertex(Data v){
        return addVertex(new Vertex(v));
    }
    
    @Override
    public boolean addVertex(Vertex nvert) {
        
        Node current = vertexListHead;
        
        while (current != null) {
            Data r1 = current.vertex.getData();
            Data r2 = (Data)nvert.getData();
            if (r1.compareTo(r2) != 0) {
                if (current.nextVertex == null) {
                    current.nextVertex = new Node(nvert);
                    numVertices++;
                    state++;
                    return true;
                }
            } else {
                return false;
            }

            current = current.nextVertex;
        }

        if (vertexListHead == null) {
            vertexListHead = new Node(nvert);
            nvert.setConnection(true);
            numVertices++;
            state++;
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteEdge(Data a, Data b) {
        
        Node vertexA = getVertexNode(a), vertexB = getVertexNode(b);
        if (vertexA != null && vertexB != null) {
            boolean b1, b2;
            if (isDirected) {
                b1 = removeFrom(vertexA, vertexB);
                
                if (!connectedToRoot(vertexA)){
                    vertexA.vertex.setConnection(false);
                    state++;
                }
                
                return b1;
            } else {
                numEdges++;
                b1 = removeFrom(vertexA, vertexB);
                b2 = removeFrom(vertexB, vertexA);
                
                if (!connectedToRoot(vertexA)){
                    vertexA.vertex.setConnection(false);
                    state++;
                }else {
                    if (!connectedToRoot(vertexB)){
                        vertexB.vertex.setConnection(false);
                        state++;
                    }
                }
                return b1 && b2;
            }
        }
        return false;
    }

    @Override
    public boolean deleteVertex(Data vert) {

        if (vertexListHead.vertex.getData().compareTo(vert) == 0) {
            removeAllEdgesTo(vertexListHead);
            vertexListHead = vertexListHead.nextVertex;
            numVertices--;
            
            if (vertexListHead == null){
                state = -1;
            }
            
            return true;
        }
        
        Node toDelete = vertexListHead.nextVertex;
        Node prev = vertexListHead;

        while (toDelete != null) {
            if (toDelete.vertex.getData().compareTo(vert) == 0) {
                prev.nextVertex = toDelete.nextVertex;
                removeAllEdgesTo(toDelete);
                toDelete = null;
                numVertices--;
                return true;
            }
            prev = toDelete;
            toDelete = toDelete.nextVertex;
        }

        return false;
    }

    private float maxEdges(){
        float denom = numVertices * (numVertices - 1);
        return (isDirected ? denom : denom / 2f);
    }
    
    @Override
    public boolean isSparse() {
        
        if (numVertices == 1) return false;

        return (numEdges / maxEdges()) <= SPARSE_RATIO;
    }

    @Override
    public boolean isDense() {
        
        if (numVertices == 1) return true;

        return (numEdges / maxEdges()) >= DENSE_RATIO;
    }

    @Override
    public boolean isConnected() {
        return (state == 0);
    }

    /**
     * The state of the graph determines if the graph is empty, connected, 
     * or disjoint. This method returns a boolean to determine if the graph
     * is empty or not.
     * @return True if the graph is empty, i.e. no vertices are in this graph.
     */
    public boolean isEmpty(){
        return (state == -1);
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

        Node vertex = vertexListHead, edge;
        while (vertex != null) {
            edge = vertex.nextEdge;
            graph.append(vertex.vertex.getData()).append(" -> ");
            while (edge != null) {
                if (edge.edge != null) {
                    if (isWeighted) {
                        if (edge.nextEdge == null) {
                            graph.append(edge.vertex.getData());
                            graph.append("-").append(edge.edge.getWeight());
                        } else {
                            graph.append(edge.vertex.getData());
                            graph.append("-").append(edge.edge.getWeight());
                            graph.append(" ");
                        }
                    } else {
                        if (edge.nextEdge == null) {
                            graph.append(edge.vertex.getData());
                        } else {
                            graph.append(edge.vertex.getData());
                            graph.append(" ");
                        }
                    }
                }
                edge = edge.nextEdge;
            }
            graph.append("\n");
            vertex = vertex.nextVertex;
        }
        stream.println(graph.toString());
    }

/*************************************************************************
 *  Helper/private methods beyond this point                             *
 *************************************************************************/

    private boolean connectedToRoot(Node node){
        ArrayList<Node> visited = new ArrayList<>();
        boolean found = findNext(visited, node, vertexListHead);
        if (!found){
            node.vertex.setConnection(false);
            state++;
        }
        return found;
    }
    
    /**
     * Removes all edges going to node. Using the self reference reduces
     * redundant searches in lists that may not contain node.vertex
     *
     * @param node
     */
    private void removeAllEdgesTo(Node node) {

        Node current;
        current = node.nextEdge;
        while (current != null) {
            removeFrom(current.self, node);
            
            if (current.self.vertex.isConnected() && 
                    current.self != vertexListHead){
                
                if (!connectedToRoot(current.self)){
                    current.self.vertex.setConnection(false);
                    state++;
                }
            }
            current = current.nextEdge;
        }

    }

    /**
     * Places a child node in the edge list of the parent.
     *
     * @param parent The vertex from
     * @param child The vertex to
     */
    private boolean putLast(Node parent, Node child) {
        Node current = parent.nextEdge;

        if (current == null) {
            parent.nextEdge = child;
            return true;
        }
        Data r1, r2;
        r2 = child.vertex.getData();
        
        if (parent.vertex.getData().compareTo(r2) == 0){
            return false;
        }
        do {
            r1 = current.vertex.getData();
            
            if (r1.compareTo(r2) == 0 && child.edge == null) {
                break;
            } else {
                if (current.nextEdge == null) {
                    current.nextEdge = child;
                    return true;
                }
            }
            current = current.nextEdge;
        }while (current != null);

        return false;
    }

    /**
     * Performs what hasEdge does except hasEdge needs to find the vertices
     * first.
     *
     * @param parent The vertex from
     * @param child The vertex to
     * @return True if an edge exists between
     */
    private boolean hasChild(Node parent, Node child) {
        Node current = parent.nextEdge;
        
        if (current == null) return false;
        
        Data r1, r2;
        r2 = child.vertex.getData();
        do {
            r1 = current.vertex.getData();
            if (r1.compareTo(r2) == 0) {
                return current.edge != null;
            }
            current = current.nextEdge;
        }while (current != null);
        
        return false;
    }

    /**
     * Removes the edge going from the parent to the child.
     *
     * @param parent The vertex from
     * @param child The vertex to
     * @return True if the edge from parent to child was deleted
     */
    private boolean removeFrom(Node parent, Node child) {
        Node current = parent.nextEdge;
        Node prev = parent;
        
        if (current == null) return false;
        
        Data r1, r2;
        r2 = child.vertex.getData();
        do {
            r1 = current.vertex.getData();
            if (r1.compareTo(r2) == 0) {
                prev.nextEdge = current.nextEdge;
                current.nextEdge = null;
                numEdges--;
                dijkstrasApplied = false;
                return true;
            }
            prev = current;
            current = current.nextEdge;
        }while (current != null);

        return false;
    }

    /**
     * Sequentially searches and returns a node in the vertex list using a
     * String.
     *
     * @param v The vertex to search for
     * @return The vertex node object
     */
    private Node getVertexNode(Data v) {

        if (vertexListHead != null) {
            Node current = vertexListHead;
            while (current != null) {
                if (current.vertex.getData().compareTo(v) == 0) {
                    return current;
                }
                current = current.nextVertex;
            }
        }

        return null;
    }

    /**
     * The root-graph is the graph with the root vertex. The root vertex is the
     * vertex head of the internal data structure implementation for the vertex
     * list. In this case it is the head of a singly-linked list. The vert
     * vertex will be set to connected as well as all the adjacent vertices to
     * vert and all the other vertices that have a path to vert.
     *
     * @param vert This vertex is the parent vertex being
     * updated, any vertex with a path to vert will also be marked.
     */
    private void markAllConnected(Node vert){
        
        if (!vert.vertex.isConnected()){
            vert.vertex.setConnection(true);
            state--;
        }
        
        Node curVert = vert;
        
        Stack<Node> next = new Stack<>();
        Stack<Node> seen = new Stack<>();
        
        seen.push(vert);
        
        while(curVert != null && state > 0){
        
            Node edge = curVert.nextEdge;
            while(edge != null && state > 0){
                if (!edge.self.vertex.isConnected()){
                    edge.self.vertex.setConnection(true);
                    state--;
                }
                if (!seen.contains(edge.self)){
                    next.push(edge.self);
                }
                edge = edge.nextEdge;
            }

            if (!next.isEmpty()){
                seen.push(curVert);
                curVert = next.pop();
            }else{
                curVert = null;
            }
        
        }
    }
    
    /**
     * Walks the graph making sure not to visit vertices already visited. If
     * the algorithm can get from start to finish then this method will return
     * true. Performs depth-first search.
     *
     * @param visitedListTail The current position of the seen vertices list
     * @param start The start of the search
     * @param finish The vertex to find.
     * @return True if the vertex to find was found.
     */
    private boolean findNext(ArrayList<Node> visited, Node start, Node finish) {

        Node edge = start.nextEdge;
        boolean found = false;
        while (edge != null) {
            if (finish.vertex.getData().compareTo(edge.vertex.getData()) == 0) {
                return true;
            }
            if (!visited.contains(edge)) {
                visited.add(edge);
                found = findNext(visited, edge.self, finish);
                if (found) {
                    break;
                }
            }
            edge = edge.nextEdge;
        }
        return found;
    }

    /**
     * Performs similar to what findNext does but instead it tries to get back
     * to the starting vertex. This means that a circuit exists in the graph.
     * Otherwise if the algorithm can't get back to the start there is no
     * circuit in the graph. If the graph is disconnected then there might exist
     * a circuit but that depends on where the start vertex is. Performs 
     * depth-first search.
     *
     * @param listHead The head of the list of vertices visited
     * @param listTail The tail of the visited vertices list
     * @param start The start vertex
     * @param next The next vertex to visit
     * @return True if a circuit was found.
     */
    private boolean findCircuit(ArrayList<Node> visited, Node start, Node next) {

        Node edge = next.nextEdge;
        boolean found = false;
        Data r1 = start.vertex.getData(), r2;
        while (edge != null) {
            if (edge.edge == null) {
                edge = edge.nextEdge;
            } else {
                r2 = edge.vertex.getData();
                if (r1.compareTo(r2) == 0) {
                    found = true;
                    break;
                }
                if (!visited.contains(edge)) {
                    visited.add(edge);
                    found = findCircuit(visited, start, edge.self);
                    if (found) {
                        break;
                    }
                }
                edge = edge.nextEdge;
            }
        }
        return found;
    }
    
}
