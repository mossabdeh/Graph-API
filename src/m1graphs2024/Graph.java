package m1graphs2024;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a directed graph with nodes and edges. Provides functionality to manage nodes and edges,
 * perform transformations, import/export graphs in DOT format, and traverse the graph.
 */
public class Graph {

    Map<Node, List<Edge>> adjEdList;


    /**
     * Default constructor for creating an empty graph.
     */
    public Graph() {/* Empty Constructor */
    }

    /**
     * Constructs an unweighted graph from a successor array (SA) format.
     * The integer 0 acts as a separator to indicate new nodes.
     *
     * @param SA array of integers representing the successor array
     */
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


    /**
     * Returns the number of nodes in the graph.
     *
     * @return the number of nodes in the graph
     */
    public int nbNodes(){
        return this.adjEdList.size();
    }


    /**
     * Checks if a node exists in the graph.
     *
     * @param node the node to check
     * @return true if the node exists, false otherwise
     */
    public boolean usesNode(Node node) {
        return adjEdList != null && adjEdList.containsKey(node);
    }

    /**
     * Checks if a node with a given ID exists in the graph.
     *
     * @param id the node ID to check
     * @return true if a node with the given ID exists, false otherwise
     */
    public boolean usesNode(int id){
        return this.adjEdList.keySet().stream().anyMatch(node -> node.getId() == id);
    }

    /**
     * Checks if a node is part of this graph.
     *
     * @param n the node to check
     * @return true if the node is part of the graph, false otherwise
     */
    public boolean holdsNode(Node n) {
        // Ensure `n` is not null, belongs to this graph, and is in `adjEdList`
        return n != null && n.getGraph() == this && adjEdList != null && adjEdList.containsKey(n);
    }

    /**
     * Returns the node with the specified ID.
     *
     * @param id the ID of the node
     * @return the node with the specified ID, or null if it doesn't exist
     */

