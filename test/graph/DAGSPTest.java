package test.graph;

import graph.dag.DAGShortestPath;

import java.util.*;

public class DAGSPTest {
    public static void main(String[] args) {
        testShortestPath();
        testCriticalPath();
    }

    private static void testShortestPath() {
        DAGShortestPath g = new DAGShortestPath(6);
        g.addEdge(0, 1, 5);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 3, 6);
        g.addEdge(1, 2, 2);
        g.addEdge(2, 4, 4);
        g.addEdge(2, 5, 2);
        g.addEdge(2, 3, 7);
        g.addEdge(3, 4, -1);
        g.addEdge(4, 5, -2);

        int[] dist = g.shortestPath(1);

        if (dist[3] == 6 && dist[5] == 4) {
            System.out.println("testShortestPath passed");
        } else {
            System.out.println("testShortestPath failed");
        }
    }

    private static void testCriticalPath() {
        DAGShortestPath g = new DAGShortestPath(5);
        g.addEdge(0, 1, 2);
        g.addEdge(0, 2, 3);
        g.addEdge(1, 3, 4);
        g.addEdge(2, 3, 1);
        g.addEdge(3, 4, 2);

        int[] dist = g.shortestPath(0);
        int maxDist = Arrays.stream(dist).max().orElse(-1);

        if (maxDist == 8) {
            System.out.println("testCriticalPath passed");
        } else {
            System.out.println("testCriticalPath failed");
        }
    }
}
