package chinesePostman;

import m1graphs2024.Node;


/*/

Key Features of the Pair Class
Constructor: Initializes the pair with two elements.
Getters: getFirst and getSecond return the first and second elements, respectively.
isEvenDeg: A static utility method to check if a node has an even degree.
Equality and Hashing: Overridden equals and hashCode methods ensure that pairs can be used in collections like HashSet or Map.
toString: Provides a readable representation of the pair, useful for debugging and logging.
 */


public class Pair<T1, T2> {
    private final T1 first;
    private final T2 second;

    // Constructor
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    // Getters
    public T1 getFirst() {
        return first;
    }

    public T2 getSecond() {
        return second;
    }

    // Utility to check if degree is even
    public static boolean isEvenDeg(Node node) {
        return node.degree() % 2 == 0;
    }

    // Override equals and hashCode to make this class usable in collections
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return first.equals(pair.first) && second.equals(pair.second);
    }

    @Override
    public int hashCode() {
        return 31 * first.hashCode() + second.hashCode();
    }

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
