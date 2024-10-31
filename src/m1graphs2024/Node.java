package m1graphs2024;


import java.util.*;

public class Node implements Comparable<Node> {
    private  int id;
    private  String name; // Optional
    private  Graph graphHolder;



    /*  Constructor takes both the id  and the graph holder as parameters */
    public Node(int id, Graph graphHolder) {
        this.id = id;
        this.name = null; // to ensure the name is null (We can remove it )
        this.graphHolder = graphHolder; }


    /*  Constructor takes  the id, the name and the graph holder as parameters */
    public Node(int id, String name, Graph graphHolder) {
        this.id = id;
        this.name = name;
        this.graphHolder = graphHolder; }


    /* getter for id For better encapsulation */
    public int getId() {
        return id; }

    /* getter for Graph for better encapsulation */
    public Graph getGraph() {
        return graphHolder; }

    /* getter for name For better encapsulation */
    public String getName() {
        return name; }

    /* Implementation of Comparable Interface to compare Nodes */
    @Override
    public int compareTo(Node o) {
        if (this.getId() == o.getId()) {
            return 0;
        } else if (this.getId() > o.getId()) {
            return 1;
        }
        return -1;
    }

    /* Redefining equals() to compare two nodes based on their ID */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return id == node.id;
    }

    /* Redefining hashCode() to guarantee consistency with equals() */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // TODO Check if the method is correct
   public List<Node> getSuccessors(){
        /* to handle duplicates */
        Set<Node> uniqueSuccessors = new HashSet<>();
        /* Retrieve edges from the Adj List */
        List<Edge> edges = graphHolder.adjEdList.get(this);
        if (edges != null) { /* Check if it has edges(not isolated node) */
            for (Edge edge : edges) { /* Loop through the edges */
                uniqueSuccessors.add(edge.getTo()); /* Add target node (successor) to the set */
            }
        }
        /* Return the successors as a List */
        return new ArrayList<>(uniqueSuccessors);
    }

    // TODO Check if the method is correct
    public List<Node> getSuccessorsMulti() {

        /* to handle duplicates */
        List<Node> MultiSuccessors = new ArrayList<>();
        /* Retrieve edges from the Adj List */
        List<Edge> edges = graphHolder.adjEdList.get(this);
        if (edges != null) { /* Check if it has edges(not isolated node) */
            for (Edge edge : edges) { /* Loop through the edges */
                MultiSuccessors.add(edge.getTo()); /* Add target node (successor) to the set */
            }
        }
        /* Return the successors as a List */
        return MultiSuccessors;
    }

    // TODO Overload with node id ( another methode)
    public boolean adjacent(Node u) {
        return graphHolder.adjEdList.get(this).stream().anyMatch(edge -> edge.getTo().equals(u));
    }

   public int inDegree(){
        return (int) graphHolder.adjEdList.values().stream()
                .flatMap(List::stream)
                .filter(edge -> edge.getTo().equals(this))
                .count();
    }

   public int outDegree(){
        return graphHolder.adjEdList.get(this).size();
    }

    // TODO fix it for multi graph
    public List<Edge> getOutEdges(){
        return graphHolder.adjEdList.get(this);
    }

    public List<Edge> getInEdges(){
        return graphHolder.adjEdList.values().stream()
                .flatMap(List::stream)
                .filter(edge -> edge.getTo().equals(this))
                .toList();
    }


    // TODO fix it for undirected graph
    public List<Edge> getIncidentEdges(){
        List<Edge> incidentEdges = new ArrayList<>();
        incidentEdges.addAll(getInEdges());
        incidentEdges.addAll(getOutEdges());
        return incidentEdges;
    }

    public List<Edge>  getEdgesTo(Node u){
        return graphHolder.adjEdList.get(this).stream()
                .filter(edge -> edge.getTo().equals(u))
                .toList();
    }

}
