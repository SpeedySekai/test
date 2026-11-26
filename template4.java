import java.io.*;
import java.util.*;

public class template4 {
    static final long INF = Long.MAX_VALUE;
    static int N;
    static List<Edge>[] graph;
    static List<Edge> edges;

    static class Edge {
        int from, to, weight;
        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    // Semua vertex menggunakan indeks 1..N.
    @SuppressWarnings("unchecked")
    static void initGraph(int n) {
        N = n;
        graph = (List<Edge>[]) new List<?>[N + 1];
        for (int i = 1; i <= N; i++) graph[i] = new ArrayList<>();
        edges = new ArrayList<>();
    }

    static void addEdge(int u, int v, int w, boolean undirected) {
        Edge e = new Edge(u, v, w);
        graph[u].add(e);
        edges.add(e); // simpan edge asli sekali untuk Kruskal
        if (undirected) graph[v].add(new Edge(v, u, w));
    }

    static class Paths {
        long[] dist;
        int[] prev;
        Paths() {
            dist = new long[N + 1];
            prev = new int[N + 1];
            Arrays.fill(dist, INF);
            Arrays.fill(prev, -1);
        }
    }

    // BFS: traversal + shortest path berdasarkan jumlah edge.
    static Paths bfs(int start) {
        Paths p = new Paths();
        Queue<Integer> q = new ArrayDeque<>();
        p.dist[start] = 0;
        q.add(start);
        while (!q.isEmpty()) {
            int u = q.poll();
            for (Edge e : graph[u]) {
                int v = e.to;
                if (p.dist[v] == INF) {
                    p.dist[v] = p.dist[u] + 1;
                    p.prev[v] = u;
                    q.add(v);
                }
            }
        }
        return p;
    }

    // DFS: gunakan visited yang sama pada semua recursive call.
    static void dfs(int u, boolean[] visited, List<Integer> order) {
        visited[u] = true;
        order.add(u);
        for (Edge e : graph[u])
            if (!visited[e.to]) dfs(e.to, visited, order);
    }

    static List<Integer> dfs(int start) {
        List<Integer> order = new ArrayList<>();
        dfs(start, new boolean[N + 1], order);
        return order;
    }

    // DFS iteratif: reverse push agar mengikuti urutan adjacency list.
    static List<Integer> dfsIterative(int start) {
        boolean[] visited = new boolean[N + 1];
        List<Integer> order = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (visited[u]) continue;
            visited[u] = true;
            order.add(u);
            for (int i = graph[u].size() - 1; i >= 0; i--) {
                int v = graph[u].get(i).to;
                if (!visited[v]) stack.push(v);
            }
        }
        return order;
    }

    // Hanya untuk undirected graph.
    static int connectedComponents() {
        boolean[] visited = new boolean[N + 1];
        int count = 0;
        Deque<Integer> stack = new ArrayDeque<>();
        for (int s = 1; s <= N; s++) {
            if (visited[s]) continue;
            count++;
            visited[s] = true;
            stack.push(s);
            while (!stack.isEmpty()) {
                int u = stack.pop();
                for (Edge e : graph[u]) {
                    if (!visited[e.to]) {
                        visited[e.to] = true;
                        stack.push(e.to);
                    }
                }
            }
        }
        return count;
    }

    static class State {
        int vertex;
        long dist;
        State(int vertex, long dist) {
            this.vertex = vertex;
            this.dist = dist;
        }
    }

    // Dijkstra: bobot non-negatif, lazy deletion.
    static Paths dijkstra(int start) {
        Paths p = new Paths();
        PriorityQueue<State> pq = new PriorityQueue<>(
            Comparator.comparingLong((State s) -> s.dist)
                      .thenComparingInt(s -> s.vertex));
        p.dist[start] = 0;
        pq.add(new State(start, 0));
        while (!pq.isEmpty()) {
            State cur = pq.poll();
            int u = cur.vertex;
            if (cur.dist != p.dist[u]) continue;
            for (Edge e : graph[u]) {
                long nd = p.dist[u] + e.weight;
                if (nd < p.dist[e.to]) {
                    p.dist[e.to] = nd;
                    p.prev[e.to] = u;
                    pq.add(new State(e.to, nd));
                }
            }
        }
        return p;
    }

    // 0-1 BFS: bobot hanya 0 atau 1.
    static Paths zeroOneBFS(int start) {
        Paths p = new Paths();
        Deque<State> dq = new ArrayDeque<>();
        p.dist[start] = 0;
        dq.addFirst(new State(start, 0));
        while (!dq.isEmpty()) {
            State cur = dq.pollFirst();
            int u = cur.vertex;
            if (cur.dist != p.dist[u]) continue;
            for (Edge e : graph[u]) {
                long nd = cur.dist + e.weight;
                if (nd < p.dist[e.to]) {
                    p.dist[e.to] = nd;
                    p.prev[e.to] = u;
                    State next = new State(e.to, nd);
                    if (e.weight == 0) dq.addFirst(next);
                    else dq.addLast(next);
                }
            }
        }
        return p;
    }

