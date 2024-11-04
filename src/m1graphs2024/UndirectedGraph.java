package m1graphs2024;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* Undirected graphs */
public class UndirectedGraph extends Graph{


    public UndirectedGraph(int... n){
        super(n);
    }

    @Override
    public int nbEdges(){/* undirected graph*/
        return adjEdList.values().stream().mapToInt(List::size).sum()/2;
    }


    @Override
    public UndirectedGraph  getTransitiveClosure() {
        UndirectedGraph transitiveClosure = new UndirectedGraph();
        transitiveClosure = this.copy();
        if (transitiveClosure.isSimpleGraph()) {
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

}
