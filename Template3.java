
import java.io.*;
import java.util.*;

public class Template3 {
    static final InputReader in = new InputReader();
    static final PrintWriter out = new PrintWriter(System.out);

    static final int INF = Integer.MAX_VALUE;

    // Pilih: "BFS", "DFS", atau "01BFS"
    static final String ALGORITHM = "01BFS";

    static class Edge {
        int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Untuk menyimpan kota dan sisa tiket
    static class Pair {
        int first;
        int second;

        Pair(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    static List<Edge>[] graph;

    // =========================================
    // 1. BFS BIASA
    // =========================================
    static int bfs(int initialSrc, int dst, int ticket,
                   boolean[][] visited) {

        Queue<Pair> frontier = new ArrayDeque<>();

        frontier.add(new Pair(initialSrc, ticket));
        visited[initialSrc][ticket] = true;

        while (!frontier.isEmpty()) {
            Pair current = frontier.remove();

            int src = current.first;
            int remTicket = current.second;

            if (src == dst) {
                return 1;
            }

            for (Edge edge : graph[src]) {
                int nextHop = edge.to;
                int edgeType = edge.weight;

                // Hitung tiket setelah melewati jalan
                int nextTicket = remTicket - edgeType;

                if (nextTicket >= 0 &&
                    !visited[nextHop][nextTicket]) {

                    visited[nextHop][nextTicket] = true;

                    frontier.add(
                        new Pair(nextHop, nextTicket)
                    );
                }
            }
        }

        return 0;
    }

    // =========================================
    // 2. DFS REKURSIF
    // =========================================
    static int dfs(int src, int dst, int ticket,
                   boolean[][] visited) {

        if (src == dst) {
            return 1;
        }

        if (visited[src][ticket]) {
            return 0;
        }

        visited[src][ticket] = true;

        for (Edge edge : graph[src]) {
            int nextHop = edge.to;
            int edgeType = edge.weight;

            int nextTicket = ticket - edgeType;

            if (nextTicket >= 0 &&
                !visited[nextHop][nextTicket]) {

                int ret = dfs(
                    nextHop, dst, nextTicket, visited
                );

                if (ret == 1) {
                    return 1;
                }
            }
        }

        return 0;
    }

    // =========================================
    // 3. 0-1 BFS
    // =========================================
    static int[] zeroOneBFS(int start, int N) {
        int[] dist = new int[N + 1];
        Arrays.fill(dist, INF);

        Deque<Pair> deque = new ArrayDeque<>();

        dist[start] = 0;
        deque.addFirst(new Pair(start, 0));

        while (!deque.isEmpty()) {
            Pair current = deque.pollFirst();

            int u = current.first;
            int d = current.second;

            // Abaikan state lama
            if (d != dist[u]) {
                continue;
            }

            for (Edge edge : graph[u]) {
                int v = edge.to;
                int weight = edge.weight;

                int newDist = dist[u] + weight;

                if (newDist < dist[v]) {
                    dist[v] = newDist;

                    if (weight == 0) {
                        deque.addFirst(new Pair(v, newDist));
                    } else {
                        deque.addLast(new Pair(v, newDist));
                    }
                }
            }
        }

        return dist;
    }

    // =========================================
    // MAIN
    // =========================================
    public static void main(String[] args) {
        int N = in.nextInt();
        int M = in.nextInt();
        int Q = in.nextInt();

        graph = new ArrayList[N + 1];

        for (int i = 1; i <= N; i++) {
            graph[i] = new ArrayList<>();
        }

        // Membaca jalan
        for (int i = 0; i < M; i++) {
            int U = in.nextInt();
            int V = in.nextInt();
            int T = in.nextInt();

            graph[U].add(new Edge(V, T));
            graph[V].add(new Edge(U, T));
        }

        // Simpan semua query
        int[] source = new int[Q];
        int[] destination = new int[Q];
        int[] tickets = new int[Q];

        for (int i = 0; i < Q; i++) {
            source[i] = in.nextInt();
            destination[i] = in.nextInt();
            tickets[i] = in.nextInt();
        }

        // Pilih metode penyelesaian
        if (ALGORITHM.equals("BFS")) {

            for (int i = 0; i < Q; i++) {
                boolean[][] visited =
                    new boolean[N + 1][tickets[i] + 1];

                int result = bfs(
                    source[i],
                    destination[i],
                    tickets[i],
                    visited
                );

                out.println(result);
            }

        } else if (ALGORITHM.equals("DFS")) {

            for (int i = 0; i < Q; i++) {
                boolean[][] visited =
                    new boolean[N + 1][tickets[i] + 1];

                int result = dfs(
                    source[i],
                    destination[i],
                    tickets[i],
                    visited
                );

                out.println(result);
            }

        } else if (ALGORITHM.equals("01BFS")) {

            // Kelompokkan query berdasarkan kota asal
            List<Integer>[] queries = new ArrayList[N + 1];

            for (int i = 1; i <= N; i++) {
                queries[i] = new ArrayList<>();
            }

            for (int i = 0; i < Q; i++) {
                queries[source[i]].add(i);
            }

            int[] answer = new int[Q];

            for (int start = 1; start <= N; start++) {
                if (queries[start].isEmpty()) {
                    continue;
                }

                int[] dist = zeroOneBFS(start, N);

                for (int index : queries[start]) {
                    if (dist[destination[index]]
                        <= tickets[index]) {

                        answer[index] = 1;
                    } else {
                        answer[index] = 0;
                    }
                }
            }

            for (int i = 0; i < Q; i++) {
                out.println(answer[i]);
            }
        }

        out.flush();
    }

    // =========================================
    // FAST INPUT
    // =========================================
    static class InputReader {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in)
        );
        StringTokenizer st;

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }
}
