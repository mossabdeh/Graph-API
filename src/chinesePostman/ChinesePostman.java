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



    // Method to implement the Non-Eulerian solution (Chinese Postman)
    public void computeChinesePostmanSolution(String matchingAlgorithm) {
        List<Node> oddNodes = getOddDegreeNodes();

        // Step 1: Run Floyd-Warshall to get shortest paths
        int n = graph.getAllNodes().size();
        Map<Integer, Integer> idToIndex = new HashMap<>();
        List<Node> allNodes = new ArrayList<>(graph.getAllNodes());
        allNodes.sort(Comparator.comparingInt(Node::getId));
        for (int i = 0; i < allNodes.size(); i++) {
            idToIndex.put(allNodes.get(i).getId(), i);
        }

        int[][] M = new int[n][n];
        int[][] Prec = new int[n][n];

        final int INF = Integer.MAX_VALUE / 2;

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

        // Step 2: Prepare odd nodes
        oddNodes.sort(Comparator.comparingInt(Node::getId));

        // Step 3: Find minimal-length matching using chosen algorithm
        // Step 3: Find minimal-length matching based on chosen algorithm
        List<Pair<Node, Node>> bestMatching = switch (matchingAlgorithm.toLowerCase()) {
            case "enumeration" -> minimalLengthPairwiseMatchingByEnumeration(oddNodes, M, idToIndex);
            case "greedy" -> minimalLengthPairwiseMatchingByGreedy(oddNodes, M, idToIndex);
            case "random" -> minimalLengthPairwiseMatchingByRandom(oddNodes, M, idToIndex);
            default -> {
                System.err.println("Unknown algorithm. Defaulting to enumeration.");
                yield minimalLengthPairwiseMatchingByEnumeration(oddNodes, M, idToIndex);
            }
        };

        int bestWeight = 0;
        for (Pair<Node, Node> pair : bestMatching) {
            int fromIndex = idToIndex.get(pair.getFirst().getId());
            int toIndex = idToIndex.get(pair.getSecond().getId());
            bestWeight += M[fromIndex][toIndex];
        }

        // Step 4: Duplicate edges along shortest paths for each pair in best matching
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
                        graph.addEdge(node1, node2, originalEdge.getWeight());
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
        System.out.println("Total length: " + (totalLength+bestWeight));

        String outputFileName = "output_nonEulerianGraph";
        DotReaderWriter.toDotFile(graph, outputFileName, "Non-Eulerian", circuit, totalLength+bestWeight, bestWeight,
                originalEdgesCount, newlyAddedEdgesCount);
        System.out.println("Enriched DOT file written to: src/chinesePostman/graphTests/" + outputFileName + ".gv");
    }





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



    /**
     * Minimal-length Pairwise Matching by Enumeration
     *
     * This method uses the enumeration of all possible perfect matchings of odd-degree nodes
     * to find the one with the minimal total weight.
     *
     * @param oddNodes the list of odd-degree nodes
     * @param M the shortest-distance matrix from Floyd-Warshall
     * @param idToIndex map from node ID to matrix index
     * @return the best matching (list of node pairs) with minimal length
     */
    private List<Pair<Node, Node>> minimalLengthPairwiseMatchingByEnumeration(List<Node> oddNodes, int[][] M, Map<Integer, Integer> idToIndex) {
        List<List<Pair<Node, Node>>> allMatchings = new ArrayList<>();
        generatePairwiseMatchings(oddNodes, new ArrayList<>(), allMatchings);

        int bestWeight = Integer.MAX_VALUE / 2;
        List<Pair<Node, Node>> bestMatching = null;

        for (List<Pair<Node, Node>> matching : allMatchings) {
            int weight = 0;
            for (Pair<Node, Node> pair : matching) {
                int fromIndex = idToIndex.get(pair.getFirst().getId());
                int toIndex = idToIndex.get(pair.getSecond().getId());
                weight += M[fromIndex][toIndex];
                if (weight >= bestWeight) {
                    // No need to continue if we already exceed current best
                    break;
                }
            }
            if (weight < bestWeight) {
                bestWeight = weight;
                bestMatching = matching;
            }
        }

        return bestMatching != null ? bestMatching : new ArrayList<>();
    }


    /**
     * A simple greedy approach:
     * 1. List all pairs of odd nodes and their distances.
     * 2. Sort pairs by distance ascending.
     * 3. Iteratively pick the shortest pair whose nodes are not yet matched.
     *
     * This will not guarantee minimality but provides a distinct solution from enumeration.
     */
    private List<Pair<Node, Node>> minimalLengthPairwiseMatchingByGreedy(List<Node> oddNodes, int[][] M, Map<Integer, Integer> idToIndex) {
        List<Node> nodes = new ArrayList<>(oddNodes);
        Set<Node> unmatched = new HashSet<>(nodes);

        // Generate all possible pairs
        List<Pair<Node, Node>> allPairs = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Node a = nodes.get(i);
                Node b = nodes.get(j);
                int dist = M[idToIndex.get(a.getId())][idToIndex.get(b.getId())];
                allPairs.add(new Pair<>(a, b));
            }
        }

        // Sort pairs by their distance
        allPairs.sort((p1, p2) -> {
            int d1 = M[idToIndex.get(p1.getFirst().getId())][idToIndex.get(p1.getSecond().getId())];
            int d2 = M[idToIndex.get(p2.getFirst().getId())][idToIndex.get(p2.getSecond().getId())];
            return Integer.compare(d1, d2);
        });

        // Pick the shortest pair that doesn't conflict
        List<Pair<Node, Node>> result = new ArrayList<>();
        for (Pair<Node, Node> pair : allPairs) {
            if (unmatched.contains(pair.getFirst()) && unmatched.contains(pair.getSecond())) {
                // Use this pair
                result.add(pair);
                unmatched.remove(pair.getFirst());
                unmatched.remove(pair.getSecond());
                if (unmatched.isEmpty()) break;
            }
        }

        return result;
    }


    /**
     * Picks a random perfect matching from all possible matchings.
     * This ensures a non-minimal solution is likely.
     */
    private List<Pair<Node, Node>> minimalLengthPairwiseMatchingByRandom(List<Node> oddNodes, int[][] M, Map<Integer, Integer> idToIndex) {
        List<List<Pair<Node, Node>>> allMatchings = new ArrayList<>();
        generatePairwiseMatchings(oddNodes, new ArrayList<>(), allMatchings);

        if (allMatchings.isEmpty()) {
            return new ArrayList<>();
        }

        // Randomly pick one matching from all possible matchings (not necessarily minimal)
        Random rand = new Random();
        return allMatchings.get(rand.nextInt(allMatchings.size()));
    }




}
