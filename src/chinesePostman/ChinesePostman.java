package chinesePostman;

import m1graphs2024.Edge;
import m1graphs2024.UndirectedGraph;
import m1graphs2024.Node;

import java.util.*;

public class ChinesePostman {
    private final UndirectedGraph graph;

    public ChinesePostman(UndirectedGraph graph) {
        this.graph = graph;
    }

    // Get odd-degree nodes
    public List<Node> getOddDegreeNodes() {
        List<Node> oddNodes = new ArrayList<>();
        for (Node node : graph.getAllNodes()) { // Assuming getNodes() returns all nodes in the graph
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

    // Eulerian Circuit (stub)
    public List<String> computeEulerianCircuit(Node startNode) {
        Stack<Node> stack = new Stack<>();
        List<String> circuit = new ArrayList<>();
        Map<Edge, Boolean> visitedEdges = new HashMap<>();

        // Initialize all edges as unvisited
        for (Edge edge : graph.getAllEdges()) {
            visitedEdges.put(edge, false);
        }

        // Start traversal
        stack.push(startNode);

        while (!stack.isEmpty()) {
            Node current = stack.peek();
            boolean foundUnvisitedEdge = false;

            // Explore unvisited edges
            for (Edge edge : graph.getOutEdges(current)) {
                if (!visitedEdges.get(edge)) {
                    visitedEdges.put(edge, true); // Mark edge as visited
                    stack.push(edge.to().equals(current) ? edge.from() : edge.to()); // Move to the next node
                    foundUnvisitedEdge = true;
                    break;
                }
            }

            // If no unvisited edges, backtrack and add the edge to the circuit
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

        return circuit; // No need to reverse as the order is already correct
    }




    // Eulerian Trail (stub)
    public List<String> computeEulerianTrail(Node startNode) {
        Stack<Node> stack = new Stack<>();
        List<String> trail = new ArrayList<>();
        Map<Edge, Boolean> visitedEdges = new HashMap<>();

        // Initialize all edges as unvisited
        for (Edge edge : graph.getAllEdges()) {
            visitedEdges.put(edge, false);
        }

        // Start traversal from the odd-degree node
        stack.push(startNode);

        while (!stack.isEmpty()) {
            Node current = stack.peek();
            boolean foundUnvisitedEdge = false;

            // Explore unvisited edges
            for (Edge edge : graph.getOutEdges(current)) {
                if (!visitedEdges.get(edge)) {
                    visitedEdges.put(edge, true); // Mark edge as visited
                    stack.push(edge.to().equals(current) ? edge.from() : edge.to()); // Move to the next node
                    foundUnvisitedEdge = true;
                    break;
                }
            }

            // If no unvisited edges, backtrack and add the edge to the trail
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

        return trail; // No need to reverse as the order is already correct
    }



    private Edge findEdgeBetweenNodes(Node node1, Node node2) {
        for (Edge edge : graph.getAllEdges()) {
            if ((edge.from().equals(node1) && edge.to().equals(node2)) ||
                    (edge.from().equals(node2) && edge.to().equals(node1))) {
                return edge;
            }
        }
        return null; // Return null if no edge is found
    }


    // Handle graph type and compute circuits
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
            System.out.println("This case requires Chinese Circuit logic.");
            // Add logic for Chinese Circuit computation here
        }
    }


    // To calculate the total length of the circuit using the findEdgeBetweenNodes method
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




    public static void main(String[] args) {
        // Step 1: Load the graph using DotReaderWriter
        String graphFileName = "eulerianGraph"; // Replace with your input file name (without extension)
        UndirectedGraph graph = DotReaderWriter.fromDotFile(graphFileName);

        if (graph == null || graph.getAllNodes().isEmpty()) {
            System.err.println("Error: Failed to load graph or the graph is empty.");
            return;
        }

        // Step 2: Create an instance of ChinesePostman
        ChinesePostman chinesePostman = new ChinesePostman(graph);

        // Step 3: Print graph details
        System.out.println("=== Graph Details ===");
        chinesePostman.printGraphDetails();

        // Step 4: Determine graph type and compute circuits
        System.out.println("=== Handling Graph Type ===");
        String graphType = chinesePostman.determineGraphType();
        List<String> circuit = new ArrayList<>();
        int totalLength = 0;
        Integer extraCost = null;

        if ("Eulerian".equals(graphType)) {
            System.out.println("Computing Eulerian Circuit...");
            Node startNode = chinesePostman.getLowestIdNode();
            circuit = chinesePostman.computeEulerianCircuit(startNode);
            totalLength = computeTotalLength(graph, circuit);
        } else if ("Semi-Eulerian".equals(graphType)) {
            System.out.println("Computing Eulerian Trail...");
            Node startNode = chinesePostman.getLowestIdNode(chinesePostman.getOddDegreeNodes());
            circuit = chinesePostman.computeEulerianTrail(startNode);
            totalLength = computeTotalLength(graph, circuit);
        }


        // Step 5: Write enriched graph to a DOT file
        String outputFileName = "output_" + graphFileName;
        DotReaderWriter.toDotFile(graph, outputFileName, graphType, circuit, totalLength, extraCost);

        System.out.println("Enriched DOT file written to: src/chinesePostman/graphTests/" + outputFileName + ".gv");
    }






}
