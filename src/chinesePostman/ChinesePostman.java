package chinesePostman;

import m1graphs2024.Edge;
import m1graphs2024.UndirectedGraph;
import m1graphs2024.Node;

import java.util.*;

public class ChinesePostman {
    private final UndirectedGraph graph;
    // Count of original edges by key
    private Map<EdgeKey, Integer> originalEdgesCount = new HashMap<>();
    // Count of newly added edges by key
    private Map<EdgeKey, Integer> newlyAddedEdgesCount = new HashMap<>();

    public ChinesePostman(UndirectedGraph graph) {
        this.graph = graph;
        // Initialize originalEdgesCount
        for (Edge e : graph.getAllEdges()) {
            EdgeKey key = new EdgeKey(e.from().getId(), e.to().getId(), e.getWeight());
            originalEdgesCount.merge(key, 1, Integer::sum);
        }
    }

    // Get odd-degree nodes
    public List<Node> getOddDegreeNodes() {
        List<Node> oddNodes = new ArrayList<>();
        for (Node node : graph.getAllNodes()) {
            if (graph.degree(node) % 2 != 0) {
                oddNodes.add(node);
            }
        }
        return oddNodes;
    }

    // Determine graph type
    public String determineGraphType() {
        List<Node> oddNodes = getOddDegreeNodes();
        int oddCount = oddNodes.size();

        if (oddCount == 0) {
            return "Eulerian";
        } else if (oddCount == 2) {
            return "Semi-Eulerian";
        } else {
            return "Non-Eulerian";
        }
    }

    // Print degrees and odd nodes
    public void printGraphDetails() {
        System.out.println("Node Degrees:");
        for (Node node : graph.getAllNodes()) {
            System.out.println("Node " + node.getId() + ": Degree " + graph.degree(node));
        }

        List<Node> oddNodes = getOddDegreeNodes();
        System.out.println("Odd Degree Nodes: " + oddNodes.stream()
                .map(Node::getId)
                .toList());

        String graphType = determineGraphType();
        System.out.println("Graph Type: " + graphType);
    }

    // Get the lowest ID node
    public Node getLowestIdNode() {
        return graph.getAllNodes().stream()
                .min(Comparator.comparingInt(Node::getId))
                .orElse(null);
    }

    public Node getLowestIdNode(List<Node> nodes) {
        return nodes.stream()
                .min(Comparator.comparingInt(Node::getId))
                .orElse(null);
    }

    // Eulerian Circuit
    public List<String> computeEulerianCircuit(Node startNode) {
        Stack<Node> stack = new Stack<>();
        List<String> circuit = new ArrayList<>();
        Map<Edge, Boolean> visitedEdges = new HashMap<>();

        for (Edge edge : graph.getAllEdges()) {
            visitedEdges.put(edge, false);
        }

        stack.push(startNode);

        while (!stack.isEmpty()) {
            Node current = stack.peek();
            boolean foundUnvisitedEdge = false;

            for (Edge edge : graph.getOutEdges(current)) {
                if (!visitedEdges.get(edge)) {
                    visitedEdges.put(edge, true);
                    stack.push(edge.to().equals(current) ? edge.from() : edge.to());
                    foundUnvisitedEdge = true;
                    break;
                }
            }

            if (!foundUnvisitedEdge) {
                Node poppedNode = stack.pop();
                if (!stack.isEmpty()) {
                    Node previousNode = stack.peek();
                    Edge edge = findEdgeBetweenNodes(poppedNode, previousNode);
                    if (edge != null) {
                        circuit.add(poppedNode.getId() + "-(" + edge.getWeight() + ")-" + previousNode.getId());
                    }
                }
            }
        }

        return circuit;
    }

    // Eulerian Trail
    public List<String> computeEulerianTrail(Node startNode) {
        Stack<Node> stack = new Stack<>();
        List<String> trail = new ArrayList<>();
        Map<Edge, Boolean> visitedEdges = new HashMap<>();

        for (Edge edge : graph.getAllEdges()) {
            visitedEdges.put(edge, false);
        }

        stack.push(startNode);

        while (!stack.isEmpty()) {
            Node current = stack.peek();
            boolean foundUnvisitedEdge = false;

            for (Edge edge : graph.getOutEdges(current)) {
                if (!visitedEdges.get(edge)) {
                    visitedEdges.put(edge, true);
                    stack.push(edge.to().equals(current) ? edge.from() : edge.to());
                    foundUnvisitedEdge = true;
                    break;
                }
            }

            if (!foundUnvisitedEdge) {
                Node poppedNode = stack.pop();
                if (!stack.isEmpty()) {
                    Node previousNode = stack.peek();
                    Edge edge = findEdgeBetweenNodes(poppedNode, previousNode);
                    if (edge != null) {
                        trail.add(poppedNode.getId() + "-(" + edge.getWeight() + ")-" + previousNode.getId());
                    }
                }
            }
        }

        return trail;
    }

