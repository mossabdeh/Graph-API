package chinesePostman;

import m1graphs2024.Edge;
import m1graphs2024.Node;
import m1graphs2024.UndirectedGraph;

import java.io.*;
import java.util.*;
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

    /**
     * Original toDotFile method for cases where no newly added edges need to be highlighted.
     */
    public static void toDotFile(UndirectedGraph graph, String outputFilename, String graphType,
                                 List<String> circuit, int totalLength, Integer extraCost) {
        // For Eulerian/Semi-Eulerian (no duplicates), just call the simplified version
        toDotFile(graph, outputFilename, graphType, circuit, totalLength, extraCost, null, null);
    }

    /**
     * Overloaded method to highlight both original and duplicated edges as red.
     * @param graph The graph
     * @param outputFilename Name of output file (without extension)
     * @param graphType Type of the graph
     * @param circuit The Eulerian path/circuit
     * @param totalLength Total length
     * @param extraCost Extra cost (for Non-Eulerian)
     * @param originalEdgesCount Map of original edges count
     * @param newlyAddedEdgesCount Map of new duplicates count
     */
    public static void toDotFile(UndirectedGraph graph, String outputFilename, String graphType,
                                 List<String> circuit, int totalLength, Integer extraCost,
                                 Map<EdgeKey, Integer> originalEdgesCount,
                                 Map<EdgeKey, Integer> newlyAddedEdgesCount) {
        String filePath = "src/chinesePostman/graphTests/" + outputFilename + ".gv";

        // If no maps provided (e.g., Eulerian/Semi-Eulerian), just print normal edges
        boolean highlight = (originalEdgesCount != null && newlyAddedEdgesCount != null);

        // Make copies to avoid modifying the original maps
        Map<EdgeKey, Integer> originalCountCopy = highlight ? new HashMap<>(originalEdgesCount) : null;
        Map<EdgeKey, Integer> newCountCopy = highlight ? new HashMap<>(newlyAddedEdgesCount) : null;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("graph {\n");
            writer.write("    rankdir=LR\n");

            for (Edge edge : graph.getAllEdges()) {
                int fromId = edge.from().getId();
                int toId = edge.to().getId();
                int weight = edge.getWeight();

                String edgeLine = "    " + fromId + " -- " + toId;

                if (highlight) {
                    EdgeKey key = new EdgeKey(fromId, toId, weight);

                    // Check original count first
                    int oc = originalCountCopy.getOrDefault(key, 0);
                    if (oc > 0) {
                        // This is one of the original edges, print normal
                        edgeLine += " [label=" + weight + ", len=" + weight + "]\n";
                        originalCountCopy.put(key, oc - 1);
                    } else {
                        // No original edges left, must be a duplicate if count > 0 in newCount
                        int nc = newCountCopy.getOrDefault(key, 0);
                        if (nc > 0) {
                            // Print in red
                            edgeLine += " [label=" + weight + ", len=" + weight + ", color=red, fontcolor=red]\n";
                            newCountCopy.put(key, nc - 1);
                        } else {
                            // Should not happen if counts are correct
                            // Just print normal if something unexpected occurs
                            edgeLine += " [label=" + weight + ", len=" + weight + "]\n";
                        }
                    }
                } else {
                    // No highlighting, just print normal edge
                    edgeLine += " [label=" + weight + ", len=" + weight + "]\n";
                }

                writer.write(edgeLine);
            }

            // Construct the label based on graph type
            StringBuilder labelBuilder = new StringBuilder();
            labelBuilder.append("Type: ").append(graphType).append("\\n");
            if ("Eulerian".equals(graphType)) {
                labelBuilder.append("Eulerian circuit: ").append(circuit).append("\\n");
            } else if ("Semi-Eulerian".equals(graphType)) {
                labelBuilder.append("Eulerian trail: ").append(circuit).append("\\n");
            } else if ("Non-Eulerian".equals(graphType)) {
                labelBuilder.append("Chinese circuit: ").append(circuit).append("\\n");
                labelBuilder.append("Extra cost: ").append(extraCost).append("\\n");
            }
            labelBuilder.append("Total length: ").append(totalLength);

            writer.write("    label=\"" + labelBuilder.toString() + "\"\n");
            writer.write("}\n");

            System.out.println("Graph successfully written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing the DOT file: " + e.getMessage());
        }
    }




}