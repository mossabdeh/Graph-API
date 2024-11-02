package m1graphs2024;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Graphs are directed by default */
public class Graph {

    Map<Node, List<Edge>> adjEdList;


    public Graph() {/* Empty Constructor */
    }

    /* Constructor using Successor Array SA for unweighted graph
    * to Change the parameter to int... SA for unspecified number of integers
    * */
    public Graph(int... SA) { /* int[] SA  for array*/
        adjEdList = new HashMap<>(); /* Init the adjEdList to Store Nodes with their Edge list*/
        int currentNodeId = 1; // Start with Node 1 by default
        Node currentNode = new Node(currentNodeId, this); // Create the first node
        adjEdList.put(currentNode, new ArrayList<>()); // Initialize adjacency list for the first node (edges)


        for (int valueSA : SA) {/* Loop in the SA array */
            if (valueSA == 0) {/* if we found 0 move to next node */
                currentNodeId++;
                currentNode = new Node(currentNodeId, this); /* create the new Node */
                adjEdList.put(currentNode, new ArrayList<>()); // Initialize adjacency list for the new node
            } else {
                // Find or create the target node based on valueSA
                Node targetNode = adjEdList.keySet().stream()
                        .filter(node -> node.getId() == valueSA) /* Check if a node with the same ID already exists*/
                        .findFirst()
                        .orElseGet(() -> { /* If not create a new Node */
                            Node newNode = new Node(valueSA, this);
                            adjEdList.put(newNode, new ArrayList<>()); // Initialize adjacency list for the target node
                            return newNode;
                        });

                /* Create an edge from the current node to the target node */
                Edge edge = new Edge(currentNode, targetNode);
                /*  Add this edge to the current node's adjacency list */
                adjEdList.get(currentNode).add(edge);
            }
        }
    }


    /* ------------- Methods related to the nodes. ------------- */


    public int nbNodes(){
        return this.adjEdList.size();
    }

    //TODO implement the method with id + check
    public boolean usesNode(Node n){
        return this.adjEdList.keySet().stream().anyMatch(node -> node.getId() == n.getId());
    }

    public boolean holdsNode(Node n) {
        // Check if `n` has the same `graphHolder` as `this`
        // and if there's already a node with the same `id` in `adjEdList`
        return n.getGraph() == this && adjEdList.keySet().stream().anyMatch(node -> node.getId() == n.getId());
    }

    public Node getNode(int id) {
        // Search for a node with the specified id in the adjacency list keys
        return this.adjEdList.keySet().stream()
                .filter(node -> node.getId() == id)
                .findFirst()
                .orElse(null);
    }

    //TODO Overload with id as parameter
    public boolean addNode(Node n) {
        // Check if a node with the same id already exists
        boolean nodeExists = adjEdList.keySet().stream().anyMatch(node -> node.getId() == n.getId());

        if (nodeExists) {
            return false; // Node with the same id already exists, so don't add it
        }

        // Add the node to the adjacency list with an empty list of edges
        adjEdList.put(n, new ArrayList<>());
        return true; // Node was added successfully
    }


    //TODO Overload with id as parameter
    public boolean removeNode(Node n) {
        // Check if the node exists in the adjacency list
        if (!adjEdList.containsKey(n)) {
            return false; // Node does not exist, so return false
        }

        // Remove the node from the adjacency list
        adjEdList.remove(n);

        // Remove any edges in other nodes' lists that point to `n`
        for (List<Edge> edges : adjEdList.values()) {
            edges.removeIf(edge -> edge.to().equals(n));
        }

        return true; // Node and all its incident edges were removed
    }

    public List<Node> getAllNodes() {
        return new ArrayList<>(this.adjEdList.keySet());
    }

    public int largestNodeId() {
        return adjEdList.keySet().stream()
                .mapToInt(Node::getId)
                .max()
                .orElse(Integer.MIN_VALUE); // Return Integer.MIN_VALUE if no nodes exist
    }

    public int smallestNodeId() {
        return adjEdList.keySet().stream()
                .mapToInt(Node::getId)
                .min()
                .orElse(Integer.MAX_VALUE); // Return Integer.MAX_VALUE if no nodes exist
    }


    //TODO overload with node id as parameter
    public List<Node> getSuccessors(Node n) {
        // Check if the node exists in the graph
        if (!adjEdList.containsKey(n)) {
            return Collections.emptyList(); // Return an empty list if node `n` is not in the graph
        }

        // Use a Set to store successors without duplicates
        Set<Node> uniqueSuccessors = new HashSet<>();

        // Retrieve all edges from `n` and add target nodes to the set
        for (Edge edge : adjEdList.get(n)) {
            uniqueSuccessors.add(edge.to());
        }

        // Convert the set to a list and return it
        return new ArrayList<>(uniqueSuccessors);
    }

