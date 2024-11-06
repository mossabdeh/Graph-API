package m1graphs2024;

import java.util.Objects;

/**
 * Represents an edge in a graph, connecting a source node (from) to a target node (to) with an optional weight.
 * Edges can be directed or undirected based on graph structure, and the weight indicates the edge cost or distance if provided.
 */
public class Edge implements Comparable<Edge> {
    private final Node from; // Source node of the edge
    private final Node to;   // Target node of the edge
    private final Integer weight; // Optional weight for the edge (null = unweighted)

    /**
     * Constructs an unweighted edge between two nodes.
     *
     * @param from the source node
     * @param to   the target node
     */
    public Edge(Node from, Node to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Source Node & Target Node must not be null.");
        }
        if (!from.getGraph().equals(to.getGraph())) {
            throw new IllegalArgumentException("Source & Target Node must be in the same graph.");
        }
        this.from = from;
        this.to = to;
        this.weight = null; // Default to unweighted
    }

    /**
     * Constructs an unweighted edge between two nodes, identified by their IDs in the graph.
     *
     * @param fromId the ID of the source node
     * @param toId   the ID of the target node
     * @param graph  the graph containing the nodes
     */
    public Edge(int fromId, int toId, Graph graph) {
        Node fromNode = graph.getNode(fromId);
        if (fromNode == null) {
            graph.addNode(fromId);
            fromNode = graph.getNode(fromId);
        }

        Node toNode = graph.getNode(toId);
        if (toNode == null) {
            graph.addNode(toId);
            toNode = graph.getNode(toId);
        }

        this.from = fromNode;
        this.to = toNode;
        this.weight = null;
    }

    /**
     * Constructs a weighted edge between two nodes, identified by their IDs in the graph.
     *
     * @param fromId the ID of the source node
     * @param toId   the ID of the target node
     * @param graph  the graph containing the nodes
     * @param weight the weight of the edge
     */
    public Edge(int fromId, int toId, Graph graph, Integer weight) {
        Node fromNode = graph.getNode(fromId);
        Node toNode = graph.getNode(toId);
        if (fromNode == null || toNode == null || !fromNode.getGraph().equals(toNode.getGraph())) {
            throw new IllegalArgumentException("Nodes must be non-null and belong to the same graph.");
        }
        this.from = fromNode;
        this.to = toNode;
        this.weight = weight;
    }

    /**
     * Constructs a weighted edge between two nodes.
     *
     * @param from   the source node
     * @param to     the target node
     * @param weight the weight of the edge
     */
    public Edge(Node from, Node to, Integer weight) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Source Node & Target Node must not be null.");
        }
        if (!from.getGraph().equals(to.getGraph())) {
            throw new IllegalArgumentException("Source & Target Node must be in the same graph.");
        }
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * Compares this edge to another edge, first by source node, then by target node, and finally by weight.
     *
     * @param o the edge to compare to
     * @return 0 if equal, 1 if greater, -1 if less than the other edge
     */
    @Override
    public int compareTo(Edge o) {
        int fromComparison = Integer.compare(this.from.getId(), o.from.getId());
        if (fromComparison != 0) return fromComparison;

        int toComparison = Integer.compare(this.to.getId(), o.to.getId());
        if (toComparison != 0) return toComparison;

        return (this.weight != null && o.weight != null) ? this.weight.compareTo(o.weight) : 0;
    }

    /**
     * Checks if two edges are equal based on source, target, and weight.
     *
     * @param o the object to compare to
     * @return true if edges are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from.equals(edge.from) &&
                to.equals(edge.to) &&
                Objects.equals(weight, edge.weight);
    }

    /**
     * Computes a hash code for the edge, based on source, target, and weight.
     *
     * @return hash code of the edge
     */
    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);
    }

    /**
     * Retrieves the target node of the edge.
     *
     * @return the target node
     */
    public Node to() {
        return to;
    }

    /**
     * Retrieves the source node of the edge.
     *
     * @return the source node
     */
    public Node from() {
        return from;
    }

    /**
     * Returns a symmetric version of the edge, swapping the source and target nodes.
     *
     * @return the symmetric edge
     */
    public Edge getSymmetric() {
        return new Edge(to, from, weight);
    }

    /**
     * Checks if the edge is a self-loop (i.e., the source and target nodes are the same).
     *
     * @return true if it is a self-loop, false otherwise
     */
    public boolean isSelfLoop() {
        return from.equals(to);
    }

    /**
     * Checks if the edge is a multi-edge (i.e., there are multiple edges between the same two nodes).
     *
     * @return true if it is a multi-edge, false otherwise
     */
    public boolean isMultiEdge() {
        return from.getGraph().adjEdList.get(from).stream()
                .filter(e -> e.to.equals(to))
                .count() > 1;
    }

    /**
     * Checks if the edge is weighted.
     *
     * @return true if the edge has a weight, false otherwise
     */
    public boolean isWeighted() {
        return weight != null;
    }

    /**
     * Retrieves the weight of the edge.
     *
     * @return the weight of the edge, or null if unweighted
     */
    public Integer getWeight() {
        return isWeighted() ? weight : null;
    }

    /**
     * Provides a string representation of the edge in the format "from->to".
     *
     * @return the string representation of the edge
     */
    @Override
    public String toString() {
        return from.getId() + "->" + to.getId();
    }
}
