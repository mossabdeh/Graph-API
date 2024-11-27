package m1graphs2024;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/* Undirected graphs */
public class UndirectedGraph extends Graph{


    public UndirectedGraph(int... SA) {
        adjEdList = new HashMap<>(); // Initialize the adjacency list to store nodes with their edge lists
        int currentNodeId = 1; // Start with Node 1 by default
        Node currentNode = new Node(currentNodeId, this); // Create the first node
        adjEdList.put(currentNode, new ArrayList<>()); // Initialize adjacency list for the first node (edges)

        for (int i = 0; i < SA.length - 1; i++) {
            int valueSA = SA[i];

            if (valueSA == 0) { // Move to the next node if we find 0
                currentNodeId++;
                currentNode = new Node(currentNodeId, this); // Create the new node
                adjEdList.put(currentNode, new ArrayList<>()); // Initialize adjacency list for the new node
            } else {
                // Find or create the target node based on valueSA
                Node targetNode = adjEdList.keySet().stream()
                        .filter(node -> node.getId() == valueSA) // Check if a node with the same ID already exists
                        .findFirst()
                        .orElseGet(() -> { // If not, create a new node
                            Node newNode = new Node(valueSA, this);
                            adjEdList.put(newNode, new ArrayList<>()); // Initialize adjacency list for the target node
                            return newNode;
                        });
                Edge edge1 = new Edge(currentNode, targetNode);
                // Add the edges to the adjacency lists
                adjEdList.get(currentNode).add(edge1);

            }
        }
    }





    @Override
    public int nbEdges(){/* undirected graph*/
        return adjEdList.values().stream().mapToInt(List::size).sum();
    }

    @Override
    public int degree(Node n) {
        return n.getIncidentEdges().size(); // Total edges connected to the node
    }

    @Override
    public int degree(int id) {
        Node n = getNode(id);
        return degree(n);
    }

    // For an undirected graph, in-degree and out-degree are effectively the same as degree.
    @Override
    public int inDegree(Node n) {
        return degree(n); // In an undirected graph, degree is the same for in/out
    }

    @Override
    public int inDegree(int id) {
        Node n = getNode(id);
        return inDegree(n);
    }

    @Override
    public int outDegree(Node n) {
        return degree(n); // In an undirected graph, degree is the same for in/out
    }

    @Override
    public int outDegree(int id) {
        Node n = getNode(id);
        return outDegree(n);
    }

    @Override
    public List<Edge> getIncidentEdges(Node n) {
        // Get all edges in the graph
        List<Edge> allEdges = getAllEdges();
        List<Edge> incidentEdges = new ArrayList<>();

        // Iterate over all edges to find those connected to the node
        for (Edge edge : allEdges) {
            if (edge.from().equals(n) && edge.to().equals(n)) {
                // Handle self-loops: Add only once per occurrence
                incidentEdges.add(edge);
            } else if (edge.from().equals(n) || edge.to().equals(n)) {
                // Handle edges where the node is either the source or destination
                incidentEdges.add(edge);
            }
        }
        return incidentEdges;
    }


    @Override
    public List<Edge> getIncidentEdges(int id) {
        Node n = getNode(id);
        return getIncidentEdges(n);
    }

    @Override
    public List<Node> getSuccessors(Node n) {
        if (!holdsNode(n)) {
            return Collections.emptyList(); // Return an empty list if the node is not in the graph
        }

        Set<Node> neighbors = new HashSet<>(); // Use a set to ensure unique neighbors
        boolean hasSelfLoop = false;

        // Iterate over all edges
        for (Edge edge : getAllEdges()) {
            if (edge.from().equals(n) && edge.to().equals(n)) {
                hasSelfLoop = true; // Mark self-loop
            }
            if (edge.from().equals(n)) {
                neighbors.add(edge.to());
            }
            if (edge.to().equals(n)) {
                neighbors.add(edge.from());
            }
        }

        // Include the node itself if it has a self-loop
        if (hasSelfLoop) {
            neighbors.add(n);
        }

        return new ArrayList<>(neighbors);
    }






    @Override
    public List<Node> getSuccessors(int id) {
        Node n = getNode(id);
        return getSuccessors(n);
    }

