
import java.io.*;
import java.util.*;

public class Template2 {
    static final InputReader in = new InputReader();
    static final PrintWriter out = new PrintWriter(System.out);
    static final long INF = Long.MAX_VALUE / 4;

    // Menyimpan tetangga dan waktu tempuh
    static class Edge {
        int to;
        int weight;

        Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    // Menyimpan node dan jaraknya untuk Dijkstra
    static class State {
        int node;
        long dist;

        State(int node, long dist) {
            this.node = node;
            this.dist = dist;
        }
    }

    static List<Edge>[] graph;
    static long[][] cache;

    public static void main(String[] args) {
        int V = in.nextInt();
        int E = in.nextInt();

        // 1. Membuat adjacency list
        graph = new ArrayList[V + 1];

        for (int i = 1; i <= V; i++) {
            graph[i] = new ArrayList<>();
        }

        // 2. Memasukkan jalan ke graf
        for (int i = 0; i < E; i++) {
            int X = in.nextInt();
            int Y = in.nextInt();
            int W = in.nextInt();

            // Undirected graph
            graph[X].add(new Edge(Y, W));
            graph[Y].add(new Edge(X, W));
        }

        int D = in.nextInt();
        long M = in.nextLong();

        // Cache hasil Dijkstra per halte asal
        cache = new long[V + 1][];

        // Jarak dari sekolah (halte 1) ke semua halte
        cache[1] = dijkstra(1);

        // 3. Memproses setiap hari
        for (int day = 0; day < D; day++) {
            int N = in.nextInt();

            int current = 1;
            int lastPickup = -1;
            long totalTime = 0;
            boolean stopped = false;

            for (int j = 0; j < N; j++) {
                int next = in.nextInt();

                // Input tetap harus dibaca semua
                if (stopped) {
                    continue;
                }

                // Jarak terpendek current -> next
                long go = getDistance(current, next);

                // Jarak terpendek next -> sekolah
                long back = getDistance(next, 1);

                // Tidak ada jalur yang memungkinkan
                if (go == INF || back == INF) {
                    stopped = true;
                    continue;
                }

                // Cek apakah masih bisa pulang tepat waktu
                if (totalTime + go + back <= M) {
                    totalTime += go;
                    current = next;
                    lastPickup = next;
                } else {
                    stopped = true;
                }
            }

            // 4. Mencetak hasil
            if (lastPickup == -1) {
                out.println("-1 -1");
            } else {
                totalTime += getDistance(current, 1);
                out.println(totalTime + " " + lastPickup);
            }
        }

        out.flush();
    }

    // Dijkstra: jarak terpendek dari start
    static long[] dijkstra(int start) {
        int V = graph.length - 1;

        long[] dist = new long[V + 1];
        Arrays.fill(dist, INF);

        PriorityQueue<State> pq = new PriorityQueue<>(
            (a, b) -> Long.compare(a.dist, b.dist)
        );

        dist[start] = 0;
        pq.add(new State(start, 0));

        while (!pq.isEmpty()) {
            State current = pq.poll();

            int u = current.node;
            long d = current.dist;

            // Abaikan data lama
            if (d != dist[u]) {
                continue;
            }

            // Periksa seluruh tetangga
            for (Edge edge : graph[u]) {
                int v = edge.to;
                long newDist = d + edge.weight;

                // Relaxation
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.add(new State(v, newDist));
                }
            }
        }

        return dist;
    }

    // Mengambil jarak terpendek dari a ke b
    static long getDistance(int a, int b) {
        if (a == b) {
            return 0;
        }

        // Jika belum dihitung, jalankan Dijkstra
        if (cache[a] == null) {
            cache[a] = dijkstra(a);
        }

        return cache[a][b];
    }

    // Fast Input
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

        long nextLong() {
            return Long.parseLong(next());
        }
    }
}
