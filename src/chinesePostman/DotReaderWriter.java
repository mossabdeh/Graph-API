package chinesePostman;

import m1graphs2024.Edge;
import m1graphs2024.Node;
import m1graphs2024.UndirectedGraph;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DotReaderWriter {

    public static UndirectedGraph fromDotFile(String filename) {
        return fromDotFile(filename, ".gv");
    }

    public static UndirectedGraph fromDotFile(String filename, String extension) {
        UndirectedGraph graph = new UndirectedGraph();
        String filePath = "src/chinesePostman/graphTests/" + filename + extension;

        // Regex patterns for undirected edges and isolated nodes
        Pattern undirectedPattern = Pattern.compile("(\\d+)\\s*--\\s*(\\d+)(\\s*\\[.*?len\\s*=\\s*(\\d+).*?\\])?");
        Pattern isolatedNodePattern = Pattern.compile("^(\\d+);?$");

        // Set to track unique edges
        Set<String> seenEdges = new HashSet<>();

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
                    if (graph.getNode(nodeId) == null) { // Avoid duplicates
                        Node isolatedNode = new Node(nodeId, graph);
                        graph.addNode(isolatedNode);
                    }
                } else if (undirectedMatcher.matches()) { // Handle undirected edges
                    int fromId = Integer.parseInt(undirectedMatcher.group(1));
                    int toId = Integer.parseInt(undirectedMatcher.group(2));
                    Integer weight = undirectedMatcher.group(4) != null ? Integer.parseInt(undirectedMatcher.group(4)) : null;

                    // Create a unique key for the edge
                    String edgeKey = Math.min(fromId, toId) + "-" + Math.max(fromId, toId) + "-" + (weight != null ? weight : "no-weight");

                    // Skip exact duplicates
                    if (seenEdges.contains(edgeKey)) continue;
                    seenEdges.add(edgeKey);

                    // Get or create nodes
                    Node from = graph.getNode(fromId);
                    if (from == null) {
                        from = new Node(fromId, graph);
                        graph.addNode(from);
                    }

                    Node to = graph.getNode(toId);
                    if (to == null) {
                        to = new Node(toId, graph);
                        graph.addNode(to);
                    }

                    // Add edge
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

        return graph;
    }

    public static void toDotFile(UndirectedGraph graph, String outputFilename, String graphType,
                                 List<String> circuit, int totalLength, Integer extraCost) {
        String filePath = "src/chinesePostman/graphTests/" + outputFilename + ".gv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Start the DOT graph structure
            writer.write("graph {\n");
            writer.write("    rankdir=LR\n");

            // Write nodes and edges with their attributes
            for (Node node : graph.getAllNodes()) {
                for (Edge edge : graph.getOutEdges(node)) {
                    // Ensure each undirected edge is written only once
                    if (node.getId() < edge.to().getId()) {
                        writer.write("    " + node.getId() + " -- " + edge.to().getId());
                        writer.write(" [label=" + edge.getWeight() + ", len=" + edge.getWeight() + "]\n");
                    }
                }
            }

            // Add additional graph-level information
            writer.write("    label=\"Type: " + graphType + "\\n");
            if (graphType.equals("Eulerian")) {
                writer.write("Eulerian circuit: " + circuit + "\\n");
            } else if (graphType.equals("Semi-Eulerian")) {
                writer.write("Eulerian trail: " + circuit + "\\n");
            } else if (graphType.equals("Non-Eulerian")) {
                writer.write("Chinese circuit: " + circuit + "\\n");
                writer.write("Extra cost: " + extraCost + "\\n");
            }
            writer.write("Total length: " + totalLength + "\"\n");

            // End the DOT graph structure
            writer.write("}\n");

            System.out.println("Graph successfully written to " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing the DOT file: " + e.getMessage());
        }
    }



}