    //TODO overload with node id as parameter & (Collections.emptyList()) check
    public List<Node> getSuccessorsMulti(Node n) {
        // Check if the node exists in the graph
        if (!adjEdList.containsKey(n)) {
            return Collections.emptyList(); // Return an empty list if node `n` is not in the graph
        }

        // Create a list to store successors, allowing duplicates
        List<Node> successorsWithDuplicates = new ArrayList<>();

        // Retrieve all edges from `n` and add target nodes to the list
        for (Edge edge : adjEdList.get(n)) {
            successorsWithDuplicates.add(edge.to());
        }

        // Return the list with possible duplicates
        return successorsWithDuplicates;
    }


    //TODO overload with node id as parameter
    public boolean adjacent(Node u, Node v) {
        // Check if `u` or `v` are not in the graph
        if (!adjEdList.containsKey(u) || !adjEdList.containsKey(v)) {
            return false; // If either node is missing, they cannot be adjacent
        }

        // Check if there is an edge from `u` to `v`
        boolean hasEdgeFromUToV = adjEdList.get(u).stream().anyMatch(edge -> edge.to().equals(v));

        // Check if there is an edge from `v` to `u` (for undirected behavior)
        boolean hasEdgeFromVToU = adjEdList.get(v).stream().anyMatch(edge -> edge.to().equals(u));

        // Return true if either condition is true
        return hasEdgeFromUToV || hasEdgeFromVToU;
    }

    //TODO overload with node id as parameter
    public int inDegree(Node n) {
        int inDegreeCount = 0;
        // Check if the node exists in the graph
        if (!adjEdList.containsKey(n)) {
            return inDegreeCount; // Return 0 if the node is not in the graph
        }
        // Iterate over each node's edges in the graph
        for (List<Edge> edges : adjEdList.values()) {
            for (Edge edge : edges) {
                if (edge.to().equals(n)) {
                    inDegreeCount++; // Increment count if an edge points to `n`
                }
            }
        }
        return inDegreeCount;
    }

    //TODO overload with node id as parameter
    public int outDegree(Node n) {
        // Check if the node exists in the graph
        if (!adjEdList.containsKey(n)) {
            return 0; // Return 0 if the node is not in the graph
        }
        // Out-degree is the number of outgoing edges from `n`
        return adjEdList.get(n).size();
    }

    //TODO overload with node id as parameter
    public int degree(Node n) {
        return inDegree(n) + outDegree(n);
    }


    /* ------------- Methods related to the edges. ------------- */

    //TODO implement for undirected graph /2
    public int nbEdges(){/* directed graph*/
        return adjEdList.values().stream().mapToInt(List::size).sum();
    }

    //TODO overload 2 times
    public boolean existsEdge(Node u, Node v) {
        // Check if both nodes exist in the graph
        if (!adjEdList.containsKey(u) || !adjEdList.containsKey(v)) {
            return false; // If either node is missing, no edge can exist
        }
        // Check if there is an edge from `u` to `v`
        boolean edgeFromUToV = adjEdList.get(u).stream().anyMatch(edge -> edge.to().equals(v));
        // Check if there is an edge from `v` to `u` (for undirected behavior)
        boolean edgeFromVToU = adjEdList.get(v).stream().anyMatch(edge -> edge.to().equals(u));
        // Return true if either direction has an edge
        return edgeFromUToV || edgeFromVToU;
    }

    //TODO overload 2 times
    public boolean isMultiEdge(Node u, Node v) {
        // First, check if there is at least one edge between u and v
        if (!existsEdge(u, v)) {
            return false; // No edge exists between u and v, so it's not a multi-edge
        }
        // Count the number of edges from u to v
        long count = this.adjEdList.get(u).stream().filter(edge -> edge.to().equals(v)).count();
        // If there is more than one edge from u to v, it's a multi-edge
        return count > 1;
    }

    //TODO overload 2 times
    public void addEdge(Node from, Node to) {
        // Ensure the `from` node is in the graph
        if (!adjEdList.containsKey(from)) {
            adjEdList.put(from, new ArrayList<>()); // Add `from` with an empty adjacency list
        }
        // Ensure the `to` node is in the graph
        if (!adjEdList.containsKey(to)) {
            adjEdList.put(to, new ArrayList<>()); // Add `to` with an empty adjacency list
        }
        // Add an edge from `from` to `to`
        adjEdList.get(from).add(new Edge(from, to));
    }

