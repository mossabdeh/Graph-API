package m1graphs2024;

public class DotFileReader {

    public final String DIRECTED = "digraph";
    public final String UNDIRECTED = "graph";
    public final String DIR_EDGE = " -> "; /* To ensure the surrounding spaces*/
    public final String UNDIR_EDGE = " -- "; /* To ensure the surrounding spaces*/

    public final String LENGTH = "len"; /* For the edge weight but DOT won't show it   */
    public final String WEIGHT = "label"; /* For DOT to explicitly show the len value(weight)  */
    public final String DOT_FILE_EXTENSION = ".dot";



}
