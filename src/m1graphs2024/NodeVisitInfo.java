package m1graphs2024;

public class NodeVisitInfo {



    private NodeColour colour;

    private  Node predecessor ;
    private Integer discovery;

    private Integer finished;

    static int time = 0;

    public NodeVisitInfo() {

        this.colour = NodeColour.WHITE;
        this.predecessor = null;
        this.discovery = null;
        this.finished = null;
    }

    public NodeVisitInfo(NodeColour colour, Node predecessor, Integer discovery, Integer finished) {
        this.colour = colour;
        this.predecessor = predecessor;
        this.discovery = discovery;
        this.finished = finished;
    }

    /* ------------------- Getters and Setters ------------------- */

    public NodeColour getColour() {
        return colour;
    }

    public void setColour(NodeColour colour) {
        this.colour = colour;
    }

    public Node getPredecessor() {
        return predecessor;
    }

    public void setPredecessor(Node predecessor) {
        this.predecessor = predecessor;
    }

    public Integer getDiscovery() {
        return discovery;
    }

    public void setDiscovery(Integer discovery) {
        this.discovery = discovery;
    }

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

}
