package chinesePostman;

import m1graphs2024.Edge;
import m1graphs2024.UndirectedGraph;
import m1graphs2024.Node;

import java.util.*;

public class ChinesePostman {
    private final UndirectedGraph graph;
    private final Set<Pair<Integer, Integer>> duplicatedEdges = new HashSet<>();

    public ChinesePostman(UndirectedGraph graph) {
        this.graph = graph;
    }

    public Map<Pair<Node, Node>, Integer> distMap;
    public Map<Pair<Node, Node>, Node> precMap;

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

    // Compute Eulerian Circuit
    public List<String> computeEulerianCircuit(Node startNode) {
        Stack<Node> stack = new Stack<>();
        List<String> circuit = new ArrayList<>();
        Map<Edge, Boolean> visitedEdges = new HashMap<>();

        // Initialize all edges as unvisited
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

    // Compute Eulerian Trail
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



    // Reconstruct shortest path from 'from' to 'to' using predecessor information
    private List<Node> reconstructPath(Node from, Node to) {
        List<Node> path = new ArrayList<>();

        if (distMap.get(new Pair<>(from, to)) == Integer.MAX_VALUE) {
            // No path
            return path;
        }

        Node current = to;
        path.add(current);

        while (!current.equals(from)) {
            Node predecessor = precMap.get(new Pair<>(from, current));
            if (predecessor == null || predecessor.equals(current)) {
                throw new IllegalStateException("Invalid predecessor chain from " + from.getId() + " to " + to.getId());
            }
            path.add(predecessor);
            current = predecessor;
        }

        Collections.reverse(path);

        // Additional check: no consecutive identical nodes
        for (int i = 0; i < path.size() - 1; i++) {
            if (path.get(i).equals(path.get(i + 1))) {
                throw new IllegalStateException("Consecutive identical nodes in path: " + path);
            }
        }

        return path;
    }



    private List<Edge> duplicateShortestPath(Pair<Node, Node> pair) {
        List<Edge> addedEdges = new ArrayList<>();
        Node from = pair.getFirst();
        Node to = pair.getSecond();

        List<Node> path = reconstructPath(from, to);
        if (path.isEmpty()) {
            throw new IllegalStateException("No shortest path found between " + from.getId() + " and " + to.getId());
        }

        // Duplicate edges along the path
        for (int i = 0; i < path.size() - 1; i++) {
            Node current = path.get(i);
            Node nxt = path.get(i + 1);

            if (current.equals(nxt)) {
                throw new IllegalStateException("Invalid path step: " + current.getId() + " -> " + nxt.getId());
            }

            Edge edge = findEdgeBetweenNodes(current, nxt);
            if (edge == null) {
                throw new IllegalStateException("No original edge found between " + current.getId() + " and " + nxt.getId());
            }

            int id1 = current.getId();
            int id2 = nxt.getId();
            Pair<Integer, Integer> edgeId = new Pair<>(Math.min(id1, id2), Math.max(id1, id2));

            if (!duplicatedEdges.contains(edgeId)) {
                graph.addEdge(current, nxt, edge.getWeight());
                addedEdges.add(edge);
                duplicatedEdges.add(edgeId);
            }
        }

        return addedEdges;
    }


    // Compute the total length of a given circuit
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


    private Edge findEdgeBetweenNodes(Node node1, Node node2) {
        for (Edge edge : graph.getAllEdges()) {
            if ((edge.from().equals(node1) && edge.to().equals(node2)) ||
                    (edge.from().equals(node2) && edge.to().equals(node1))) {
                return edge;
            }
        }
        return null;
    }

    // Floyd-Warshall
    public void floydWarshall() {
        List<Node> nodes = graph.getAllNodes();
        distMap = new HashMap<>();
        precMap = new HashMap<>();

        // Initialization
        for (Node x : nodes) {
            for (Node y : nodes) {
                if (x.equals(y)) {
                    distMap.put(new Pair<>(x, y), 0);
                    precMap.put(new Pair<>(x, y), null);
                } else {
                    Edge edge = findEdgeBetweenNodes(x, y);
                    if (edge != null) {
                        distMap.put(new Pair<>(x, y), edge.getWeight());
                        precMap.put(new Pair<>(x, y), x);
                    } else {
                        distMap.put(new Pair<>(x, y), Integer.MAX_VALUE);
                        precMap.put(new Pair<>(x, y), null);
                    }
                }
            }
        }

        // Floyd-Warshall core loops
        for (Node k : nodes) {
            for (Node i : nodes) {
                for (Node j : nodes) {
                    int distIK = distMap.get(new Pair<>(i, k));
                    int distKJ = distMap.get(new Pair<>(k, j));
                    int distIJ = distMap.get(new Pair<>(i, j));

                    if (distIK != Integer.MAX_VALUE && distKJ != Integer.MAX_VALUE && distIK + distKJ < distIJ) {
                        distMap.put(new Pair<>(i, j), distIK + distKJ);
                        // Update predecessor of j on the best path from i
                        precMap.put(new Pair<>(i, j), precMap.get(new Pair<>(k, j)));
                    }
                }
            }
        }
    }


    // Minimal pair matching (optimal)
    public List<Pair<Node, Node>> findMinimalLengthMatching(List<Node> oddNodes) {
        List<Pair<Node, Node>> bestMatching = new ArrayList<>();
        int bestMatchingWeight = Integer.MAX_VALUE;

        List<List<Pair<Node, Node>>> allMatchings = generateAllMatchings(oddNodes);
        for (List<Pair<Node, Node>> matching : allMatchings) {
            int matchingWeight = 0;
            for (Pair<Node, Node> p : matching) {
                // Get distance from distMap using the global field
                int dist = distMap.get(new Pair<>(p.getFirst(), p.getSecond()));
                if (dist == Integer.MAX_VALUE) {
                    // No path between these two nodes, skip or consider infinite cost
                    matchingWeight = Integer.MAX_VALUE;
                    break;
                } else {
                    matchingWeight += dist;
                }
            }

            if (matchingWeight < bestMatchingWeight) {
                bestMatchingWeight = matchingWeight;
                bestMatching = matching;
            }
        }

        return bestMatching;
    }

    private List<List<Pair<Node, Node>>> generateAllMatchings(List<Node> oddNodes) {
        List<List<Pair<Node, Node>>> matchings = new ArrayList<>();
        generatePairs(oddNodes, new ArrayList<>(), matchings);
        return matchings;
    }

    private void generatePairs(List<Node> nodes, List<Pair<Node, Node>> currentMatching, List<List<Pair<Node, Node>>> matchings) {
        if (nodes.isEmpty()) {
            matchings.add(new ArrayList<>(currentMatching));
            return;
        }

        Node firstNode = nodes.get(0);
        for (int i = 1; i < nodes.size(); i++) {
            Node secondNode = nodes.get(i);
            currentMatching.add(new Pair<>(firstNode, secondNode));

            List<Node> remainingNodes = new ArrayList<>(nodes);
            remainingNodes.remove(firstNode);
            remainingNodes.remove(secondNode);

            generatePairs(remainingNodes, currentMatching, matchings);
            currentMatching.remove(currentMatching.size() - 1);
        }
    }

    // Random Pairwise Matching (Non-Optimal)
    public List<Pair<Node, Node>> findRandomMatching(List<Node> oddNodes) {
        List<Pair<Node, Node>> matching = new ArrayList<>();
        Collections.shuffle(oddNodes);

        while (oddNodes.size() >= 2) {
            Node first = oddNodes.remove(0);
            Node second = oddNodes.remove(0);
            matching.add(new Pair<>(first, second));
        }

        return matching;
    }



    public static void main(String[] args) {
        String graphFileName = "nonEulerianGraph"; // Adjust if needed
        UndirectedGraph graph = DotReaderWriter.fromDotFile(graphFileName);

        if (graph == null || graph.getAllNodes().isEmpty()) {
            System.err.println("Error: Failed to load graph or the graph is empty.");
            return;
        }

        ChinesePostman chinesePostman = new ChinesePostman(graph);

        System.out.println("=== Graph Details ===");
        chinesePostman.printGraphDetails();

        System.out.println("=== Handling Graph Type ===");
        String graphType = chinesePostman.determineGraphType();
        List<String> circuit = new ArrayList<>();
        int totalLength = 0;
        Integer extraCost = null;
        List<Edge> addedEdges = new ArrayList<>();

        switch (graphType) {
            case "Eulerian":
                System.out.println("Computing Eulerian Circuit...");
                Node startNodeEulerian = chinesePostman.getLowestIdNode();
                circuit = chinesePostman.computeEulerianCircuit(startNodeEulerian);
                totalLength = computeTotalLength(graph, circuit);
                break;

            case "Semi-Eulerian":
                System.out.println("Computing Eulerian Trail...");
                Node startNodeTrail = chinesePostman.getLowestIdNode(chinesePostman.getOddDegreeNodes());
                circuit = chinesePostman.computeEulerianTrail(startNodeTrail);
                totalLength = computeTotalLength(graph, circuit);
                break;

            case "Non-Eulerian":
                System.out.println("Non-Eulerian Graph: Computing Chinese Circuit...");

                // Run Floyd-Warshall to fill distMap and precMap fields in the ChinesePostman instance
                chinesePostman.floydWarshall();

                // Get odd degree nodes
                List<Node> oddNodes = chinesePostman.getOddDegreeNodes();

                // Compute minimal-length matching using distMap (no need to pass distances now)
                List<Pair<Node, Node>> matching = chinesePostman.findMinimalLengthMatching(oddNodes);

                System.out.println("Pairwise Matching: " + matching);

                // Duplicate shortest paths for each matched pair
                for (Pair<Node, Node> pair : matching) {
                    addedEdges.addAll(chinesePostman.duplicateShortestPath(pair));
                }

                // Now all nodes are of even degree, we can compute the Eulerian circuit
                Node startNodeChinese = chinesePostman.getLowestIdNode();
                circuit = chinesePostman.computeEulerianCircuit(startNodeChinese);
                totalLength = computeTotalLength(graph, circuit);
                extraCost = addedEdges.stream().mapToInt(Edge::getWeight).sum();
                break;


            default:
                System.err.println("Error: Unknown graph type.");
                return;
        }

        String outputFileName = "output_" + graphFileName;
        DotReaderWriter.toDotFile(graph, outputFileName, graphType, circuit, totalLength, extraCost, addedEdges);

        System.out.println("Enriched DOT file written to: src/chinesePostman/graphTests/" + outputFileName + ".gv");
    }

}
