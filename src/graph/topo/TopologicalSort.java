package graph.topo;

import java.util.*;

public class TopologicalSort {
    private Map<Integer, List<Integer>> graph;

    public TopologicalSort(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
    }

    public List<Integer> kahnSort() {
        Map<Integer, Integer> indegree = new HashMap<>();
        for (int u : graph.keySet()) {
            indegree.putIfAbsent(u, 0);
            for (int v : graph.get(u)) {
                indegree.put(v, indegree.getOrDefault(v, 0) + 1);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int v : indegree.keySet()) {
            if (indegree.get(v) == 0) {
                queue.offer(v);
            }
        }

        List<Integer> order = new ArrayList<>();
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order.add(u);
            for (int v : graph.getOrDefault(u, new ArrayList<>())) {
                indegree.put(v, indegree.get(v) - 1);
                if (indegree.get(v) == 0) {
                    queue.offer(v);
                }
            }
        }

        return order;
    }

    public static void main(String[] args) {
        Map<Integer, List<Integer>> dag = new HashMap<>();
        dag.put(0, Arrays.asList(1, 2));
        dag.put(1, Arrays.asList(3));
        dag.put(2, Arrays.asList(3));
        dag.put(3, Arrays.asList());

        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> order = topo.kahnSort();

        System.out.println("Topological Order: " + order);
    }
}
