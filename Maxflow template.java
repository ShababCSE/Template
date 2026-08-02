import java.util.*;

public class MaximumLinkDisjointPaths {

    // Helper method using BFS to find an augmenting path from source (s) to destination (t)
    // Returns true if a path exists where flow can be sent, false otherwise.
    private static boolean bfs(int s, int t, int[] parent, int[][] capacity, List<List<Integer>> adj) {
        // Fill parent array with -1 to mark all nodes as unvisited
        Arrays.fill(parent, -1);
        parent[s] = -2; // Mark source as visited

        Queue<Integer> queue = new LinkedList<>();
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();

            // Check all neighboring nodes of u
            for (int v : adj.get(u)) {
                // If node v is not visited AND there is remaining capacity from u to v
                if (parent[v] == -1 && capacity[u][v] > 0) {
                    parent[v] = u; // Remember that we came to 'v' from 'u'

                    // If we reached the destination, we found a valid path
                    if (v == t) {
                        return true;
                    }
                    queue.add(v);
                }
            }
        }
        return false; // No path found from s to t
    }

    public static void main(String[] args) {
        /*
         * OBJECTIVE:
         * Find the maximum number of link-disjoint paths from source node 's' to destination node 't'.
         * If 't' cannot be reached from 's', output -1.
         *
         * GIVEN:
         * N = number of nodes, M = number of directed links.
         * s = source node, t = destination node.
         * M pairs of (u, v) representing directed links from u to v.
         */
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) return;

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int s = scanner.nextInt();
        int t = scanner.nextInt();

        // capacity[u][v] stores how many paths can still pass from u to v
        int[][] capacity = new int[n][n];

        // Adjacency list to keep track of neighbors for fast BFS traversal
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        // Read all M directed links
        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();

            // Each link can be used by at most 1 path, so we add 1 to capacity
            capacity[u][v] += 1;

            // Add both directions to the adjacency list so BFS can inspect
            // both forward edges and reverse residual edges
            adj.get(u).add(v);
            adj.get(v).add(u);
        }

        int maxPaths = 0;
        int[] parent = new int[n];

        // While there is an available path from s to t in the residual graph
        while (bfs(s, t, parent, capacity, adj)) {
            // Since every edge capacity is 1, each augmenting path adds exactly 1 to our total paths
            maxPaths++;

            // Backtrack from destination 't' to source 's' using the parent array
            // and update the residual capacities
            int current = t;
            while (current != s) {
                int prev = parent[current];
                capacity[prev][current] -= 1; // Use up forward link capacity
                capacity[current][prev] += 1; // Create reverse residual capacity
                current = prev;
            }
        }

        // If no paths could be found at all, t is not reachable from s -> print -1
        if (maxPaths == 0) {
            System.out.println(-1);
        } else {
            System.out.println(maxPaths);
        }

        scanner.close();
    }
             }
