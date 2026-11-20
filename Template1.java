import java.io.*;
import java.util.*;

public class Template1 {
    final static InputReader in = new InputReader(System.in);
    final static PrintWriter out = new PrintWriter(System.out);

    // Edge: menyimpan neighbor dan streak (weight)
    static class Edge {
        int to;
        int streak;

        Edge(int to, int streak) {
            this.to = to;
            this.streak = streak;
        }
    }

    static void handleT(List<Edge>[] graph, int N, int U) {
        List<Edge> friends = new ArrayList<>(graph[U]);

        if (friends.isEmpty()) {
            out.println(-1);
            return;
        }

        friends.sort((a, b) -> {
            if (a.streak != b.streak) {
                return Integer.compare(b.streak, a.streak);
            }
            return Integer.compare(a.to, b.to);
        });

        int limit = Math.min(N, friends.size());

        for (int i = 0; i < limit; i++) {
            if (i > 0) out.print(" ");
            out.print(friends.get(i).to);
        }
        out.println();
    }

    static void handleC(List<Edge>[] graph, int M, int U) {
        boolean[] visited = new boolean[graph.length];
        int[] distance = new int[graph.length];

        Queue<Integer> queue = new ArrayDeque<>();

        queue.add(U);
        visited[U] = true;

        int count = 0;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            if (distance[current] == M) {
                continue;
            }

            for (Edge edge : graph[current]) {
                int neighbor = edge.to;

                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    distance[neighbor] = distance[current] + 1;

                    count++;
                    queue.add(neighbor);
                }
            }
        }

        out.println(count);
    }

    static void handleS(List<Edge>[] graph, int P, int Q) {
        if (P == Q) {
            out.println(0);
            return;
        }

        int[] best = new int[graph.length];

        PriorityQueue<int[]> pq = new PriorityQueue<>(
            (a, b) -> Integer.compare(b[1], a[1])
        );

        best[P] = Integer.MAX_VALUE;
        pq.add(new int[]{P, best[P]});

        while (!pq.isEmpty()) {
            int[] current = pq.poll();

            int u = current[0];
            int bottleneck = current[1];

            if (bottleneck < best[u]) {
                continue;
            }

            if (u == Q) {
                out.println(best[Q]);
                return;
            }

            for (Edge edge : graph[u]) {
                int v = edge.to;

                int newBottleneck =
                    Math.min(best[u], edge.streak);

                if (newBottleneck > best[v]) {
                    best[v] = newBottleneck;
                    pq.add(new int[]{v, newBottleneck});
                }
            }
        }

        out.println(-1);
    }

    public static void main(String[] args) {
        // read number of users
        int U = in.nextInteger();
        int C = in.nextInteger();

        @SuppressWarnings("unchecked") // Biar gaada warning di declaration graph
        List<Edge>[] graph = new ArrayList[U + 1];   // 1-based: graph[1..U]
        for (int i = 1; i <= U; i++) {
            graph[i] = new ArrayList<>();
        }

        for (int i = 0; i < C; i++) {
            int A = in.nextInteger();
            int S = in.nextInteger();
            int B = in.nextInteger();

            // undirected
            graph[A].add(new Edge(B, S));
            graph[B].add(new Edge(A, S));
        }

        int Q = in.nextInteger();

        // read queries
        for (int i = 0; i < Q; i++) {
            char queryType = in.next().charAt(0);
            switch (queryType) {
                case 'T':
                    int N = in.nextInteger();
                    int Uquery = in.nextInteger();
                    handleT(graph, N, Uquery);
                    break;

                case 'C':
                    int M = in.nextInteger();
                    Uquery = in.nextInteger();
                    handleC(graph, M, Uquery);
                    break;

                case 'S':
                    int P = in.nextInteger();
                    int Quser = in.nextInteger();
                    handleS(graph, P, Quser);
                    break;
            }
        }

        out.flush();
    }

    // taken from https://codeforces.com/submissions/Petr
    // together with PrintWriter, these input-output (IO) is much faster than the
    // usual Scanner(System.in) and System.out
    // please use these classes to avoid your fast algorithm gets Time Limit
    // Exceeded caused by slow input-output (IO)
    static class InputReader {
        public BufferedReader reader;
        public StringTokenizer tokenizer;

        public InputReader(InputStream stream) {
            reader = new BufferedReader(new InputStreamReader(stream), 32768);
            tokenizer = null;
        }

        public String next() {
            while (tokenizer == null || !tokenizer.hasMoreTokens()) {
                try {
                    tokenizer = new StringTokenizer(reader.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return tokenizer.nextToken();
        }

        public int nextInteger() {
            return Integer.parseInt(next());
        }

        public long nextLong() {
            return Long.parseLong(next());
        }
    }
}