    private Edge findEdgeBetweenNodes(Node node1, Node node2) {
        for (Edge edge : graph.getAllEdges()) {
            if ((edge.from().equals(node1) && edge.to().equals(node2)) ||
                    (edge.from().equals(node2) && edge.to().equals(node1))) {
                return edge;
            }
        }
        return null;
    }

    // Handle graph type and compute solutions
    public void handleGraphType() {
        String graphType = determineGraphType();
        List<Node> oddNodes = getOddDegreeNodes();

        if ("Eulerian".equals(graphType)) {
            System.out.println("Eulerian Circuit:");
            computeEulerianCircuit(getLowestIdNode());
        } else if ("Semi-Eulerian".equals(graphType)) {
            System.out.println("Eulerian Trail:");
            computeEulerianTrail(getLowestIdNode(oddNodes));
        } else {
            System.out.println("Non-Eulerian Graph:");
            System.out.println("This case requires Chinese Postman logic.");
            computeChinesePostmanSolution();
        }
    }

    // Method to implement the Non-Eulerian solution (Chinese Postman)
    public void computeChinesePostmanSolution() {
        List<Node> oddNodes = getOddDegreeNodes();

        // Step 1: Run Floyd-Warshall to get shortest paths
        int n = graph.getAllNodes().size();
        // Map node ID to index for matrix representation
        Map<Integer, Integer> idToIndex = new HashMap<>();
        List<Node> allNodes = new ArrayList<>(graph.getAllNodes());
        allNodes.sort(Comparator.comparingInt(Node::getId));
        for (int i = 0; i < allNodes.size(); i++) {
            idToIndex.put(allNodes.get(i).getId(), i);
        }

        // Initialize M and Prec matrices
        int[][] M = new int[n][n];
        int[][] Prec = new int[n][n];

        final int INF = Integer.MAX_VALUE / 2; // to avoid overflow

        // Initialization for Floyd-Warshall
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                if (x == y) {
                    M[x][y] = 0;
                    Prec[x][y] = x;
                } else {
                    M[x][y] = INF;
                    Prec[x][y] = -1;
                }
            }
        }

        for (Edge edge : graph.getAllEdges()) {
            int i = idToIndex.get(edge.from().getId());
            int j = idToIndex.get(edge.to().getId());
            // Since undirected, set both directions
            if (edge.getWeight() < M[i][j]) {
                M[i][j] = edge.getWeight();
                M[j][i] = edge.getWeight();
                Prec[i][j] = i;
                Prec[j][i] = j;
            }
        }

        // Floyd-Warshall algorithm
        for (int z = 0; z < n; z++) {
            for (int x = 0; x < n; x++) {
                if (M[x][z] == INF) continue;
                for (int y = 0; y < n; y++) {
                    if (M[z][y] != INF && M[x][z] + M[z][y] < M[x][y]) {
                        M[x][y] = M[x][z] + M[z][y];
                        Prec[x][y] = Prec[z][y];
                    }
                }
            }
        }

        // Step 2: Generate all pairwise matchings of oddNodes
        oddNodes.sort(Comparator.comparingInt(Node::getId));
        List<List<Pair<Node, Node>>> allMatchings = new ArrayList<>();
        generatePairwiseMatchings(oddNodes, new ArrayList<>(), allMatchings);

        // Step 3: Find minimal-length matching
        int bestWeight = INF;
        List<Pair<Node, Node>> bestMatching = null;

        for (List<Pair<Node, Node>> matching : allMatchings) {
            int weight = 0;
            for (Pair<Node, Node> pair : matching) {
                int fromIndex = idToIndex.get(pair.getFirst().getId());
                int toIndex = idToIndex.get(pair.getSecond().getId());
                weight += M[fromIndex][toIndex];
                if (weight >= bestWeight) {
                    // No need to check further if we already exceed bestWeight
                    break;
                }
            }
            if (weight < bestWeight) {
                bestWeight = weight;
                bestMatching = matching;
            }
        }

        // Step 4: Duplicate edges along shortest paths for each pair in best matching
        // This will make the graph Eulerian
        if (bestMatching != null) {
            for (Pair<Node, Node> pair : bestMatching) {
                int u = idToIndex.get(pair.getFirst().getId());
                int v = idToIndex.get(pair.getSecond().getId());
                List<Node> path = reconstructPath(u, v, Prec, allNodes);

                for (int i = 0; i < path.size() - 1; i++) {
                    Node node1 = path.get(i);
                    Node node2 = path.get(i + 1);
                    Edge originalEdge = findEdgeBetweenNodes(node1, node2);
                    if (originalEdge != null) {
                        // Add a duplicate edge
                        graph.addEdge(node1, node2, originalEdge.getWeight());

                        // Record this newly added edge in newlyAddedEdgesCount
                        EdgeKey key = new EdgeKey(node1.getId(), node2.getId(), originalEdge.getWeight());
                        newlyAddedEdgesCount.merge(key, 1, Integer::sum);
                    } else {
                        System.err.println("Error: No existing edge found in shortest path, which should not happen.");
                    }
                }
            }
        }

        Node startNode = getLowestIdNode();
        List<String> circuit = computeEulerianCircuit(startNode);
        int totalLength = computeTotalLength(graph, circuit);

        System.out.println("Chinese Circuit: " + circuit);
        System.out.println("Extra cost: " + bestWeight);
        System.out.println("Total length: " + totalLength);

        String outputFileName = "output_nonEulerianGraph";
        // Pass originalEdgesCount and newlyAddedEdgesCount to toDotFile
        DotReaderWriter.toDotFile(graph, outputFileName, "Non-Eulerian", circuit, totalLength, bestWeight,
                originalEdgesCount, newlyAddedEdgesCount);
        System.out.println("Enriched DOT file written to: src/chinesePostman/graphTests/" + outputFileName + ".gv");
    }



    // Now graph is Eulerian


    /**
     * Generate all pairwise matchings of a list of nodes.
     * This recursive function tries to pick the first node and match it with each other node,
     * then recurses for the remaining nodes.
     */
    private void generatePairwiseMatchings(List<Node> oddNodes,
                                           List<Pair<Node, Node>> currentMatching,
                                           List<List<Pair<Node, Node>>> allMatchings) {
        if (oddNodes.isEmpty()) {
            allMatchings.add(new ArrayList<>(currentMatching));
            return;
        }
        // Take the first node
        Node first = oddNodes.get(0);
        // Try matching it with each other node
        for (int i = 1; i < oddNodes.size(); i++) {
            Node second = oddNodes.get(i);
            // Create pair and recurse
            List<Node> remaining = new ArrayList<>(oddNodes);
            remaining.remove(first);
            remaining.remove(second);

            currentMatching.add(new Pair<>(first, second));
            generatePairwiseMatchings(remaining, currentMatching, allMatchings);
            currentMatching.remove(currentMatching.size() - 1);
        }
    }

    /**
     * Reconstruct the shortest path between nodes x and y using the Prec matrix from Floyd-Warshall.
     * x and y are indices in the node list allNodes.
     */
    private List<Node> reconstructPath(int x, int y, int[][] Prec, List<Node> allNodes) {
        List<Node> path = new ArrayList<>();
        if (Prec[x][y] == -1) {
            return path; // no path
        }
        // Reconstruct path backwards
        Stack<Integer> stack = new Stack<>();
        stack.push(y);
        int current = y;
        while (current != x) {
            current = Prec[x][current];
            stack.push(current);
        }
        while (!stack.isEmpty()) {
            path.add(allNodes.get(stack.pop()));
        }
        return path;
    }

    // To calculate the total length of the circuit
    private static int computeTotalLength(UndirectedGraph graph, List<String> circuit) {
        int totalLength = 0;

        for (String edgeDescription : circuit) {
            String[] parts = edgeDescription.split("-\\(|\\)-");
            if (parts.length < 3) continue;

            int fromId = Integer.parseInt(parts[0]);
            int toId = Integer.parseInt(parts[2]);

            Node fromNode = graph.getNode(fromId);
            Node toNode = graph.getNode(toId);

            if (fromNode != null && toNode != null) {
                // Find the edge between the nodes
                Edge edge = graph.getAllEdges().stream()
                        .filter(e -> (e.from().equals(fromNode) && e.to().equals(toNode)) ||
                                (e.from().equals(toNode) && e.to().equals(fromNode)))
                        .findFirst()
                        .orElse(null);

                if (edge != null) {
                    totalLength += edge.getWeight();
                }
            }
        }

        return totalLength;
    }



}
