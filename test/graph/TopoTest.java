package test.graph;

import graph.topo.TopologicalSort;

import java.util.*;

public class TopoTest {
    public static void main(String[] args) {
        testSimpleDAG();
        testDAGWithMultipleRoots();
    }

    private static void testSimpleDAG() {
        Map<Integer, List<Integer>> dag = new HashMap<>();
        dag.put(0, Arrays.asList(1, 2));
        dag.put(1, Arrays.asList(3));
        dag.put(2, Arrays.asList(3));
        dag.put(3, Arrays.asList());

        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> order = topo.kahnSort();

        if (order.indexOf(0) < order.indexOf(1) && order.indexOf(0) < order.indexOf(2) && 
            order.indexOf(1) < order.indexOf(3) && order.indexOf(2) < order.indexOf(3)) {
            System.out.println("testSimpleDAG passed");
        } else {
            System.out.println("testSimpleDAG failed");
        }
    }

    private static void testDAGWithMultipleRoots() {
        Map<Integer, List<Integer>> dag = new HashMap<>();
        dag.put(0, Arrays.asList(2));
        dag.put(1, Arrays.asList(2));
        dag.put(2, Arrays.asList(3));
        dag.put(3, Arrays.asList());

        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> order = topo.kahnSort();

        if (order.indexOf(0) < order.indexOf(2) && order.indexOf(1) < order.indexOf(2) &&
            order.indexOf(2) < order.indexOf(3)) {
            System.out.println("testDAGWithMultipleRoots passed");
        } else {
            System.out.println("testDAGWithMultipleRoots failed");
        }
    }
}
