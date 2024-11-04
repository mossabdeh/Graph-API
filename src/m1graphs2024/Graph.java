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


        for (int i = 0; i < SA.length - 1; i++) { /* Loop through SA, excluding the last element */
            int valueSA = SA[i];
            /* Loop in the SA array */
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


    /*  Number of nodes of the graph */
    public int nbNodes(){
        return this.adjEdList.size();
    }


    public boolean usesNode(Node node) {
        return adjEdList != null && adjEdList.containsKey(node);
    }

    public boolean usesNode(int id){
        return this.adjEdList.keySet().stream().anyMatch(node -> node.getId() == id);
    }

    public boolean holdsNode(Node n) {
        // Check if the node is not null and belongs to this graph
        if (n == null || n.getGraph() != this) {
            return false;
        }
        // Check if adjEdList is not null and contains the node
        return adjEdList != null && usesNode(n);
    }


    public Node getNode(int id) {
        /* Search for a node with the specified id in the adjacency list keys*/
        return this.adjEdList.keySet().stream()
                .filter(node -> node.getId() == id)
                .findFirst()
                .orElse(null);
    }
    public boolean addNode(Node node) {
        if (adjEdList == null) {
            adjEdList = new HashMap<>(); // Double-check initialization
        }

        if (!adjEdList.containsKey(node)) {
            adjEdList.put(node, new ArrayList<>());
             return true;// Initialize adjacency list for the node
        }
        return false;
    }
    public boolean addNode(int id) {
        if(usesNode(id)){return false;} /* Check if a node with the same id already exists */
        Node n = new Node(id, this);
        return addNode(n);
    }

    public boolean removeNode(Node n) {
        if (!holdsNode(n)) {return false;}/* Check if the node exists in the adjacency list*/
        adjEdList.remove(n);/* Remove the node from the adjacency list */
        for (List<Edge> edges : adjEdList.values()) { /* Remove any edges in other nodes' lists that point to `n` */
            edges.removeIf(edge -> edge.to().equals(n));}
        return true; /* Node and all its incident edges were removed */
    }
    public boolean removeNode(int id) {
        Node n = getNode(id);
        return removeNode(n);
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
    public List<Node> getSuccessors(Node n) {
        if (!holdsNode(n)) {/* Check if the node exists in the graph */
            return Collections.emptyList();} /* Return an empty list if node `n` is not in the graph */
        return n.getSuccessors(); /* Return Successors */
    }
    public List<Node> getSuccessors(int id) {
        Node n = getNode(id);
        return getSuccessors(n);
    }

    public List<Node> getSuccessorsMulti(Node n) {
        if (!holdsNode(n)) {/* Check if the node exists in the graph */
            return Collections.emptyList();}/* Return an empty list if node `n` is not in the graph */
        return n.getSuccessorsMulti();  /* Return the list with possible duplicates */
    }

    public List<Node> getSuccessorsMulti(int id) {
        Node n = getNode(id);
        return getSuccessorsMulti(n);}

    public boolean adjacent(Node u, Node v) {
        /* Check if `u` or `v` are not in the graph */
        if (!holdsNode(u) || !holdsNode(v) ) {return false; }/* If either node is missing, they cannot be adjacent */
        return u.adjacent(v) || v.adjacent(u);/*  Return true if either condition is true*/
    }
    public boolean adjacent(int u, int v) {
        Node nodeU = getNode(u);
        Node nodeV = getNode(v);
        return adjacent(nodeU, nodeV);}

    public int inDegree(Node n) {
       return n.inDegree();
    }
    public int inDegree(int id) {
        Node n = getNode(id);
        return inDegree(n);
    }

    public int outDegree(Node n) {
      return n.outDegree();
    }
    public int outDegree(int id) {
        Node n = getNode(id);
        return outDegree(n);
    }
    public int degree(Node n) {
        return n.degree();
    }
    public int degree(int id) {
        Node n = getNode(id);
        return degree(n);
    }


    /* ------------- Methods related to the edges. ------------- */


    public int nbEdges(){/* directed graph*/
        return adjEdList.values().stream().mapToInt(List::size).sum();
    }

    public boolean existsEdge(Node u, Node v) {
        /* we check if nodes are adjacent that means there's edge between them */
        return adjacent(u,v);
    }
    public boolean existsEdge(int u, int v) {
        return adjacent(u,v);}

    /* Overloaded Version Edge reference*/
    public boolean existsEdge(Edge e){
        return adjEdList.get(e.from()).stream().anyMatch(edge -> edge.equals(e));
    }

    public boolean isMultiEdge(Node u, Node v) {
        return adjEdList.get(u).stream().filter(edge -> edge.to().equals(v)).count() > 1;
    }
    public boolean isMultiEdge(int u, int v) {
        Node nodeU = getNode(u);
        Node nodeV = getNode(v);
        return isMultiEdge(nodeU, nodeV);
    }
    public boolean isMultiEdge(Edge e){
        return e.isMultiEdge(); }


    public void addEdge(Node from, Node to) {
        if (getNode(from.getId()) == null) {
            addNode(from);}
        if (getNode(to.getId()) == null) {
            addNode(to); }
        adjEdList.get(from).add(new Edge(from, to));
    }

    public void addEdge(Node from, Node to, Integer weight) {
        if (getNode(from.getId()) == null) {
            addNode(from);}
        if (getNode(to.getId()) == null) {
            addNode(to); }
        adjEdList.get(from).add(new Edge(from, to, weight));
    }

    public void addEdge(int from, int to) {
        // Ensure both nodes exist in the graph
        if (getNode(from) == null) {
            addNode(from);
        }
        if (getNode(to) == null) {
            addNode(to);
        }

        // Retrieve the nodes after ensuring they exist
        Node nodeFrom = this.getNode(from);
        Node nodeTo = this.getNode(to);

        // Now, create the edge, as both nodes are present in the graph
        Edge edge = new Edge(from, to, this);  // Now safe to call Edge constructor

        // Add the edge to the adjacency list
        adjEdList.get(nodeFrom).add(edge);
    }


    public void addEdge(Edge e) {
        // Check if 'from' node exists; if not, add it to the graph
        if (getNode(e.from().getId()) == null) {
            addNode(e.from().getId());
        }
        // Check if 'to' node exists; if not, add it to the graph
        if (getNode(e.to().getId()) == null) {
            addNode(e.to().getId());
        }
        // Retrieve the nodes after ensuring they exist
        Node nodeFrom = this.getNode(e.from().getId());
        Node nodeTo = this.getNode(e.to().getId());

        // Now that nodes are validated, add the edge directly to adjacency list
        adjEdList.get(nodeFrom).add(e);
    }

    public void addEdge(int from, int to, Integer weight) {
        Node nodeFrom = getNode(from);
        Node nodeTo = getNode(to);
        addEdge(nodeFrom, nodeTo, weight);
    }

    public boolean removeEdge(Node from, Node to) {
        if (existsEdge(from,to)) {
            return adjEdList.get(from).removeIf(edge -> edge.to().equals(to));}
       return false;}

    public boolean removeEdge(int from, int to) {
        Node nodeFrom = getNode(from);
        Node nodeTo = getNode(to);
        if (nodeFrom == null || nodeTo == null) {
            return false;
        }
        return removeEdge(nodeFrom, nodeTo);}

    public boolean removeEdge(Edge e){
        return adjEdList.get(e.from()).removeIf(edge -> edge.equals(e));
    }

    public List<Edge> getOutEdges(Node n) {
        return n.getOutEdges();
    }
    public List<Edge> getOutEdges(int id) {
        Node n = getNode(id);
        return getOutEdges(n);}

    public List<Edge> getInEdges(Node n){
        return n.getInEdges();}

    public List<Edge> getInEdges(int id){
        Node n = getNode(id);
        return getInEdges(n);}

    // TODO  case of undirected graph
    public List<Edge> getIncidentEdges(Node n) {
       return n.getIncidentEdges();
    }
    public List<Edge> getIncidentEdges(int id) {
        Node n = getNode(id);
        return getIncidentEdges(n);}

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

    //TODO use symmetric edge
    public Graph getReverse() {
        Graph reverseGraph = new Graph();
        // Add all nodes to the reverse graph
        if (this.adjEdList == null){
            System.out.println("i m here ");
        }
        for (Node node : this.adjEdList.keySet()) {
            reverseGraph.addNode(new Node(node.getId(), reverseGraph)); // Create new Node instances if necessary
        }
        // Add reversed edges to the reverse graph
        for (Edge edge : getAllEdges()) {
            reverseGraph.addEdge(edge.to().getId(), edge.from().getId()); // Add reversed edge
        }
        return reverseGraph;
    }


    //TODO
    public Graph getTransitiveClosure() {
        Graph transitiveClosure = new Graph();
        transitiveClosure = this.copy();
        if (!transitiveClosure.isSimpleGraph()) {
            transitiveClosure= transitiveClosure.toSimpleGraph();
        }
        // Step 2: Compute transitive closure using adjacency list traversal
        for (Node node : transitiveClosure.getAllNodes()) {
            for (Node intermediate : transitiveClosure.getSuccessors(node)) {
                for (Node target : transitiveClosure.getSuccessors(intermediate)) {
                    if (!node.equals(target) && !transitiveClosure.existsEdge(node, target)) {
                        transitiveClosure.addEdge(node, target);
                    }
                }
            }
        }
        return transitiveClosure;
    }

    public boolean isMultiGraph() {
        // Iterate over each node in the adjacency list
        for (Node u : this.adjEdList.keySet()) {
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
        return !this.hasSelfLoops() && !this.isMultiGraph();
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
                if (edge.isSelfLoop()) {continue;}  /* Skip self-loops */
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

    public List<Node> getDFS(int id){
        Node n = getNode(id);
        return getDFS(n);
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

    public List<Node> getBFS(int id) {
        Node n = getNode(id);
        return getBFS(n);
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
        String filePath = "src/m1graphs2024/graphTests/" + filename + extension;


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

                    System.out.println("Adding nodes " + fromId + " and " + toId + " to the graph.");
                    graph.addNode(from);
                    graph.addNode(to);

                    if (weight != null) {
                        System.out.println("Adding directed edge with weight from " + fromId + " to " + toId);
                        graph.addEdge(from, to, weight);
                    } else {
                        System.out.println("Adding directed edge from " + fromId + " to " + toId);
                        graph.addEdge(from, to);
                    }
                } else if (undirectedMatcher.matches()) {
                    int fromId = Integer.parseInt(undirectedMatcher.group(1));
                    int toId = Integer.parseInt(undirectedMatcher.group(2));
                    Integer weight = undirectedMatcher.group(4) != null ? Integer.parseInt(undirectedMatcher.group(4)) : null;

                    Node from = new Node(fromId, graph);
                    Node to = new Node(toId, graph);

                    System.out.println("Adding nodes " + fromId + " and " + toId + " to the graph.");
                    graph.addNode(from);
                    graph.addNode(to);

                    if (weight != null) {
                        System.out.println("Adding undirected edge with weight between " + fromId + " and " + toId);
                        graph.addEdge(from, to, weight);
                        graph.addEdge(to, from, weight);
                    } else {
                        System.out.println("Adding undirected edge between " + fromId + " and " + toId);
                        graph.addEdge(from, to);
                        graph.addEdge(to, from);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading the DOT file: " + e.getMessage());
        }

        System.out.println("Graph imported successfully with " + graph.adjEdList.size() + " nodes.");
        return graph;
    }

    // 3. Export method to string in DOT format
    public String toDotString() {
        StringBuilder dotBuilder = new StringBuilder("digraph G {\n");

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