    @Override
    public List<Node> getSuccessorsMulti(Node n) {
        if (!holdsNode(n)) {
            return Collections.emptyList(); // Return an empty list if the node is not in the graph
        }

        List<Node> successors = new ArrayList<>();

        // Iterate over all edges
        for (Edge edge : getAllEdges()) {
            if (edge.from().equals(n)) {
                // Add the "to" node for edges originating from the given node
                successors.add(edge.to());
            } else if (edge.to().equals(n) && !edge.from().equals(n)) {
                // Add the "from" node only if it's not a self-loop
                successors.add(edge.from());
            }
        }

        return successors;
    }





    @Override
    public List<Edge> getAllEdges() {
        List<Edge> allEdges = new ArrayList<>();

        for (Node from : adjEdList.keySet()) {
            allEdges.addAll(adjEdList.get(from));
        }

        return allEdges;
    }


    @Override
    public List<Node> getAllNodes() {
        // Return all nodes present in the adjacency list
        return new ArrayList<>(this.adjEdList.keySet());
    }


    @Override
    public List<Edge> getInEdges(Node n) {
        return getIncidentEdges(n); // In an undirected graph, in-edges are the same as incident edges
    }

    @Override
    public List<Edge> getOutEdges(Node n) {
        return getIncidentEdges(n); // In an undirected graph, out-edges are the same as incident edges
    }


    @Override
    public UndirectedGraph getReverse() {
        // For an undirected graph, the reverse is the same as the original graph
        UndirectedGraph reverseGraph = new UndirectedGraph();

        // Add all nodes to the reverse graph
        for (Node node : this.adjEdList.keySet()) {
            reverseGraph.addNode(new Node(node.getId(), reverseGraph)); // Create new Node instances
        }

        // Add all edges to the reverse graph
        for (Edge edge : getAllEdges()) {
            reverseGraph.addEdge(edge.from().getId(), edge.to().getId()); // Add edge as-is (no reversing needed)
        }

        return reverseGraph;
    }



    @Override
    public UndirectedGraph getTransitiveClosure() {
        // Step 1: Create a new graph to hold the transitive closure
        UndirectedGraph transitiveClosure = new UndirectedGraph();
        transitiveClosure.adjEdList = new HashMap<>(); // Initialize adjEdList to prevent NullPointerException

        // Step 2: Copy all nodes from the original graph to the transitive closure graph
        for (Node node : adjEdList.keySet()) {
            transitiveClosure.addNode(node.getId());
        }

        // Step 3: Add initial edges from the original graph
        for (Edge edge : getAllEdges()) {
            Node from = edge.from();
            Node to = edge.to();
            if (!from.equals(to) && !transitiveClosure.existsEdge(from, to)) {
                transitiveClosure.addEdge(from, to); // Add edge
                transitiveClosure.addEdge(to, from); // Add reverse for undirected graph
            }
        }

        // Step 4: Compute the transitive closure
        List<Node> allNodes = new ArrayList<>(transitiveClosure.getAllNodes());
        for (Node intermediate : allNodes) {
            for (Node source : allNodes) {
                for (Node target : allNodes) {
                    // Check if source is reachable from target through intermediate
                    if (source != target &&
                            (transitiveClosure.existsEdge(source, intermediate) &&
                                    transitiveClosure.existsEdge(intermediate, target))) {
                        // Add bidirectional edges to ensure symmetry
                        if (!transitiveClosure.existsEdge(source, target)) {
                            transitiveClosure.addEdge(source, target);
                            transitiveClosure.addEdge(target, source); // Add reverse for undirected graph
                        }
                    }
                }
            }
        }

        // Step 5: Remove duplicate edges in the final graph
        UndirectedGraph resultGraph = new UndirectedGraph();
        resultGraph.adjEdList = new HashMap<>();
        for (Node node : transitiveClosure.getAllNodes()) {
            resultGraph.addNode(node.getId());
        }
        Set<String> seenEdges = new HashSet<>();
        for (Edge edge : transitiveClosure.getAllEdges()) {
            Node from = edge.from();
            Node to = edge.to();

            // Ensure that only one direction of an edge is added
            String edgeKey = Math.min(from.getId(), to.getId()) + "-" + Math.max(from.getId(), to.getId());
            if (!seenEdges.contains(edgeKey)) {
                resultGraph.addEdge(from, to);
                seenEdges.add(edgeKey);
            }
        }

        return resultGraph;
    }










