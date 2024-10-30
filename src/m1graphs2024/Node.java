package m1graphs2024;


import java.util.Objects;

public class Node implements Comparable<Node> {
    private  int id;
    private  String name; // Optional
    private  Graph graphHolder;


    public Node(int id){this.id =  id;}
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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
