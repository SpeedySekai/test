
import java.io.*;
import java.util.*;

public class template5 {
    static final long INF = Long.MAX_VALUE;
    static int N;
    static List<Edge>[] graph;
    static List<Edge> edges;

    // ==================== EDGE ====================

    static class Edge {
        int from, to, weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }

    static class State {
        int vertex;
        long dist;

        State(int vertex, long dist) {
            this.vertex = vertex;
            this.dist = dist;
        }
    }

    // ==================== BFS ====================
    // Unweighted graph
    // Time: O(V + E)

    static long[] bfs(int start) {
        long[] dist = new long[N + 1];
        Arrays.fill(dist, INF);

        Queue<Integer> q = new ArrayDeque<>();

        dist[start] = 0;
        q.add(start);

        while (!q.isEmpty()) {
            int u = q.poll();

            for (Edge e : graph[u]) {
                int v = e.to;

                if (dist[v] == INF) {
                    dist[v] = dist[u] + 1;
                    q.add(v);
                }
            }
        }

        return dist;
    }

    // ==================== DFS RECURSIVE ====================
    // Time: O(V + E)

    static void dfs(int u, boolean[] visited) {
        visited[u] = true;

        for (Edge e : graph[u]) {
            if (!visited[e.to]) {
                dfs(e.to, visited);
            }
        }
    }

    // ==================== DFS ITERATIVE ====================
    // Time: O(V + E)

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