    public Node getNode(int id) {
        /* Search for a node with the specified id in the adjacency list keys*/
        return this.adjEdList.keySet().stream()
                .filter(node -> node.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Adds a node to the graph.
     *
     * @param node the node to add
     * @return true if the node was added, false if it already exists
     */
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

    /**
     * Adds a node with the specified ID to the graph.
     *
     * @param id the ID of the node to add
     * @return true if the node was added, false if it already exists
     */
    public boolean addNode(int id) {
        if(usesNode(id)){return false;} /* Check if a node with the same id already exists */
        Node n = new Node(id, this);
        return addNode(n);
    }

    /**
     * Removes a node and all its edges from the graph.
     *
     * @param n the node to remove
     * @return true if the node was removed, false if it didn't exist
     */
    public boolean removeNode(Node n) {
        if (!holdsNode(n)) {return false;}/* Check if the node exists in the adjacency list*/
        adjEdList.remove(n);/* Remove the node from the adjacency list */
        for (List<Edge> edges : adjEdList.values()) { /* Remove any edges in other nodes' lists that point to `n` */
            edges.removeIf(edge -> edge.to().equals(n));}
        return true; /* Node and all its incident edges were removed */
    }

    /**
     * Removes a node by ID and all its edges from the graph.
     *
     * @param id the ID of the node to remove
     * @return true if the node was removed, false if it didn't exist
     */
    public boolean removeNode(int id) {
        Node n = getNode(id);
        return removeNode(n);
    }

    /**
     * Retrieves all nodes in the graph.
     *
     * @return a list of all nodes in the graph
     */
    public List<Node> getAllNodes() {
        return new ArrayList<>(this.adjEdList.keySet());
    }

    /**
     * Retrieves the largest node ID in the graph.
     *
     * @return the largest node ID, or Integer.MIN_VALUE if no nodes exist
     */
    public int largestNodeId() {
        return adjEdList.keySet().stream()
                .mapToInt(Node::getId)
                .max()
                .orElse(Integer.MIN_VALUE); // Return Integer.MIN_VALUE if no nodes exist
    }


    /**
     * Retrieves the smallest node ID in the graph.
     *
     * @return the smallest node ID, or Integer.MAX_VALUE if no nodes exist
     */
    public int smallestNodeId() {
        return adjEdList.keySet().stream()
                .mapToInt(Node::getId)
                .min()
                .orElse(Integer.MAX_VALUE); // Return Integer.MAX_VALUE if no nodes exist
    }


    /**
     * Retrieves the successors of a node.
     *
     * @param n the node to get successors for
     * @return a list of successor nodes
     */
    public List<Node> getSuccessors(Node n) {
        if (!holdsNode(n)) {/* Check if the node exists in the graph */
            return Collections.emptyList();} /* Return an empty list if node `n` is not in the graph */
        return n.getSuccessors(); /* Return Successors */
    }

    /**
     * Retrieves the successors of a node by ID.
     *
     * @param id the ID of the node to get successors for
     * @return a list of successor nodes
     */
    public List<Node> getSuccessors(int id) {
        Node n = getNode(id);
        return getSuccessors(n);
    }
    /**
     * Retrieves a list of successor nodes for a given node, including duplicates if multiple edges
     * exist between the same nodes.
     *
     * @param n the node for which to retrieve successors
     * @return a list of successor nodes with possible duplicates if multiple edges exist
     */
    public List<Node> getSuccessorsMulti(Node n) {
        if (!holdsNode(n)) {
            return Collections.emptyList(); // Return an empty list if the node is not in the graph
        }
        return n.getSuccessorsMulti(); // Return the list of successors with possible duplicates
    }

    /**
     * Retrieves a list of successor nodes for a node specified by its ID, including duplicates if multiple edges
     * exist between the same nodes.
     *
     * @param id the ID of the node for which to retrieve successors
     * @return a list of successor nodes with possible duplicates if multiple edges exist
     */
    public List<Node> getSuccessorsMulti(int id) {
        Node n = getNode(id);
        return getSuccessorsMulti(n);
    }

    /**
     * Checks if two nodes are adjacent in the graph, meaning there is a direct edge from `u` to `v`.
     *
     * @param u the source node
     * @param v the target node
     * @return true if an edge exists from `u` to `v`, false otherwise
     */
    public boolean adjacent(Node u, Node v) {
        if (!holdsNode(u) || !holdsNode(v)) {
            return false; // Return false if either node is not in the graph
        }
        return u.adjacent(v); // Check for a direct edge from `u` to `v`
    }

    /**
     * Checks if two nodes specified by their IDs are adjacent in the graph, meaning there is a direct edge from `u` to `v`.
     *
     * @param u the ID of the source node
     * @param v the ID of the target node
     * @return true if an edge exists from `u` to `v`, false otherwise
     */
    public boolean adjacent(int u, int v) {
        Node nodeU = getNode(u);
        Node nodeV = getNode(v);
        return adjacent(nodeU, nodeV);
    }

    /**
     * Returns the in-degree of a specified node, representing the number of incoming edges.
     *
     * @param n the node for which to calculate the in-degree
     * @return the in-degree of the node
     */
    public int inDegree(Node n) {
        return n.inDegree();
    }

    /**
     * Returns the in-degree of a node specified by its ID, representing the number of incoming edges.
     *
     * @param id the ID of the node for which to calculate the in-degree
     * @return the in-degree of the node
     */
    public int inDegree(int id) {
        Node n = getNode(id);
        return inDegree(n);
    }

    /**
     * Returns the out-degree of a specified node, representing the number of outgoing edges.
     *
     * @param n the node for which to calculate the out-degree
     * @return the out-degree of the node
     */
    public int outDegree(Node n) {
        return n.outDegree();
    }

    /**
     * Returns the out-degree of a node specified by its ID, representing the number of outgoing edges.
     *
     * @param id the ID of the node for which to calculate the out-degree
     * @return the out-degree of the node
     */
    public int outDegree(int id) {
        Node n = getNode(id);
        return outDegree(n);
    }

    /**
     * Returns the total degree (sum of in-degree and out-degree) of a specified node.
     *
     * @param n the node for which to calculate the degree
     * @return the total degree of the node
     */
    public int degree(Node n) {
        return n.degree();
    }

    /**
     * Returns the total degree (sum of in-degree and out-degree) of a node specified by its ID.
     *
     * @param id the ID of the node for which to calculate the degree
     * @return the total degree of the node
     */
    public int degree(int id) {
        Node n = getNode(id);
        return degree(n);
    }



    /* ------------- Methods related to the edges. ------------- */


    /**
     * Returns the total number of edges in the graph.
     *
     * @return the number of edges in the graph, calculated by summing the sizes
     *         of all edge lists in the adjacency list.
     */
    public int nbEdges() {
        return adjEdList.values().stream().mapToInt(List::size).sum();
    }


    /**
     * Checks if an edge exists between `u` and `v` in this graph.
     * Delegates to `adjacent` as they serve the same purpose.
     */
    public boolean existsEdge(Node u, Node v) {
        // Check if the `u` node has any edges in the adjacency list
        if (!adjEdList.containsKey(u)) return false;

        // Check if there’s an edge from `u` to `v` in `u`'s list of edges
        return adjEdList.get(u).stream().anyMatch(edge -> edge.to().equals(v));
    }

    /**
     * Checks if an edge exists between two nodes identified by their IDs.
     *
     * @param u the ID of the source node.
     * @param v the ID of the target node.
     * @return true if there is an edge from node `u` to node `v`, false otherwise.
     */
    public boolean existsEdge(int u, int v) {
        return adjacent(u, v);
    }

    /**
     * Checks if a specified edge exists in the graph.
     *
     * @param e the edge to check.
     * @return true if the edge exists in the graph, false otherwise.
     */
    public boolean existsEdge(Edge e) {
        return adjEdList.get(e.from()).stream().anyMatch(edge -> edge.equals(e));
    }

    /**
     * Checks if there is more than one edge between two nodes in the graph.
     *
     * @param u the source node.
     * @param v the target node.
     * @return true if there is more than one edge from node `u` to node `v`, false otherwise.
     */
    public boolean isMultiEdge(Node u, Node v) {
        if (!adjEdList.containsKey(u)) return false;

        long count = adjEdList.get(u).stream()
                .filter(edge -> edge.to().equals(v))
                .limit(2)
                .count();

        return count > 1;
    }

    /**
     * Checks if there is more than one edge between two nodes identified by their IDs.
     *
     * @param u the ID of the source node.
     * @param v the ID of the target node.
     * @return true if there is more than one edge from node `u` to node `v`, false otherwise.
     */
    public boolean isMultiEdge(int u, int v) {
        Node nodeU = getNode(u);
        Node nodeV = getNode(v);
        return isMultiEdge(nodeU, nodeV);
    }

    /**
     * Checks if an edge is a multi-edge by examining all edges from its source node.
     *
     * @param e the edge to check.
     * @return true if the edge is a multi-edge, false otherwise.
     */
    public boolean isMultiEdge(Edge e) {
        return e.isMultiEdge();
    }

    /**
     * Adds a directed edge between two nodes.
     *
     * @param from the source node.
     * @param to the target node.
     */
    public void addEdge(Node from, Node to) {
        if (getNode(from.getId()) == null) {
            addNode(from);
        }
        if (getNode(to.getId()) == null) {
            addNode(to);
        }
        adjEdList.get(from).add(new Edge(from, to));
    }

    /**
     * Adds a directed weighted edge between two nodes.
     *
     * @param from the source node.
     * @param to the target node.
     * @param weight the weight of the edge.
     */
    public void addEdge(Node from, Node to, Integer weight) {
        if (getNode(from.getId()) == null) {
            addNode(from);
        }
        if (getNode(to.getId()) == null) {
            addNode(to);
        }
        adjEdList.get(from).add(new Edge(from, to, weight));
    }

    /**
     * Adds a directed edge between two nodes identified by their IDs.
     *
     * @param from the ID of the source node.
     * @param to the ID of the target node.
     */
    public void addEdge(int from, int to) {
        if (getNode(from) == null) {
            addNode(from);
        }
        if (getNode(to) == null) {
            addNode(to);
        }
        Node nodeFrom = getNode(from);
        Node nodeTo = getNode(to);
        adjEdList.get(nodeFrom).add(new Edge(from, to, this));
    }

    /**
     * Adds an edge to the graph, ensuring its nodes exist within the graph.
     *
     * @param e the edge to add.
     */
    public void addEdge(Edge e) {
        if (getNode(e.from().getId()) == null) {
            addNode(e.from().getId());
        }
        if (getNode(e.to().getId()) == null) {
            addNode(e.to().getId());
        }
        adjEdList.get(e.from()).add(e);
    }

    /**
     * Adds a directed weighted edge between two nodes identified by their IDs.
     *
     * @param from the ID of the source node.
     * @param to the ID of the target node.
     * @param weight the weight of the edge.
     */
    public void addEdge(int from, int to, Integer weight) {
        Node nodeFrom = getNode(from);
        Node nodeTo = getNode(to);
        addEdge(nodeFrom, nodeTo, weight);
    }

    /**
     * Removes a directed edge between two nodes.
     *
     * @param from the source node.
     * @param to the target node.
     * @return true if the edge was removed, false if it did not exist.
     */
    public boolean removeEdge(Node from, Node to) {
        if (existsEdge(from, to)) {
            return adjEdList.get(from).removeIf(edge -> edge.to().equals(to));
        }
        return false;
    }

    /**
     * Removes a directed edge between two nodes identified by their IDs.
     *
     * @param from the ID of the source node.
     * @param to the ID of the target node.
     * @return true if the edge was removed, false if it did not exist.
     */
    public boolean removeEdge(int from, int to) {
        Node nodeFrom = getNode(from);
        Node nodeTo = getNode(to);
        return nodeFrom != null && nodeTo != null && removeEdge(nodeFrom, nodeTo);
    }

    /**
     * Removes an edge from the graph.
     *
     * @param e the edge to remove.
     * @return true if the edge was removed, false if it did not exist.
     */
    public boolean removeEdge(Edge e) {
        return adjEdList.get(e.from()).removeIf(edge -> edge.equals(e));
    }

    /**
     * Gets all outgoing edges of a node.
     *
     * @param n the node.
     * @return a list of all outgoing edges.
     */
    public List<Edge> getOutEdges(Node n) {
        return n.getOutEdges();
    }

    /**
     * Gets all outgoing edges of a node identified by its ID.
     *
     * @param id the ID of the node.
     * @return a list of all outgoing edges.
     */
    public List<Edge> getOutEdges(int id) {
        Node n = getNode(id);
        return getOutEdges(n);
    }

    /**
     * Gets all incoming edges of a node.
     *
     * @param n the node.
     * @return a list of all incoming edges.
     */
    public List<Edge> getInEdges(Node n) {
        return n.getInEdges();
    }

    /**
     * Gets all incoming edges of a node identified by its ID.
     *
     * @param id the ID of the node.
     * @return a list of all incoming edges.
     */
    public List<Edge> getInEdges(int id) {
        Node n = getNode(id);
        return getInEdges(n);
    }

    /**
     * Gets all incident edges of a node, including incoming and outgoing edges.
     *
     * @param n the node.
     * @return a list of all incident edges.
     */
    public List<Edge> getIncidentEdges(Node n) {
        return n.getIncidentEdges();
    }

    /**
     * Gets all incident edges of a node identified by its ID, including incoming and outgoing edges.
     *
     * @param id the ID of the node.
     * @return a list of all incident edges.
     */
    public List<Edge> getIncidentEdges(int id) {
        Node n = getNode(id);
        return getIncidentEdges(n);
    }

    /**
     * Retrieves all edges between two nodes.
     *
     * @param u the source node.
     * @param v the target node.
     * @return a list of all edges from node `u` to node `v`.
     */
    public List<Edge> getEdges(Node u, Node v) {
        return adjEdList.get(u).stream()
                .filter(edge -> edge.to().equals(v))
                .toList();
    }

    /**
     * Gets a list of all edges in the graph.
     *
     * @return a list containing all edges in the graph.
     */
    public List<Edge> getAllEdges() {
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



    public Graph getTransitiveClosure() {


        // Step 1: Create a new graph to hold the transitive closure
        Graph transitiveClosure = new Graph();
        transitiveClosure.adjEdList = new HashMap<>(); // Initialize adjEdList to prevent NullPointerException

        Graph simpleGraph = new Graph();
        simpleGraph = this.toSimpleGraph();
        // Copy all nodes from the original graph to the transitive closure graph
        for (Node node : simpleGraph.adjEdList.keySet()) {
            transitiveClosure.addNode(node.getId());
        }

        // Step 2: Add all original edges (excluding multi-edges and self-loops) to the transitive closure graph
        for (Node from : adjEdList.keySet()) {
            for (Edge edge : adjEdList.get(from)) {
                Node to = edge.to();
                if (!from.equals(to) && !transitiveClosure.existsEdge(from, to)) {
                    transitiveClosure.addEdge(from, to); // Add only if it’s not a self-loop and not already present
                }
            }
        }
        // Step 3: Apply the Roy-Warshall algorithm to compute the transitive closure
        List<Node> allNodes = new ArrayList<>(transitiveClosure.getAllNodes());
        for (Node intermediate : allNodes) {
            for (Node source : allNodes) {
                for (Node target : allNodes) {
                    // Check if there is a path from source to target through intermediate
                    if (transitiveClosure.existsEdge(source, intermediate) && transitiveClosure.existsEdge(intermediate, target)) {
                        // Add a direct edge from source to target if it doesn't exist and is not a self-loop
                        if (!transitiveClosure.existsEdge(source, target) && !source.equals(target)) {
                            transitiveClosure.addEdge(source, target);
                        }
                    }
                }
            }
        }
        return transitiveClosure;
    }

    public boolean isMultiGraph() {
        // Iterate over each node in the adjacency list
        for (Node u : adjEdList.keySet()) {
            // Check each target node for multi-edges
            for (Edge edge : adjEdList.get(u)) {
                Node v = edge.to();
                // If there is a multi-edge from `u` to `v`, return true immediately
                if (isMultiEdge(u, v)) {
                    return true;
                }
            }
        }
        // No multi-edges were found, so return false
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

    public Graph toSimpleGraph() {
        // Create a new Graph instance to hold the simple graph
        Graph simpleGraph = new Graph();
        simpleGraph.adjEdList = new HashMap<>(); // Initialize adjacency list for the new graph

        // Step 1: Copy all nodes from the original graph to the simple graph
        for (Node node : adjEdList.keySet()) {
            simpleGraph.addNode(node.getId());
        }

        // Step 2: Copy edges while avoiding self-loops and multi-edges
        for (Node from : adjEdList.keySet()) {
            for (Edge edge : adjEdList.get(from)) {
                Node to = edge.to();

                // Skip self-loops
                if (from.equals(to)) continue;

                // Only add the edge if it doesn’t already exist in the simple graph
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
        Pattern isolatedNodePattern = Pattern.compile("^(\\d+);?$"); // Pattern for isolated nodes

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("//")) continue;

                Matcher directedMatcher = directedPattern.matcher(line);
                Matcher isolatedNodeMatcher = isolatedNodePattern.matcher(line);

                // Handle isolated nodes
                if (isolatedNodeMatcher.matches()) {
                    int nodeId = Integer.parseInt(isolatedNodeMatcher.group(1));
                    Node isolatedNode = new Node(nodeId, graph);
                    graph.addNode(isolatedNode); // Add isolated node without any edges

                    // Handle directed edges
                } else if (directedMatcher.matches()) {
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








    // 3. Export method to string in DOT format
    public String toDotString() {
        StringBuilder dotBuilder = new StringBuilder("digraph G {\n");
        dotBuilder.append("    rankdir=LR;\n");

        // Sort nodes by their natural order
        List<Node> sortedNodes = new ArrayList<>(adjEdList.keySet());
        Collections.sort(sortedNodes);

        // Iterate through each node and append edges or the node itself in sorted order
        for (Node node : sortedNodes) {
            List<Edge> sortedEdges = new ArrayList<>(adjEdList.get(node));
            Collections.sort(sortedEdges); // Sort edges by target node ID

            if (!sortedEdges.isEmpty()) {
                // Append each edge for this node
                for (Edge edge : sortedEdges) {
                    Node to = edge.to();
                    dotBuilder.append("    ")
                            .append(node.getId())
                            .append(" -> ")
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











