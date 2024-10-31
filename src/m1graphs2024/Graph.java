package m1graphs2024;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

}










