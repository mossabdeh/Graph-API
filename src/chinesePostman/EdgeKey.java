package chinesePostman;

import java.util.Objects;

public class EdgeKey {
    private final int nodeA;
    private final int nodeB;
    private final int weight;

    //ensure that each undirected edge is stored only once
    public EdgeKey(int id1, int id2, int weight) {
        // Ensure nodeA < nodeB for uniqueness
        this.nodeA = Math.min(id1, id2);
        this.nodeB = Math.max(id1, id2);
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof EdgeKey)) return false;
        EdgeKey other = (EdgeKey) obj;
        return this.nodeA == other.nodeA && this.nodeB == other.nodeB && this.weight == other.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeA, nodeB, weight);
    }
}
