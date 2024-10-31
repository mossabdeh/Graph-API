package m1graphs2024;

import java.util.Objects;

public class Edge implements Comparable<Edge>{
    private Node from; /* Source Node */
    private Node to;  /* target Node */
    private Integer weight; /* weight of the  Node  (null = no weight)
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
        this.weight = null; /* to ensure theres its unweighted graph*/
    }

    /* Constructor no weight version node ids */
    public Edge(Integer from , Integer to){
        if (from == null || to == null) {
            throw new IllegalArgumentException("Source Node & Target Node must not be nulls.");
        }
        //TODO: Check if the nodes exist in the same graph
        this.from = new Node(from,null);
        this.to = new Node(to,null);
        this.weight = null; /* to ensure theres its unweighted graph*/
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

         because  its a weighted graph or not weighted graph
         */

        return 0;
    }

    /* Redefining equals() to compare two edges based on their source, target and weight */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return from.equals(edge.from) && to.equals(edge.to) && weight.equals(edge.weight);
    }

    /* Redefining hashCode() to guarantee consistency with equals() */
    @Override
    public int hashCode() {
        return Objects.hash(from, to, weight);}

    public Node getTo() {
        return to;
    }

    public Node getFrom() {
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

    public Integer getWeight(){
        return weight;}


}
