package graph.dag;

import java.util.*;

public class DAGShortestPath {
    private Map<Integer, List<Edge>> graph;
    private int vertices;

    public DAGShortestPath(int vertices) {
        this.vertices = vertices;
        graph = new HashMap<>();
        for (int i = 0; i < vertices; i++) {
            graph.put(i, new ArrayList<>());
        }
    }

    public void addEdge(int u, int v, int w) {
        graph.get(u).add(new Edge(v, w));
    }

    public List<Integer> topologicalSort() {
        boolean[] visited = new boolean[vertices];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < vertices; i++) {
            if (!visited[i]) dfs(i, visited, stack);
        }
        List<Integer> order = new ArrayList<>();
        while (!stack.isEmpty()) order.add(stack.pop());
        return order;
    }

    private void dfs(int u, boolean[] visited, Stack<Integer> stack) {
        visited[u] = true;
        for (Edge e : graph.get(u)) {
            if (!visited[e.v]) dfs(e.v, visited, stack);
        }
        stack.push(u);
    }

    public int[] shortestPath(int src) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        List<Integer> topo = topologicalSort();
        for (int u : topo) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Edge e : graph.get(u)) {
                    if (dist[e.v] > dist[u] + e.w) {
                        dist[e.v] = dist[u] + e.w;
                    }
                }
            }
        }
        return dist;
    }

    static class Edge {
        int v, w;
        Edge(int v, int w) {
            this.v = v;
            this.w = w;
        }
    }

    public static void main(String[] args) {
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
        System.out.println("Shortest distances from vertex 1:");
        for (int i = 0; i < dist.length; i++) {
            System.out.println("To " + i + " = " + (dist[i] == Integer.MAX_VALUE ? "INF" : dist[i]));
        }
    }
}