    @Override
    public UndirectedGraph toSimpleGraph() {
        UndirectedGraph simpleGraph = new UndirectedGraph();
        simpleGraph = this.copy();
        for (Node node : simpleGraph.getAllNodes()) {
            for (Node successor : simpleGraph.getSuccessors(node)) {
                if (simpleGraph.getSuccessors(successor).contains(node)) {
                    simpleGraph.removeEdge(successor, node);
                }
            }
        }
        return simpleGraph;
    }

    @Override
    public UndirectedGraph copy() {
        UndirectedGraph copyGraph = new UndirectedGraph();
        for (Node node : this.getAllNodes()) {
            copyGraph.addNode(node);
        }
        for (Node node : this.getAllNodes()) {
            for (Node successor : this.getSuccessors(node)) {
                copyGraph.addEdge(node, successor);
            }
        }
        return copyGraph;
    }


    public static UndirectedGraph fromDotFile(String filename) {
        return fromDotFile(filename, ".gv");
    }


    public static UndirectedGraph fromDotFile(String filename, String extension) {
        UndirectedGraph graph = new UndirectedGraph();
        String filePath = "src/m1graphs2024/graphTests/" + filename + extension;

        // Pattern to match undirected edges and isolated nodes
        Pattern undirectedPattern = Pattern.compile("(\\d+) -- (\\d+)( \\[label=(\\d+), len=(\\d+)\\])?");
        Pattern isolatedNodePattern = Pattern.compile("^(\\d+);?$");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                Matcher undirectedMatcher = undirectedPattern.matcher(line);
                Matcher isolatedNodeMatcher = isolatedNodePattern.matcher(line);

                // Handle isolated nodes
                if (isolatedNodeMatcher.matches()) {
                    int nodeId = Integer.parseInt(isolatedNodeMatcher.group(1));
                    Node isolatedNode = new Node(nodeId, graph);
                    graph.addNode(isolatedNode); // Add isolated node without any edges

                } else if (undirectedMatcher.matches()) { // Handle undirected edges
                    int fromId = Integer.parseInt(undirectedMatcher.group(1));
                    int toId = Integer.parseInt(undirectedMatcher.group(2));
                    Integer weight = undirectedMatcher.group(4) != null ? Integer.parseInt(undirectedMatcher.group(4)) : null;

                    Node from = new Node(fromId, graph);
                    Node to = new Node(toId, graph);

                    graph.addNode(from);
                    graph.addNode(to);

                    // Add the edge in both directions for undirected connectivity
                    if (weight != null) {
                        graph.addEdge(from, to, weight);
                        graph.addEdge(to, from, weight);
                    } else {
                        graph.addEdge(from, to);
                        graph.addEdge(to, from);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the DOT file: " + e.getMessage());
        }

        // Sort nodes by ID
        List<Node> sortedNodes = new ArrayList<>(graph.adjEdList.keySet());
        Collections.sort(sortedNodes, Comparator.comparingInt(Node::getId));

        // Sort edges for each node by the target node's ID
        for (Node node : sortedNodes) {
            List<Edge> edges = graph.getOutEdges(node);
            Collections.sort(edges, Comparator.comparingInt(edge -> edge.to().getId()));
            graph.adjEdList.put(node, edges); // Ensure sorted edges in adjEdList
        }

        return graph;
    }

    @Override
    public String toDotString() {
        StringBuilder dotBuilder = new StringBuilder("graph G {\n");
        dotBuilder.append("    rankdir=LR;\n");

        // Sort nodes by their natural order
        List<Node> sortedNodes = new ArrayList<>(adjEdList.keySet());
        Collections.sort(sortedNodes);

        // Iterate through each node and append edges or the node itself in sorted order
        for (Node node : sortedNodes) {
            List<Edge> sortedEdges = new ArrayList<>(adjEdList.get(node));
            Collections.sort(sortedEdges); // Sort edges by target node ID

            if (!sortedEdges.isEmpty()) {
                // Append each edge for this node, allowing duplicates and self-loops
                for (Edge edge : sortedEdges) {
                    Node to = edge.to();

                    dotBuilder.append("    ")
                            .append(node.getId())
                            .append(" -- ")
                            .append(to.getId());

                    // Check if the edge has a weight, and if so, add label and len attributes
                    if (edge.getWeight() != null) {
                        int weight = edge.getWeight();
                        dotBuilder.append(" [label=")
                                .append(weight)
                                .append(", len=")
                                .append(weight)
                                .append("]");
                    }

                    dotBuilder.append(";\n");
                }
            } else if (getInEdges(node).isEmpty() && getOutEdges(node).isEmpty()) {
                // Append node as isolated if it has no incoming or outgoing edges
                dotBuilder.append("    ").append(node.getId()).append(";\n");
            }
        }

        dotBuilder.append("}\n");
        return dotBuilder.toString();
    }



    @Override
    public int[] toSuccessorArray() {
        List<Integer> successorArray = new ArrayList<>();
        List<Edge> edges = new ArrayList<>(getAllEdges()); // Get all edges

        // Sort nodes for consistent order
        List<Node> sortedNodes = new ArrayList<>(adjEdList.keySet());
        Collections.sort(sortedNodes);

        for (Node node : sortedNodes) {
            Iterator<Edge> iterator = edges.iterator(); // Use an iterator to safely remove edges

            while (iterator.hasNext()) {
                Edge edge = iterator.next();
                if (edge.from().equals(node)) {
                    // Add the "to" node to the successor array
                    successorArray.add(edge.to().getId());
                    iterator.remove(); // Remove the edge after processing
                }
            }

            successorArray.add(0); // Add 0 to signify the end of this node's successors
        }

        return successorArray.stream().mapToInt(Integer::intValue).toArray(); // Convert list to array
    }



    @Override
    public int[][] toAdjMatrix() {
        int n = nbNodes(); // Total number of nodes
        int[][] adjMatrix = new int[n][n]; // Initialize adjacency matrix with zeros

        // Get all edges
        List<Edge> allEdges = getAllEdges();

        for (Edge edge : allEdges) {
            int from = edge.from().getId() - 1; // Convert to 0-based index
            int to = edge.to().getId() - 1; // Convert to 0-based index

            if (from == to) {
                // Self-loop: Increment diagonal entry
                adjMatrix[from][to] += 1;
            } else {
                // Regular edge: Increment both directions for undirected graph
                adjMatrix[from][to] += 1;
                adjMatrix[to][from] += 1;
            }
        }

        return adjMatrix;
    }





    @Override
    public List<Node> getDFS() {
        List<Node> visited = new ArrayList<>();
        Stack<Node> stack = new Stack<>();

        for (Node start : adjEdList.keySet()) {
            if (!visited.contains(start)) {
                stack.push(start);

                while (!stack.isEmpty()) {
                    Node current = stack.pop();

                    if (!visited.contains(current)) {
                        visited.add(current);

                        List<Node> successors = getSuccessors(current);
                        successors.sort(Comparator.comparingInt(Node::getId).reversed());
                        for (Node successor : successors) {
                            if (!visited.contains(successor)) {
                                stack.push(successor);
                            }
                        }
                    }
                }
            }
        }

        return visited;
    }


    @Override
    public List<Node> getBFS() {
        if (adjEdList.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Node> visited = new HashSet<>();
        List<Node> result = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();

        // Iterate through all nodes in the graph to handle disconnected components
        for (Node start : adjEdList.keySet()) {
            if (!visited.contains(start)) {
                queue.add(start);

                while (!queue.isEmpty()) {
                    Node current = queue.poll();

                    if (visited.add(current)) { // Add to visited if it hasn't been visited
                        result.add(current); // Add to result

                        // Get successors, sort them, and add unvisited nodes to the queue
                        List<Node> successors = getSuccessors(current);
                        successors.sort(Comparator.comparingInt(Node::getId)); // Sort successors by ID
                        for (Node successor : successors) {
                            if (!visited.contains(successor)) {
                                queue.add(successor);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }






}