    public void addEdge(Node from, Node to, Integer weight) {
        // Ensure the `from` node is in the graph
        if (!adjEdList.containsKey(from)) {
            adjEdList.put(from, new ArrayList<>()); // Add `from` with an empty adjacency list
        }
        // Ensure the `to` node is in the graph
        if (!adjEdList.containsKey(to)) {
            adjEdList.put(to, new ArrayList<>()); // Add `to` with an empty adjacency list
        }
        // Add a weighted edge (or unweighted if weight is null) from `from` to `to`
        adjEdList.get(from).add(new Edge(from, to, weight));
    }

    //TODO overload 2 times
    public boolean removeEdge(Node from, Node to) {
        // Check if `from` exists in the graph
        if (!adjEdList.containsKey(from)) {
            return false; // `from` node is not in the graph, so no edge can exist
        }
        // Attempt to remove the edge from `from` to `to`
        return adjEdList.get(from).removeIf(edge -> edge.to().equals(to));
         // `removeIf` returns true if any edge was removed, false otherwise
    }

    //TODO overload
    public List<Edge> getOutEdges(Node n) {
        // Check if `n` exists in the graph; if not, return an empty list
        return adjEdList.getOrDefault(n, Collections.emptyList());
    }

    public List<Edge> getInEdges(Node n){
        List<Edge> inEdges = new ArrayList<>();
        for (List<Edge> edges : adjEdList.values()) {
            for (Edge edge : edges) {
                if (edge.to().equals(n)) {
                    inEdges.add(edge);
                }
            }
        }
        return inEdges;
    }

    // TODO overload with node id + case of undirected graph
    public List<Edge> getIncidentEdges(Node n) {
        List<Edge> incidentEdges = new ArrayList<>();
        incidentEdges.addAll(getInEdges(n));
        incidentEdges.addAll(getOutEdges(n));
        return incidentEdges;
    }

    public List<Edge> getEdges(Node u, Node v){
        return adjEdList.get(u).stream()
                .filter(edge -> edge.to().equals(v))
                .toList();
    }

    public List<Edge> getAllEdges(){
        List<Edge> allEdges = new ArrayList<>();
        for (List<Edge> edges : adjEdList.values()) {
            allEdges.addAll(edges);
        }
        return allEdges;
    }

    /* ------------ Methods related to the graph’s representations and transformations -----*/

    public int[] toSuccessorArray(){
        List<Integer> SA = new ArrayList<>();
        for (Node node : adjEdList.keySet()) {
            SA.add(node.getId());
            for (Edge edge : adjEdList.get(node)) {
                SA.add(edge.to().getId());
            }
            SA.add(0);
        }
        return SA.stream().mapToInt(Integer::intValue).toArray();
    }

