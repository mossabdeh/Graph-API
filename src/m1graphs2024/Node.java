package m1graphs2024;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a node in a graph with an associated ID, name (optional), and a reference to the graph it belongs to.
 * Each node can have successors, incident edges, and degrees calculated based on its connections in the graph.
 */
public class Node implements Comparable<Node> {
    private final int id;
    private final String name; // Optional name for the node
    private final Graph graphHolder; // Reference to the graph that holds this node

    /**
     * Constructs a Node with a specified ID and its containing graph.
     *
     * @param id           the unique ID of the node
     * @param graphHolder  the graph to which this node belongs
     */
    public Node(int id, Graph graphHolder) {
        this.id = id;
        this.name = null;
        this.graphHolder = graphHolder;
    }

    /**
     * Constructs a Node with a specified ID, name, and containing graph.
     *
     * @param id           the unique ID of the node
     * @param name         the name of the node (optional)
     * @param graphHolder  the graph to which this node belongs
     */
    public Node(int id, String name, Graph graphHolder) {
        this.id = id;
        this.name = name;
        this.graphHolder = graphHolder;
    }

    /**
     * Returns the ID of the node.
     *
     * @return the node's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the graph to which this node belongs.
     *
     * @return the graph containing this node
     */
    public Graph getGraph() {
        return this.graphHolder;
    }

    /**
     * Returns the name of the node, if set.
     *
     * @return the name of the node, or null if no name is set
     */
    public String getName() {
        return name;
    }

    /**
     * Compares this node to another node based on their IDs.
     *
     * @param o the other node to compare to
     * @return 0 if IDs are equal, 1 if this ID is greater, -1 otherwise
     */
    @Override
    public int compareTo(Node o) {
        return Integer.compare(this.getId(), o.getId());
    }

    /**
     * Checks equality based on the node ID.
     *
     * @param o the object to compare with
     * @return true if the IDs are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    /**
     * Generates a hash code based on the node ID.
     *
     * @return the hash code for this node
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Retrieves the unique successors (nodes directly reachable from this node).
     *
     * @return a list of unique successor nodes
     */
    public List<Node> getSuccessors() {
        Set<Node> uniqueSuccessors = new HashSet<>();
        List<Edge> edges = graphHolder.adjEdList.get(this);
        if (edges != null) {
            for (Edge edge : edges) {
                uniqueSuccessors.add(edge.to());
            }
        }
        return new ArrayList<>(uniqueSuccessors);
    }

    /**
     * Retrieves all successors, allowing for multiple occurrences if there are multiple edges to the same node.
     *
     * @return a list of successor nodes, including duplicates for multi-edges
     */
    public List<Node> getSuccessorsMulti() {
        List<Node> multiSuccessors = new ArrayList<>();
        List<Edge> edges = graphHolder.adjEdList.get(this);
        if (edges != null) {
            for (Edge edge : edges) {
                multiSuccessors.add(edge.to());
            }
        }
        return multiSuccessors;
    }

    /**
     * Checks if a specified node is adjacent (directly connected) to this node.
     *
     * @param u the node to check adjacency with
     * @return true if adjacent, false otherwise
     */
    public boolean adjacent(Node u) {
        return graphHolder.adjEdList.get(this).stream().anyMatch(edge -> edge.to().equals(u));
    }

    /**
     * Checks if a node with a specified ID is adjacent to this node.
     *
     * @param u the ID of the node to check adjacency with
     * @return true if adjacent, false otherwise
     */
    public boolean adjacent(int u) {
        return graphHolder.adjEdList.get(this).stream().anyMatch(edge -> edge.to().getId() == u);
    }

    /**
     * Calculates the in-degree (number of incoming edges) of this node.
     *
     * @return the in-degree of this node
     */
    public int inDegree() {
        return (int) graphHolder.adjEdList.values().stream()
                .flatMap(List::stream)
                .filter(edge -> edge.to().equals(this))
                .count();
    }

    /**
     * Calculates the out-degree (number of outgoing edges) of this node.
     *
     * @return the out-degree of this node
     */
    public int outDegree() {
        return graphHolder.adjEdList.get(this).size();
    }

    /**
     * Calculates the degree (total number of edges) of this node.
     *
     * @return the degree of this node
     */
    public int degree() {
        return inDegree() + outDegree();
    }

    /**
     * Retrieves all outgoing edges from this node.
     *
     * @return a list of outgoing edges
     */
    public List<Edge> getOutEdges() {
        return graphHolder.adjEdList.get(this);
    }

    /**
     * Retrieves all incoming edges to this node.
     *
     * @return a list of incoming edges
     */
    public List<Edge> getInEdges() {
        return graphHolder.adjEdList.values().stream()
                .flatMap(List::stream)
                .filter(edge -> edge.to().equals(this))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves all incident edges (both incoming and outgoing) for this node.
     *
     * @return a list of incident edges
     */
    public List<Edge> getIncidentEdges() {
        List<Edge> incidentEdges = new ArrayList<>();
        incidentEdges.addAll(getInEdges());
        incidentEdges.addAll(getOutEdges());
        return incidentEdges;
    }

    /**
     * Retrieves all edges from this node to a specified target node.
     *
     * @param u the target node
     * @return a list of edges from this node to the target node
     */
    public List<Edge> getEdgesTo(Node u) {
        return Optional.ofNullable(getOutEdges())
                .orElse(List.of())
                .stream()
                .filter(edge -> edge.to().equals(u))
                .toList();
    }

    /**
     * Provides a string representation of the node, showing its ID.
     *
     * @return the string representation of the node ID
     */
    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
