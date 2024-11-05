package m1graphs2024;

import java.util.Objects;

public class Edge implements Comparable<Edge>{
    private final Node from; /* Source Node */
    private final Node to;  /* target Node */
    private final Integer weight; /* weight of the  Node  (null = no weight)
    */


    /* Constructor no weight version node references */
    public Edge(Node from , Node to){
       if (from == null || to == null) {
            throw new IllegalArgumentException("Source Node & Target Node must not be nulls.");
        }
       if (!from.getGraph().equals(to.getGraph())) {
            throw new IllegalArgumentException("Source & Target Node must be in the same Graph.");
       }
        this.from = from;
        this.to = to;
        this.weight = null; /* to ensure there's its unweighted graph*/
    }

    /* Overloaded Constructor no weight version node ids */
 /*   public Edge(int fromId, int toId, Graph graph) {
        Node fromNode = graph.getNode(fromId);
        Node toNode = graph.getNode(toId);
        System.out.println("fromNode: "+fromNode);
        System.out.println("toNode: "+toNode);
        if (fromNode == null) {
            throw new IllegalArgumentException("Node with ID " + fromId + " does not exist in the graph.");
        }
        if (toNode == null) {
            throw new IllegalArgumentException("Node with ID " + toId + " ALI trich does not exist in the graph.");
        }

        this.from = fromNode;
        this.to = toNode;
        this.weight = null; // Assuming weight is optional; set to a default if necessary
    }*/

    public Edge(int fromId, int toId, Graph graph) {
        // Check and add 'from' node if it doesn't exist
        Node fromNode = graph.getNode(fromId);
        if (fromNode == null) {
            graph.addNode(fromId);
            fromNode = graph.getNode(fromId); // Retrieve the newly added node
        }

        // Check and add 'to' node if it doesn't exist
        Node toNode = graph.getNode(toId);
        if (toNode == null) {
            graph.addNode(toId);
            toNode = graph.getNode(toId); // Retrieve the newly added node
        }

        // Assign the validated nodes to the edge
        this.from = fromNode;
        this.to = toNode;
        this.weight = null; // Assuming unweighted by default
    }
    /* Overloaded Constructor with weight version node ids */
    public Edge(int fromId, int toId, Graph graph, Integer weight) {
        Node fromNode = graph.getNode(fromId);
        Node toNode = graph.getNode(toId);
        if (fromNode == null || toNode == null || fromNode.getGraph() != toNode.getGraph()) {
            throw new IllegalArgumentException("Nodes must be non-null and belong to the same graph");
        }
        this.from = fromNode;
        this.to = toNode;
        this.weight = weight;
    }

    /* Constructor with weight version node references */
    public Edge(Node from , Node to, Integer weight) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Source Node & Target Node must not be nulls.");
        }
        if (!from.getGraph().equals(to.getGraph())) {
            throw new IllegalArgumentException("Source & Target Node must be in the same Graph.");
        }
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /* This methode should apply  only for Edges in the same Graph */
    @Override
    public int compareTo(Edge o) {
        /* Sort By source Node here */
        if (this.from.getId() > o.from.getId()) {
            return 1;
        } else if (this.from.getId() < o.from.getId()) {
            return -1;
        }
           /* If the Source Node the same
           *  Sort by the target Node
           * */
        if (this.to.getId() > o.to.getId()) {
            return 1;
        } else if (this.to.getId() < o.to.getId()) {
            return -1;
        }
          /* If target and source are the same
          * Sort by the weight
          *  */
        if (this.weight != null && o.weight != null) { /* check if weight in both objects are not null */
            return this.weight.compareTo(o.weight);
        }

        /* I did not check for
         this.weight is Non-Null, o.weight is Null   or
         this.weight is Null, o.weight is Non-Null

         because  it's a weighted graph or not weighted graph
         */

        return 0;
    }

    /* Redefining equals() to compare two edges based on their source, target and weight */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from.equals(edge.from) &&
                to.equals(edge.to) &&
                Objects.equals(weight, edge.weight); // Safe null handling for weight
    }


    /* Redefining hashCode() to guarantee consistency with equals() */
    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);}

    public Node to() {
        return to;
    }

    public Node from() {
        return from;}

    public Edge getSymmetric(){
        return new Edge(to,from,weight);
    }

   public boolean isSelfLoop(){
        return from.equals(to);
    }

   public boolean isMultiEdge(){
        return from.getGraph().adjEdList.get(from).stream().filter(e -> e.to.equals(to)).count() > 1;
    }

    public boolean isWeighted(){
        return weight != null;
    }

    /* Getting the weight of an edge (or null in the unweighted case) */
    public Integer getWeight(){
        return isWeighted() ? weight : null;}


    @Override
    public String toString() {
        return from.getId() + "->" + to.getId();
    }

}
