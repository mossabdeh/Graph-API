# Chinese Postman Problem Solver

## Table of Contents

- [Project Description](#project-description)
- [Prerequisites](#prerequisites)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)


## Project Description

The **Chinese Postman Problem Solver** is a Java-based application designed to find optimal routes in a graph, addressing the Chinese Postman Problem. The program can handle Eulerian, Semi-Eulerian, and Non-Eulerian graphs, computing Eulerian circuits or trails as appropriate and generating visual representations using DOT files for visualization with Graphviz.

## Prerequisites

Before setting up and running the project, ensure you have the following installed on your system:

- **Java Development Kit (JDK) 8 or higher**: [Download JDK](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html)
- **Graphviz** (for visualizing DOT files): [Download Graphviz](https://graphviz.org/download/)
- **Optional IDE**: An Integrated Development Environment like IntelliJ IDEA, Eclipse, or VS Code can simplify compilation and running.


## Usage

Upon running the application, you will be presented with a menu:


### Steps to Use:

1. **List Available Graph Files**

    - **Choose Option `1`**: This will display all available `.gv` graph files located in the `src/chinesePostman/graphTests/` directory.

    - **Example Output**:
      ```
      ---------- AVAILABLE GRAPH FILES ----------
       - eulerianGraph
       - semiEulerianGraph
       - nonEulerianGraph
      -------------------------------------------
      ```

2. **Load a Graph File**

    - **Choose Option `2`**: You will be prompted to enter the graph file name without the `.gv` extension.

    - **Example Prompt**:
      ```
      ---------- LOAD A GRAPH FILE ----------
      Enter the graph file name (without extension): eulerianGraph
      ```

    - **Outcome**:
        - The application will load the specified graph, display its details, and determine its type (Eulerian, Semi-Eulerian, or Non-Eulerian).

3. **Compute and Visualize Solution**

    - **After Loading a Graph**: Based on the graph type, the application will guide you through computing the appropriate solution.

    - **For Eulerian Graphs**:
        - **Action**: Compute Eulerian Circuit.
        - **Outcome**: The application will generate a DOT file representing the Eulerian Circuit.

    - **For Semi-Eulerian Graphs**:
        - **Action**: Compute Eulerian Trail.
        - **Outcome**: The application will generate a DOT file representing the Eulerian Trail.

    - **For Non-Eulerian Graphs**:
        - **Action**: Choose a minimal-length pairwise matching algorithm (Enumeration, Greedy, or Random) to compute the Chinese Postman Solution.
        - **Outcome**: The application will generate a DOT file highlighting duplicated edges to form a complete circuit.

    - **Example Workflow for a Non-Eulerian Graph**:
      ```
      Non-Eulerian Graph detected.
      Choose the minimal-length pairwise matching algorithm:
      1. Enumeration
      2. Greedy
      3. Random
      Enter your choice (1-3): 1
      Applying Chinese Postman solution with enumeration algorithm...
      Operation completed successfully!
      Check the 'src/chinesePostman/graphTests/' directory for the output DOT file.
      DOT file successfully written to src/chinesePostman/graphTests/output_nonEulerianGraph.gv
      ```

4. **Generate DOT File**

    - **Location**: The generated DOT file will be saved in the `src/chinesePostman/graphTests/` directory with a name prefixed by `output_` followed by the original graph file name.

    - **Visualization**: Use Graphviz or any compatible tool to open and visualize the generated DOT file.

        - **Command-Line Example**:
          ```bash
          dot -Tpng src/chinesePostman/graphTests/output_eulerianGraph.gv -o eulerianGraph.png
          ```
        - **Result**: This command will create a `eulerianGraph.png` image visualizing the Eulerian Circuit.

5. **Exit the Program**

    - **Choose Option `3`**: This will terminate the application.

    - **Example Output**:
      ```
      Exiting the program. Thank you for using the Chinese Postman Problem Solver!
      ```

### Important Note:

- **Immediate DOT File Generation**: If you notice that no DOT file is generated immediately after processing a graph, **stop the menu** by choosing the option to **return to the main menu** or **exit the program**. This action finalizes the DOT file generation process and ensures that the file is saved properly.

    - **Reason**: The application writes the DOT file upon completing the computation. Exiting the menu ensures that the file writing process is finalized and the file is saved without delay.

    - **Example**:
      ```
      Operation completed successfully!
      Check the 'src/chinesePostman/graphTests/' directory for the output DOT file.
      DOT file successfully written to src/chinesePostman/graphTests/output_eulerianGraph.gv
      ```





## Troubleshooting

### Issue: DOT File Not Generated Immediately

**Symptom**: After computing a solution, no DOT file appears in the `src/chinesePostman/graphTests/` directory until you exit the program.

**Solution**:

- **Action**: After computing the solution (Eulerian Circuit, Eulerian Trail, or Chinese Postman Circuit), choose to **return to the main menu** or **exit the program**. This action finalizes the DOT file generation process.

- **Reason**: The application writes the DOT file upon completing the computation. Exiting the menu ensures that the file writing process is finalized and the file is saved properly.