    static List<Integer> getPath(int dest, Paths p) {
        List<Integer> path = new ArrayList<>();
        if (p.dist[dest] == INF) return path;
        for (int v = dest; v != -1; v = p.prev[v]) path.add(v);
        Collections.reverse(path);
        return path;
    }

    // Kahn: directed graph. Hasil parsial berarti ada cycle.
    static List<Integer> topologicalSort() {
        int[] indegree = new int[N + 1];
        for (int u = 1; u <= N; u++)
            for (Edge e : graph[u]) indegree[e.to]++;
        Queue<Integer> q = new ArrayDeque<>();
        for (int u = 1; u <= N; u++)
            if (indegree[u] == 0) q.add(u);
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);
            for (Edge e : graph[u])
                if (--indegree[e.to] == 0) q.add(e.to);
        }
        return order;
    }

    static boolean hasDirectedCycle() {
        return topologicalSort().size() != N;
    }

    static class UFDS {
        int[] parent, size;
        int setCount;
        UFDS(int n) {
            parent = new int[n + 1];
            size = new int[n + 1];
            setCount = n;
            for (int i = 1; i <= n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }
        int find(int x) {
            if (parent[x] == x) return x;
            return parent[x] = find(parent[x]);
        }
        boolean isSameSet(int a, int b) {
            return find(a) == find(b);
        }
        boolean union(int a, int b) {
            a = find(a);
            b = find(b);
            if (a == b) return false;
            if (size[a] < size[b]) {
                int tmp = a; a = b; b = tmp;
            }
            parent[b] = a;
            size[a] += size[b];
            setCount--;
            return true;
        }
        int getSetCount() { return setCount; }
        int getSetSize(int x) { return size[find(x)]; }
    }

    // Undirected graph, setiap edge asli disimpan sekali.
    static boolean hasUndirectedCycle() {
        UFDS ds = new UFDS(N);
        for (Edge e : edges)
            if (!ds.union(e.from, e.to)) return true;
        return false;
    }

    static class MST {
        List<Edge> edges = new ArrayList<>();
        long totalWeight;
        void add(Edge e) {
            edges.add(e);
            totalWeight += e.weight;
        }
    }

    static final Comparator<Edge> BY_WEIGHT =
        Comparator.comparingInt((Edge e) -> e.weight);

    // Lazy Prim: undirected connected graph, PQ berisi edge.
    static void visit(int u, boolean[] inMST, PriorityQueue<Edge> pq) {
        inMST[u] = true;
        for (Edge e : graph[u])
            if (!inMST[e.to]) pq.add(e);
    }

    static MST primMST(int start) {
        boolean[] inMST = new boolean[N + 1];
        PriorityQueue<Edge> pq = new PriorityQueue<>(BY_WEIGHT);
        MST mst = new MST();
        visit(start, inMST, pq);
        while (!pq.isEmpty() && mst.edges.size() < N - 1) {
            Edge e = pq.poll();
            if (inMST[e.to]) continue;
            mst.add(e);
            visit(e.to, inMST, pq);
        }
        if (mst.edges.size() != N - 1)
            throw new IllegalStateException("Graph is disconnected");
        return mst;
    }

    // Kruskal: undirected connected graph, sorting + UFDS.
    static MST kruskalMST() {
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(BY_WEIGHT);
        UFDS ds = new UFDS(N);
        MST mst = new MST();
        for (Edge e : sorted) {
            if (ds.union(e.from, e.to)) mst.add(e);
            if (mst.edges.size() == N - 1) break;
        }
        if (mst.edges.size() != N - 1)
            throw new IllegalStateException("Graph is disconnected");
        return mst;
    }

    // Contoh main: format soal tiket dari template asli.
    // N M Q; M baris u v bobot(0/1); Q baris source dest tiket.
    // Ubah main sesuai soal lain. Query dikelompokkan per source.
    public static void main(String[] args) {
        InputReader in = new InputReader();
        PrintWriter out = new PrintWriter(System.out);
        int n = in.nextInt(), m = in.nextInt(), q = in.nextInt();
        initGraph(n);
        for (int i = 0; i < m; i++) {
            int u = in.nextInt(), v = in.nextInt(), w = in.nextInt();
            addEdge(u, v, w, true);
        }
        int[] dest = new int[q], tickets = new int[q], answer = new int[q];
        Map<Integer, List<Integer>> queries = new HashMap<>();
        for (int i = 0; i < q; i++) {
            int src = in.nextInt();
            dest[i] = in.nextInt();
            tickets[i] = in.nextInt();
            queries.computeIfAbsent(src, k -> new ArrayList<>()).add(i);
        }
        for (Map.Entry<Integer, List<Integer>> group : queries.entrySet()) {
            Paths p = zeroOneBFS(group.getKey());
            for (int i : group.getValue())
                answer[i] = p.dist[dest[i]] <= tickets[i] ? 1 : 0;
        }
        for (int a : answer) out.println(a);
        out.flush();
    }

    static class InputReader {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    String line = br.readLine();
                    if (line == null) throw new NoSuchElementException("End of input");
                    st = new StringTokenizer(line);
                } catch (IOException e) { throw new UncheckedIOException(e); }
            }
            return st.nextToken();
        }
        int nextInt() { return Integer.parseInt(next()); }
    }
}