            // Reverse push agar urutan sama dengan recursive DFS
            for (int i = graph[u].size() - 1; i >= 0; i--) {
                int v = graph[u].get(i).to;

                if (!visited[v]) {
                    stack.push(v);
                }
            }
        }

        return order;
    }

    // ==================== CONNECTED COMPONENTS ====================
    // Undirected graph
    // Time: O(V + E)

    static int connectedComponents() {
        boolean[] visited = new boolean[N + 1];
        Queue<Integer> q = new ArrayDeque<>();
        int count = 0;

        for (int i = 1; i <= N; i++) {
            if (visited[i]) continue;

            count++;
            visited[i] = true;
            q.add(i);

            while (!q.isEmpty()) {
                int u = q.poll();

                for (Edge e : graph[u]) {
                    int v = e.to;

                    if (!visited[v]) {
                        visited[v] = true;
                        q.add(v);
                    }
                }
            }
        }

        return count;
    }

    // ==================== DIJKSTRA ====================
    // Non-negative weighted graph
    // Time: O(V + E log E)

    static long[] dijkstra(int start) {
        long[] dist = new long[N + 1];
        Arrays.fill(dist, INF);

        PriorityQueue<State> pq = new PriorityQueue<>(
            Comparator.comparingLong(s -> s.dist)
        );

        dist[start] = 0;
        pq.add(new State(start, 0));

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            int u = cur.vertex;

            // Lazy deletion
            if (cur.dist != dist[u]) continue;

            for (Edge e : graph[u]) {
                int v = e.to;
                long newDist = dist[u] + e.weight;

                // Relaxation
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.add(new State(v, newDist));
                }
            }
        }

        return dist;
    }

    // ==================== DIJKSTRA PATH ====================
    // Mengembalikan rute shortest path
    // Time: O(V + E log E)

    static List<Integer> dijkstraPath(int start, int dest) {
        long[] dist = new long[N + 1];
        int[] prev = new int[N + 1];

        Arrays.fill(dist, INF);
        Arrays.fill(prev, -1);

        PriorityQueue<State> pq = new PriorityQueue<>(
            Comparator.comparingLong(s -> s.dist)
        );

        dist[start] = 0;
        pq.add(new State(start, 0));

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            int u = cur.vertex;

            if (cur.dist != dist[u]) continue;

            if (u == dest) break;

            for (Edge e : graph[u]) {
                int v = e.to;
                long newDist = dist[u] + e.weight;

                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    prev[v] = u;

                    pq.add(new State(v, newDist));
                }
            }
        }

        // Rekonstruksi shortest path
        List<Integer> path = new ArrayList<>();

        if (dist[dest] == INF) return path;

        for (int v = dest; v != -1; v = prev[v]) {
            path.add(v);
        }

        Collections.reverse(path);

        return path;
    }

    // ==================== 0-1 BFS ====================
    // Bobot edge hanya 0 atau 1
    // Time: O(V + E)

    static long[] zeroOneBFS(int start) {
        long[] dist = new long[N + 1];
        Arrays.fill(dist, INF);

        Deque<State> dq = new ArrayDeque<>();

        dist[start] = 0;
        dq.addFirst(new State(start, 0));

        while (!dq.isEmpty()) {
            State cur = dq.pollFirst();
            int u = cur.vertex;

            if (cur.dist != dist[u]) continue;

            for (Edge e : graph[u]) {
                int v = e.to;
                long newDist = dist[u] + e.weight;

                if (newDist < dist[v]) {
                    dist[v] = newDist;

                    if (e.weight == 0) {
                        dq.addFirst(new State(v, newDist));
                    } else {
                        dq.addLast(new State(v, newDist));
                    }
                }
            }
        }

        return dist;
    }

    // ==================== TOPOLOGICAL SORT ====================
    // Kahn's Algorithm
    // Directed graph
    // Time: O(V + E)

    static List<Integer> topologicalSort() {
        int[] indegree = new int[N + 1];
        Queue<Integer> q = new ArrayDeque<>();
        List<Integer> order = new ArrayList<>();

        // Hitung indegree setiap vertex
        for (int u = 1; u <= N; u++) {
            for (Edge e : graph[u]) {
                indegree[e.to]++;
            }
        }

        // Masukkan semua vertex dengan indegree 0
        for (int u = 1; u <= N; u++) {
            if (indegree[u] == 0) {
                q.add(u);
            }
        }

        while (!q.isEmpty()) {
            int u = q.poll();
            order.add(u);

            for (Edge e : graph[u]) {
                indegree[e.to]--;

                if (indegree[e.to] == 0) {
                    q.add(e.to);
                }
            }
        }

        // Jika order.size() != N, terdapat cycle
        return order;
    }

    // ==================== UFDS ====================
    // Path compression + Union by size
    // Find/Union: amortized O(alpha(V))

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

        // Find dengan path compression
        int find(int x) {
            if (parent[x] == x) return x;

            return parent[x] = find(parent[x]);
        }

        // Apakah berada pada set yang sama?
        boolean isSameSet(int a, int b) {
            return find(a) == find(b);
        }

        // Union by size
        boolean union(int a, int b) {
            a = find(a);
            b = find(b);

            if (a == b) return false;

            if (size[a] < size[b]) {
                int temp = a;
                a = b;
                b = temp;
            }

            parent[b] = a;
            size[a] += size[b];
            setCount--;

            return true;
        }

        int getSetCount() {
            return setCount;
        }

        int getSetSize(int x) {
            return size[find(x)];
        }
    }

    // ==================== UNDIRECTED CYCLE DETECTION ====================
    // Menggunakan UFDS
    // Time: O(V + E alpha(V))

    static boolean hasUndirectedCycle() {
        UFDS ds = new UFDS(N);

        for (Edge e : edges) {
            if (!ds.union(e.from, e.to)) {
                return true;
            }
        }

        return false;
    }

    // ==================== PRIM MST ====================
    // Lazy Prim
    // Undirected connected weighted graph
    // Time: O(V + E log E)

    static List<Edge> primMST(int start) {
        boolean[] inMST = new boolean[N + 1];
        List<Edge> result = new ArrayList<>();

        PriorityQueue<Edge> pq = new PriorityQueue<>(
            Comparator.comparingInt(e -> e.weight)
        );

        // Masukkan vertex awal ke MST
        inMST[start] = true;

        for (Edge e : graph[start]) {
            if (!inMST[e.to]) {
                pq.add(e);
            }
        }

        while (!pq.isEmpty() && result.size() < N - 1) {
            Edge e = pq.poll();

            // Skip jika bukan crossing edge
            if (inMST[e.to]) continue;

            result.add(e);

            int u = e.to;
            inMST[u] = true;

            // Tambahkan crossing edge baru
            for (Edge next : graph[u]) {
                if (!inMST[next.to]) {
                    pq.add(next);
                }
            }
        }

        if (result.size() != N - 1) {
            throw new IllegalStateException("Graph is disconnected");
        }

        return result;
    }

    // ==================== KRUSKAL MST ====================
    // Sorting + UFDS
    // Undirected connected weighted graph
    // Time: O(V + E log E)

    static List<Edge> kruskalMST() {
        List<Edge> sorted = new ArrayList<>(edges);
        List<Edge> result = new ArrayList<>();

        sorted.sort(Comparator.comparingInt(e -> e.weight));

        UFDS ds = new UFDS(N);

        for (Edge e : sorted) {
            // Ambil edge jika tidak membentuk cycle
            if (ds.union(e.from, e.to)) {
                result.add(e);
            }

            if (result.size() == N - 1) break;
        }

        if (result.size() != N - 1) {
            throw new IllegalStateException("Graph is disconnected");
        }

        return result;
    }

    // ==================== MAIN ====================

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        InputReader in = new InputReader();
        PrintWriter out = new PrintWriter(System.out);

        N = in.nextInt();
        int M = in.nextInt();

        // Inisialisasi graph
        graph = new ArrayList[N + 1];
        edges = new ArrayList<>();

        for (int i = 1; i <= N; i++) {
            graph[i] = new ArrayList<>();
        }

        // Input edge
        for (int i = 0; i < M; i++) {
            int u = in.nextInt();
            int v = in.nextInt();
            int w = in.nextInt();

            Edge e = new Edge(u, v, w);

            graph[u].add(e);
            edges.add(e);

            // Hapus baris berikut jika directed graph
            graph[v].add(new Edge(v, u, w));
        }

        // ===== UBAH SESUAI KEBUTUHAN SOAL =====

        List<Edge> mst = primMST(1);
        long totalWeight = 0;

        for (Edge e : mst) {
            totalWeight += e.weight;
        }

        out.println(totalWeight);

        out.flush();
    }

    // ==================== INPUT READER ====================

    static class InputReader {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in)
        );

        StringTokenizer st;

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    String line = br.readLine();

                    if (line == null) {
                        throw new NoSuchElementException();
                    }

                    st = new StringTokenizer(line);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }

            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }

        long nextLong() {
            return Long.parseLong(next());
        }
    }
}
