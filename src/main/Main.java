package main;

import graph.scc.TarjanSCC;
import graph.topo.TopologicalSort;
import graph.dag.DAGShortestPath;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main {

    
    static class EdgeW { int to; int w; EdgeW(int t, int w){ this.to=t; this.w=w; } }

    
    private static String readAll(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
    }

    
    private static List<String> parseNodes(String json) {
        int pos = json.indexOf("\"nodes\"");
        if (pos < 0) return Collections.emptyList();
        int b = json.indexOf('[', pos);
        int e = json.indexOf(']', b);
        if (b < 0 || e < 0) return Collections.emptyList();
        String inside = json.substring(b+1, e).trim();
        List<String> nodes = new ArrayList<>();
        if (inside.isEmpty()) return nodes;
        int i = 0;
        while (i < inside.length()) {
            int q1 = inside.indexOf('"', i);
            if (q1 < 0) break;
            int q2 = inside.indexOf('"', q1+1);
            if (q2 < 0) break;
            nodes.add(inside.substring(q1+1, q2));
            i = q2+1;
            int comma = inside.indexOf(',', i);
            if (comma < 0) break;
            i = comma+1;
        }
        return nodes;
    }

    
    private static List<String[]> parseEdges(String json) {
        List<String[]> out = new ArrayList<>();
        int pos = json.indexOf("\"edges\"");
        if (pos < 0) return out;
        int b = json.indexOf('[', pos);
        if (b < 0) return out;
        
        int depth = 0;
        int end = -1;
        for (int i = b; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') depth++;
            else if (c == ']') {
                depth--;
                if (depth == 0) { end = i; break; }
            }
        }
        if (end < 0) return out;
        String block = json.substring(b+1, end).trim();
        int idx = 0;
        while (idx < block.length()) {
            int s = block.indexOf('[', idx);
            if (s < 0) break;
            int d = 0;
            int t = s;
            for (; t < block.length(); t++) {
                char c = block.charAt(t);
                if (c == '[') d++;
                else if (c == ']') {
                    d--;
                    if (d == 0) break;
                }
            }
            if (t >= block.length()) break;
            String item = block.substring(s+1, t).trim(); 
            
            List<String> parts = new ArrayList<>();
            StringBuilder cur = new StringBuilder();
            boolean inQ = false;
            for (int k = 0; k < item.length(); k++) {
                char c = item.charAt(k);
                if (c == '"') inQ = !inQ;
                if (c == ',' && !inQ) {
                    parts.add(cur.toString().trim());
                    cur.setLength(0);
                } else cur.append(c);
            }
            if (cur.length() > 0) parts.add(cur.toString().trim());
            String a = parts.size() > 0 ? parts.get(0).replaceAll("^\"|\"$", "") : "";
            String bstr = parts.size() > 1 ? parts.get(1).replaceAll("^\"|\"$", "") : "";
            String wstr = parts.size() > 2 ? parts.get(2) : "1";
            out.add(new String[]{a, bstr, wstr});
            idx = t + 1;
        }
        return out;
    }

    
    private static Map<Integer, List<Integer>> buildAdj(List<String> nodes, List<String[]> edges, Map<String,Integer> nameToId, List<int[]> weighted) {
        int n = nodes.size();
        Map<Integer, List<Integer>> adj = new HashMap<>();
        for (int i = 0; i < n; i++) adj.put(i, new ArrayList<>());
        for (String[] e : edges) {
            Integer u = nameToId.get(e[0]), v = nameToId.get(e[1]);
            int w = 1;
            try { w = Integer.parseInt(e[2]); } catch (Exception ex) { w = 1; }
            if (u==null || v==null) continue;
            adj.get(u).add(v);
            weighted.add(new int[]{u, v, w});
        }
        return adj;
    }

    
    private static Map<Integer, List<EdgeW>> buildCondensationWeighted(List<List<Integer>> comps, List<int[]> weightedEdges) {
        Map<Integer,Integer> compId = new HashMap<>();
        for (int i = 0; i < comps.size(); i++) for (int v : comps.get(i)) compId.put(v, i);
        Map<Integer, Map<Integer,Integer>> tmp = new HashMap<>();
        for (int[] e : weightedEdges) {
            int u = e[0], v = e[1], w = e[2];
            int cu = compId.get(u), cv = compId.get(v);
            if (cu == cv) continue;
            tmp.putIfAbsent(cu, new HashMap<>());
            Map<Integer,Integer> row = tmp.get(cu);
            if (!row.containsKey(cv) || w < row.get(cv)) row.put(cv, w);
        }
        Map<Integer, List<EdgeW>> dag = new HashMap<>();
        for (int i = 0; i < comps.size(); i++) dag.put(i, new ArrayList<>());
        for (Map.Entry<Integer, Map<Integer,Integer>> e : tmp.entrySet()) {
            int from = e.getKey();
            for (Map.Entry<Integer,Integer> cell : e.getValue().entrySet()) {
                dag.get(from).add(new EdgeW(cell.getKey(), cell.getValue()));
            }
        }
        return dag;
    }

    
    private static class KahnResult {
        List<Integer> order;
        long pushes;
        long pops;
    }
    private static KahnResult kahnWithCounts(Map<Integer, List<Integer>> dag) {
        Map<Integer,Integer> indeg = new HashMap<>();
        for (int u : dag.keySet()) {
            indeg.putIfAbsent(u, 0);
            for (int v : dag.get(u)) indeg.put(v, indeg.getOrDefault(v,0)+1);
        }
        Queue<Integer> q = new LinkedList<>();
        for (int v : indeg.keySet()) if (indeg.get(v)==0) q.offer(v);
        List<Integer> order = new ArrayList<>();
        long pushes = q.size();
        long pops = 0;
        while (!q.isEmpty()) {
            int u = q.poll(); pops++;
            order.add(u);
            for (int v : dag.getOrDefault(u, Collections.emptyList())) {
                indeg.put(v, indeg.get(v)-1);
                if (indeg.get(v)==0) { q.offer(v); pushes++; }
            }
        }
        KahnResult r = new KahnResult(); r.order = order; r.pushes = pushes; r.pops = pops; return r;
    }

    
    private static class SPRes { int[] dist; int[] prev; long relax; long timeNs; }
    private static SPRes shortestOnCondensation(int source, Map<Integer, List<EdgeW>> dag) {
        long t0 = System.nanoTime();
        
        Map<Integer, List<Integer>> simple = new HashMap<>();
        for (int u : dag.keySet()) {
            List<Integer> outs = new ArrayList<>();
            for (EdgeW e : dag.get(u)) outs.add(e.to);
            simple.put(u, outs);
        }
        TopologicalSort topo = new TopologicalSort(simple);
        List<Integer> order = topo.kahnSort();
        int n = dag.size();
        final int INF = Integer.MAX_VALUE/4;
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        int[] prev = new int[n];
        Arrays.fill(prev, -1);
        dist[source] = 0;
        long relax = 0;
        for (int u : order) {
            if (dist[u] == INF) continue;
            for (EdgeW e : dag.get(u)) {
                int v = e.to, w = e.w;
                if (dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                    relax++;
                }
            }
        }
        long t1 = System.nanoTime();
        SPRes r = new SPRes(); r.dist = dist; r.prev = prev; r.relax = relax; r.timeNs = t1 - t0; return r;
    }

    
    private static class LPRes { int[] dist; int[] prev; long timeNs; }
    private static LPRes longestOnCondensation(int source, Map<Integer, List<EdgeW>> dag) {
        long t0 = System.nanoTime();
        Map<Integer, List<Integer>> simple = new HashMap<>();
        for (int u : dag.keySet()) {
            List<Integer> outs = new ArrayList<>();
            for (EdgeW e : dag.get(u)) outs.add(e.to);
            simple.put(u, outs);
        }
        TopologicalSort topo = new TopologicalSort(simple);
        List<Integer> order = topo.kahnSort();
        int n = dag.size();
        final int NEG_INF = Integer.MIN_VALUE / 4;
        int[] dist = new int[n];
        Arrays.fill(dist, NEG_INF);
        int[] prev = new int[n];
        Arrays.fill(prev, -1);
        dist[source] = 0;
        for (int u : order) {
            if (dist[u] == NEG_INF) continue;
            for (EdgeW e : dag.get(u)) {
                int v = e.to, w = e.w;
                if (dist[v] < dist[u] + w) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                }
            }
        }
        long t1 = System.nanoTime();
        LPRes r = new LPRes(); r.dist = dist; r.prev = prev; r.timeNs = t1 - t0; return r;
    }

    private static List<Integer> reconstruct(int target, int[] prev) {
        if (target < 0) return Collections.emptyList();
        List<Integer> p = new ArrayList<>();
        for (int cur = target; cur != -1; cur = prev[cur]) p.add(cur);
        Collections.reverse(p);
        return p;
    }

    
    private static void processFile(String filepath) {
        try {
            String json = readAll(filepath);
            List<String> nodes = parseNodes(json);
            List<String[]> edges = parseEdges(json);
            Map<String,Integer> nameToId = new HashMap<>();
            for (int i = 0; i < nodes.size(); i++) nameToId.put(nodes.get(i), i);
            List<int[]> weighted = new ArrayList<>();
            Map<Integer, List<Integer>> adj = buildAdj(nodes, edges, nameToId, weighted);
            System.out.println("\n--- File: " + filepath + " ---");
            System.out.println("Nodes: " + nodes.size() + " Edges: " + weighted.size());

            long t0 = System.nanoTime();
            TarjanSCC tarjan = new TarjanSCC(adj);
            List<List<Integer>> comps = tarjan.run();
            long t1 = System.nanoTime();
            System.out.println("SCC count: " + comps.size());
            for (int i = 0; i < comps.size(); i++) {
                System.out.println("  comp " + i + " size=" + comps.get(i).size() + " nodes=" + comps.get(i));
            }
            System.out.println("Tarjan time (ns): " + (t1 - t0));

            Map<Integer, List<EdgeW>> cond = buildCondensationWeighted(comps, weighted);
            System.out.println("Condensation nodes: " + cond.size());
            for (int u : cond.keySet()) {
                System.out.print("  " + u + " -> ");
                List<String> outs = new ArrayList<>();
                for (EdgeW e : cond.get(u)) outs.add(e.to + "(w=" + e.w + ")");
                System.out.println(outs);
            }

            
            Map<Integer, List<Integer>> condSimple = new HashMap<>();
            for (int u : cond.keySet()) {
                List<Integer> out = new ArrayList<>();
                for (EdgeW e : cond.get(u)) out.add(e.to);
                condSimple.put(u, out);
            }
            long tk0 = System.nanoTime();
            KahnResult kr = kahnWithCounts(condSimple);
            long tk1 = System.nanoTime();
            System.out.println("Topo order (components): " + kr.order);
            System.out.println("Kahn pushes=" + kr.pushes + " pops=" + kr.pops + " time(ns)=" + (tk1-tk0));

            
            if (cond.size() > 0) {
                int source = kr.order.isEmpty() ? 0 : kr.order.get(0);
                SPRes spr = shortestOnCondensation(source, cond);
                System.out.println("Shortest (from comp " + source + "): dist=" + Arrays.toString(spr.dist));
                System.out.println("Shortest relaxations=" + spr.relax + " time(ns)=" + spr.timeNs);
                
                int best = -1;
                int bestDist = Integer.MAX_VALUE;
                for (int i = 0; i < spr.dist.length; i++) {
                    if (spr.dist[i] < bestDist) { bestDist = spr.dist[i]; best = i; }
                }
                if (best != -1 && spr.dist[best] < Integer.MAX_VALUE/4) {
                    List<Integer> pcomp = reconstruct(best, spr.prev);
                    System.out.println("One shortest path in components to " + best + " : " + pcomp);
                }

                LPRes lpr = longestOnCondensation(source, cond);
                int tgt = -1; int mx = Integer.MIN_VALUE;
                for (int i = 0; i < lpr.dist.length; i++) if (lpr.dist[i] > mx) { mx = lpr.dist[i]; tgt = i; }
                if (tgt != -1 && mx > Integer.MIN_VALUE/4) {
                    List<Integer> compPath = reconstruct(tgt, lpr.prev);
                    System.out.println("Critical path (components): " + compPath + " length=" + mx);
                    List<Integer> expanded = new ArrayList<>();
                    for (int cid : compPath) expanded.addAll(comps.get(cid));
                    System.out.println("Critical path (original tasks expanded): " + expanded);
                } else {
                    System.out.println("No reachable longest path found.");
                }
            }

        } catch (Exception ex) {
            System.err.println("Error processing file " + filepath + ": " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        
        if (args.length > 0) {
            for (String f : args) processFile(f);
            return;
        }
        
        try {
            Path dataDir = Paths.get("data");
            if (!Files.exists(dataDir) || !Files.isDirectory(dataDir)) {
                System.err.println("data/ folder not found in project root.");
                return;
            }
            DirectoryStream<Path> ds = Files.newDirectoryStream(dataDir, "*.json");
            for (Path p : ds) processFile(p.toString());
        } catch (IOException ioe) {
            System.err.println("Error scanning data/: " + ioe.getMessage());
        }
    }
}