    public int[][] toAdjMatrix(){
        int n = nbNodes();
        int[][] adjMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            Node node = getNode(i + 1);
            if (node != null) {
                for (Edge edge : adjEdList.get(node)) {
                    adjMatrix[i][edge.to().getId() - 1] = 1;
                }
            }
        }
        return adjMatrix;
    }

    public Graph getReverse(){
        Graph reverseGraph = new Graph();
        for (Node node : adjEdList.keySet()) {
            reverseGraph.addNode(node);
        }
        for (Edge edge : getAllEdges()) {
            reverseGraph.addEdge(edge.to(), edge.from());
        }
        return reverseGraph;
    }

    //TODO
    public Graph getTransitiveClosure() {
        return null;
    }

    public boolean isMultiGraph() {
        // Iterate over each node in the adjacency list
        for (Node u : this.adjEdList.keySet()) {
            // For each edge from `u`, check if it forms a multi-edge with its target node `v`
            for (Edge edge : this.adjEdList.get(u)) {
                Node v = edge.to();
                // If `u` has a multi-edge to `v`, return true
                if (isMultiEdge(u, v)) {
                    return true;
                }
            }
        }
        // If no multi-edges are found, return false
        return false;
    }

    public boolean isSimpleGraph() {
        // Iterate over each node and its edges in the adjacency list
        for (List<Edge> edges : adjEdList.values()) {
            for (Edge edge : edges) {
                // Check if the edge is a self-loop or part of a multi-edge
                if (edge.isSelfLoop() || edge.isMultiEdge()) {
                    return false; // The graph is not simple if any such edge is found
                }
            }
        }
        // If no self-loops or multi-edges are found, the graph is simple
        return true;
    }

    public boolean hasSelfLoops() {
        // Iterate over each node and its edges in the adjacency list
        for (List<Edge> edges : adjEdList.values()) {
            for (Edge edge : edges) {
                // Check if the edge is a self-loop
                if (edge.isSelfLoop()) {
                    return true; // Return true as soon as a self-loop is found
                }
            }
        }
        // If no self-loops are found, return false
        return false;
    }

    // TODO Check if the method is correct
    public Graph toSimpleGraph() {
        // Create a new Graph instance to hold the simple graph
        Graph simpleGraph = new Graph();

        // Iterate over each node and its edges in the current graph's adjacency list
        for (Node from : adjEdList.keySet()) {
            for (Edge edge : adjEdList.get(from)) {
                Node to = edge.to();

                // Skip self-loops
                if (from.equals(to)) {
                    continue;
                }

                // Add nodes to the new graph if they do not already exist
                simpleGraph.addNode(from);
                simpleGraph.addNode(to);

                // Add an edge from `from` to `to` if it's the first occurrence in the simple graph
                if (!simpleGraph.existsEdge(from, to)) {
                    simpleGraph.addEdge(from, to);
                }
            }
        }

        return simpleGraph;
    }

    public Graph copy(){
        Graph copyGraph = new Graph();
        for (Node node : adjEdList.keySet()) {
            copyGraph.addNode(node);
        }
        for (Edge edge : getAllEdges()) {
            copyGraph.addEdge(edge.from(), edge.to());
        }
        return copyGraph;
    }

    /* -------Graph Traversal ---------*/

    public List<Node> getDFS() {
        if (adjEdList.isEmpty()) {
            return new ArrayList<>(); // Return empty list if the graph has no nodes
        }

        List<Node> visited = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        Node start = adjEdList.keySet().iterator().next();
        stack.push(start);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (!visited.contains(current)) {
                visited.add(current);
                List<Node> successors = getSuccessors(current);
                for (Node successor : successors) {
                    stack.push(successor);
                }
            }
        }

        return visited;
    }

    // TODO overload with node id
    public List<Node> getDFS(Node u) {
        List<Node> visited = new ArrayList<>();
        Stack<Node> stack = new Stack<>();
        stack.push(u);

        while (!stack.isEmpty()) {
            Node current = stack.pop();

            if (!visited.contains(current)) {
                visited.add(current);
                List<Node> successors = getSuccessors(current);
                for (Node successor : successors) {
                    stack.push(successor);
                }
            }
        }

        return visited;
    }

    public List<Node> getBFS() {
        if (adjEdList.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Node> visited = new HashSet<>();
        List<Node> result = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        Node start = adjEdList.keySet().iterator().next();
        queue.add(start);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (visited.add(current)) { // Only add to visited if it's not already there
                result.add(current);

                List<Node> successors = getSuccessors(current);
                for (Node successor : successors) {
                    if (!visited.contains(successor)) {
                        queue.add(successor);
                    }
                }
            }
        }

        return result;
    }

    // TODO overload with node id

    public List<Node> getBFS(Node u) {
        Set<Node> visited = new HashSet<>();
        List<Node> result = new ArrayList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(u);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            if (visited.add(current)) { // Only add to visited if it's not already there
                result.add(current);

                List<Node> successors = getSuccessors(current);
                for (Node successor : successors) {
                    if (!visited.contains(successor)) {
                        queue.add(successor);
                    }
                }
            }
        }

        return result;
    }


    public List<Node> getDFSWithVisitInfo(Map<Node, NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit) {
        if (adjEdList.isEmpty()) return new ArrayList<>();

        // Find the node with the lowest ID to start the DFS
        Node startNode = adjEdList.keySet().stream().min(Comparator.comparingInt(Node::getId)).orElse(null);

        // Perform DFS starting from the node with the lowest ID
        return getDFSWithVisitInfo(startNode, nodeVisit, edgeVisit);
    }


    public List<Node> getDFSWithVisitInfo(Node u, Map<Node, NodeVisitInfo> nodeVisit, Map<Edge, EdgeVisitType> edgeVisit) {
        List<Node> visitedNodes = new ArrayList<>();
        Stack<Node> stack = new Stack<>();

        // Initialize the starting node's visit information
        NodeVisitInfo.time = 0; // Reset the static time counter
        stack.push(u);

        // Initialize the nodeVisit information for all nodes
        adjEdList.keySet().forEach(node -> nodeVisit.put(node, new NodeVisitInfo()));

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            NodeVisitInfo currentInfo = nodeVisit.get(current);

            if (currentInfo.getColour() == NodeColour.WHITE) {
                currentInfo.setColour(NodeColour.GRAY);
                currentInfo.setDiscovery(++NodeVisitInfo.time); // Set discovery time
                visitedNodes.add(current);

                List<Node> successors = getSuccessors(current);
                for (Node successor : successors) {
                    Edge edge = new Edge(current, successor);

                    // Classify the edge type and update the edgeVisit map
                    if (nodeVisit.get(successor).getColour() == NodeColour.WHITE) {
                        edgeVisit.put(edge, EdgeVisitType.TREE);
                        nodeVisit.get(successor).setPredecessor(current);
                        stack.push(successor);
                    } else if (nodeVisit.get(successor).getColour() == NodeColour.GRAY) {
                        edgeVisit.put(edge, EdgeVisitType.BACKWARD);
                    } else if (nodeVisit.get(successor).getColour() == NodeColour.BLACK) {
                        if (currentInfo.getDiscovery() < nodeVisit.get(successor).getDiscovery()) {
                            edgeVisit.put(edge, EdgeVisitType.FORWARD);
                        } else {
                            edgeVisit.put(edge, EdgeVisitType.CROSS);
                        }
                    }
                }
            }

            // Finish processing the current node
            if (currentInfo.getColour() == NodeColour.GRAY) {
                currentInfo.setColour(NodeColour.BLACK);
                currentInfo.setFinished(++NodeVisitInfo.time); // Set finish time
            }
        }

        return visitedNodes;
    }

