package graph.scc;


import java.util.*;

public class TarjanSCC {
    private Map<Integer, List<Integer>> graph;
    private int time;
    private Map<Integer, Integer> disc;
    private Map<Integer, Integer> low;
    private Deque<Integer> stack;
    private Set<Integer> inStack;
    private List<List<Integer>> sccList;

    public TarjanSCC(Map<Integer, List<Integer>> graph) {
        this.graph = graph;
        this.time = 0;
        this.disc = new HashMap<>();
        this.low = new HashMap<>();
        this.stack = new ArrayDeque<>();
        this.inStack = new HashSet<>();
        this.sccList = new ArrayList<>();
    }

    public List<List<Integer>> run() {
        for (Integer v : graph.keySet()) {
            if (!disc.containsKey(v)) {
                dfs(v);
            }
        }
        return sccList;
    }

    private void dfs(int u) {
        disc.put(u, time);
        low.put(u, time);
        time++;
        stack.push(u);
        inStack.add(u);

        for (int v : graph.getOrDefault(u, new ArrayList<>())) {
            if (!disc.containsKey(v)) {
                dfs(v);
                low.put(u, Math.min(low.get(u), low.get(v)));
            } else if (inStack.contains(v)) {
                low.put(u, Math.min(low.get(u), disc.get(v)));
            }
        }

        if (low.get(u).equals(disc.get(u))) {
            List<Integer> scc = new ArrayList<>();
            while (true) {
                int node = stack.pop();
                inStack.remove(node);
                scc.add(node);
                if (node == u) break;
            }
            sccList.add(scc);
        }
    }

    public Map<Integer, List<Integer>> buildCondensationGraph(List<List<Integer>> components) {
        Map<Integer, Integer> compId = new HashMap<>();
        for (int i = 0; i < components.size(); i++) {
            for (int v : components.get(i)) {
                compId.put(v, i);
            }
        }

        Map<Integer, List<Integer>> dag = new HashMap<>();
        for (int i = 0; i < components.size(); i++) {
            dag.put(i, new ArrayList<>());
        }

        for (int u : graph.keySet()) {
            for (int v : graph.getOrDefault(u, new ArrayList<>())) {
                int cu = compId.get(u);
                int cv = compId.get(v);
                if (cu != cv && !dag.get(cu).contains(cv)) {
                    dag.get(cu).add(cv);
                }
            }
        }

        return dag;
    }

    
    public static void main(String[] args) {
        Map<Integer, List<Integer>> g = new HashMap<>();
        g.put(0, Arrays.asList(1));
        g.put(1, Arrays.asList(2));
        g.put(2, Arrays.asList(0, 3));
        g.put(3, Arrays.asList(4));
        g.put(4, Arrays.asList(5));
        g.put(5, Arrays.asList(3));

        TarjanSCC tarjan = new TarjanSCC(g);
        List<List<Integer>> sccs = tarjan.run();

        System.out.println("Strongly Connected Components:");
        for (List<Integer> comp : sccs) {
            System.out.println(comp);
        }

        Map<Integer, List<Integer>> dag = tarjan.buildCondensationGraph(sccs);
        System.out.println("\nCondensation Graph (DAG):");
        for (int key : dag.keySet()) {
            System.out.println(key + " -> " + dag.get(key));
        }
    }
}

