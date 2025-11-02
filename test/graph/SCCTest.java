package test.graph;

import graph.scc.TarjanSCC;

import java.util.*;

public class SCCTest {
    public static void main(String[] args) {
        testSingleSCC();
        testMultipleSCCs();
    }

    private static void testSingleSCC() {
        Map<Integer, List<Integer>> g = new HashMap<>();
        g.put(0, Arrays.asList(1));
        g.put(1, Arrays.asList(2));
        g.put(2, Arrays.asList(0));

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.run();

        if (sccs.size() == 1 && sccs.get(0).containsAll(Arrays.asList(0, 1, 2))) {
            System.out.println("testSingleSCC passed");
        } else {
            System.out.println("testSingleSCC failed");
        }
    }

    private static void testMultipleSCCs() {
        Map<Integer, List<Integer>> g = new HashMap<>();
        g.put(0, Arrays.asList(1));
        g.put(1, Arrays.asList(2));
        g.put(2, Arrays.asList(0, 3));
        g.put(3, Arrays.asList(4));
        g.put(4, Arrays.asList(5));
        g.put(5, Arrays.asList(3));

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.run();

        if (sccs.size() == 3) {
            System.out.println("testMultipleSCCs passed");
        } else {
            System.out.println("testMultipleSCCs failed");
        }
    }
}