/*  --------------------- GRAPH IMPORT / EXPORT --------------------*/

    // 1. Import method with default extension ".gv"
    public static Graph fromDotFile(String filename) {
        return fromDotFile(filename, ".gv");
    }

    // 2. Import method with custom extension
    public static Graph fromDotFile(String filename, String extension) {
        Graph graph = new Graph();
        String filePath = filename + extension;

        Pattern directedPattern = Pattern.compile("(\\d+) -> (\\d+)( \\[label=(\\d+), len=(\\d+)\\])?");
        Pattern undirectedPattern = Pattern.compile("(\\d+) -- (\\d+)( \\[label=(\\d+), len=(\\d+)\\])?");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                Matcher directedMatcher = directedPattern.matcher(line);
                Matcher undirectedMatcher = undirectedPattern.matcher(line);

                if (directedMatcher.matches()) {
                    int fromId = Integer.parseInt(directedMatcher.group(1));
                    int toId = Integer.parseInt(directedMatcher.group(2));
                    Integer weight = directedMatcher.group(4) != null ? Integer.parseInt(directedMatcher.group(4)) : null;

                    Node from = new Node(fromId, graph);
                    Node to = new Node(toId, graph);

                    graph.addNode(from);
                    graph.addNode(to);

                    if (weight != null) {
                        graph.addEdge(from, to, weight);
                    } else {
                        graph.addEdge(from, to);
                    }
                } else if (undirectedMatcher.matches()) {
                    int fromId = Integer.parseInt(undirectedMatcher.group(1));
                    int toId = Integer.parseInt(undirectedMatcher.group(2));
                    Integer weight = undirectedMatcher.group(4) != null ? Integer.parseInt(undirectedMatcher.group(4)) : null;

                    Node from = new Node(fromId, graph);
                    Node to = new Node(toId, graph);

                    graph.addNode(from);
                    graph.addNode(to);

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

        return graph;
    }

    // 3. Export method to string in DOT format
    public String toDotString() {
        StringBuilder dotBuilder = new StringBuilder("graph G {\n");

        Set<Edge> edgesProcessed = new HashSet<>();

        for (Node from : adjEdList.keySet()) {
            for (Edge edge : adjEdList.get(from)) {
                Node to = edge.to();
                if (!edgesProcessed.contains(edge)) {
                    if (edge.getWeight() != null) {
                        dotBuilder.append("    ")
                                .append(from.getId())
                                .append(" -> ")
                                .append(to.getId())
                                .append(" [label=")
                                .append(edge.getWeight())
                                .append(", len=")
                                .append(edge.getWeight())
                                .append("];\n");
                    } else {
                        dotBuilder.append("    ")
                                .append(from.getId())
                                .append(" -> ")
                                .append(to.getId())
                                .append(";\n");
                    }
                    edgesProcessed.add(edge);
                }
            }
        }

        dotBuilder.append("}\n");
        return dotBuilder.toString();
    }

    // 4. Export method to file with default ".gv" extension
    public void toDotFile(String filename) {
        toDotFile(filename, ".gv");
    }

    // 5. Export method to file with custom extension
    public void toDotFile(String filename, String extension) {
        String dotString = toDotString();
        String filePath = filename + extension;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(dotString);
        } catch (IOException e) {
            System.err.println("Error writing the DOT file: " + e.getMessage());
        }
    }


}











