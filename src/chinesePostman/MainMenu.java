package chinesePostman;

import m1graphs2024.Edge;
import m1graphs2024.Node;
import m1graphs2024.UndirectedGraph;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {

    private static final String GRAPH_DIR = "src/chinesePostman/graphTests/";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Chinese Postman Problem Solver ===");

        while (true) {
            // Display main menu
            System.out.println("\nMain Menu:");
            System.out.println("1. List available graph files");
            System.out.println("2. Load a graph file by name");
            System.out.println("3. Quit");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> listGraphFiles();
                case "2" -> loadAndProcessGraph(scanner);
                case "3" -> {
                    System.out.println("Exiting the program.");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }
    }

    /**
     * Lists all available .gv files in the GRAPH_DIR directory.
     */
    private static void listGraphFiles() {
        File dir = new File(GRAPH_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            System.out.println("No graph directory found at: " + GRAPH_DIR);
            return;
        }

        String[] files = dir.list((d, name) -> name.endsWith(".gv"));
        if (files == null || files.length == 0) {
            System.out.println("No .gv files found in " + GRAPH_DIR);
            return;
        }

        System.out.println("Available Graph Files:");
        for (String f : files) {
            // Print just the base name without extension
            System.out.println("- " + f.replace(".gv", ""));
        }
    }

    /**
     * Prompts user for a graph file name, loads the graph, and if successful,
     * proceeds to handle graph details and solution.
     */
    private static void loadAndProcessGraph(Scanner scanner) {
        System.out.print("Enter the graph file name (without extension): ");
        String graphFileName = scanner.nextLine().trim();

        UndirectedGraph graph = DotReaderWriter.fromDotFile(graphFileName);
        if (graph == null || graph.getAllNodes().isEmpty()) {
            System.err.println("Error: Failed to load graph or the graph is empty. Check file name and try again.");
            return;
        }

        ChinesePostman chinesePostman = new ChinesePostman(graph);
        System.out.println("=== Graph Details ===");
        chinesePostman.printGraphDetails();

        // Determine graph type
        String graphType = chinesePostman.determineGraphType();
        System.out.println("Graph type: " + graphType);

        // After loading and identifying the graph, show another menu
        handleGraphOperations(scanner, graph, chinesePostman, graphFileName, graphType);
    }

    /**
     * Display a sub-menu to handle operations on the loaded graph:
     * - Compute and visualize the Eulerian/Chinese Postman solution
     * - Return to main menu (to load another graph)
     */
    private static void handleGraphOperations(Scanner scanner, UndirectedGraph graph, ChinesePostman chinesePostman, String graphFileName, String graphType) {
        while (true) {
            System.out.println("\nGraph Operations Menu:");
            System.out.println("1. Compute and visualize solution (Eulerian/Chinese Postman)");
            System.out.println("2. Return to Main Menu");
            System.out.print("Enter your choice: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    List<String> circuit = new ArrayList<>();
                    int totalLength = 0;
                    Integer extraCost = null;

                    if ("Eulerian".equals(graphType)) {
                        System.out.println("Computing Eulerian Circuit...");
                        Node startNode = chinesePostman.getLowestIdNode();
                        circuit = chinesePostman.computeEulerianCircuit(startNode);
                        totalLength = computeTotalLength(graph, circuit);
                        DotReaderWriter.toDotFile(graph, "output_" + graphFileName, graphType, circuit, totalLength, extraCost);

                    } else if ("Semi-Eulerian".equals(graphType)) {
                        System.out.println("Computing Eulerian Trail...");
                        Node startNode = chinesePostman.getLowestIdNode(chinesePostman.getOddDegreeNodes());
                        circuit = chinesePostman.computeEulerianTrail(startNode);
                        totalLength = computeTotalLength(graph, circuit);
                        DotReaderWriter.toDotFile(graph, "output_" + graphFileName, graphType, circuit, totalLength, extraCost);

                    } else {
                        // Non-Eulerian: Need to choose the matching algorithm
                        System.out.println("Non-Eulerian Graph detected.");
                        System.out.println("Choose the minimal-length pairwise matching algorithm:");
                        System.out.println("1. Enumeration");
                        System.out.println("2. Greedy");
                        System.out.println("3. Random");
                        System.out.print("Enter your choice: ");
                        String algoChoice = scanner.nextLine().trim();

                        String matchingAlgorithm;
                        switch (algoChoice) {
                            case "1" -> matchingAlgorithm = "enumeration";
                            case "2" -> matchingAlgorithm = "greedy";
                            case "3" -> matchingAlgorithm = "random";
                            default -> {
                                System.out.println("Invalid choice, defaulting to enumeration.");
                                matchingAlgorithm = "enumeration";
                            }
                        }

                        System.out.println("Applying Chinese Postman solution with " + matchingAlgorithm + " algorithm...");
                        chinesePostman.computeChinesePostmanSolution(matchingAlgorithm);
                        // The DOT file is written inside computeChinesePostmanSolution
                    }

                    System.out.println("Operation completed. Check the 'src/chinesePostman/graphTests/' directory for the output DOT file.");
                }
                case "2" -> {
                    // Return to main menu
                    return;
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }
    }

    /**
     * A helper method to compute the total length of a given circuit.
     */
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

}
