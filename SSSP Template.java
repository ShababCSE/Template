import java.util.*;

public class NearestEmergencyCenter {

    /*
     * OBJECTIVE: Find the shortest time to reach ANY emergency center from EVERY city.
     * GIVEN: n cities, m one-way roads (u -> v with time w), and k emergency centers.
     */

    // A simple helper class to represent a road to a destination
    static class Edge implements Comparable<Edge> {
        int node;
        long time; // Using 'long' because time can add up to very large numbers

        public Edge(int node, long time) {
            this.node = node;
            this.time = time;
        }

        // This tells our Priority Queue to always pick the road with the shortest time first
        @Override
        public int compareTo(Edge other) {
            return Long.compare(this.time, other.time);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // 1. Read the given values: n, m, k
        if (!scanner.hasNextInt()) return; // Safety check
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int k = scanner.nextInt();

        // 2. Read the emergency centers
        int[] centers = new int[k];
        for (int i = 0; i < k; i++) {
            centers[i] = scanner.nextInt();
        }

        // 3. Create our "Reversed" Graph
        // We use a list of lists. graph.get(i) will give us all roads LEADING OUT OF city i
        List<List<Edge>> reversedGraph = new ArrayList<>();

        // Initialize an empty list for each city (1-indexed, so we go up to n + 1)
        for (int i = 0; i <= n; i++) {
            reversedGraph.add(new ArrayList<>());
        }

        // Read all m roads
        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            long w = scanner.nextLong();

            // REVERSAL MAGIC: Instead of adding a road from u to v,
            // we add a road from v to u!
            reversedGraph.get(v).add(new Edge(u, w));
        }

        // 4. Setup for Multi-Source Dijkstra
        // Array to keep track of the minimum time to each city
        long[] minTime = new long[n + 1];

        // Fill it with a huge number to represent "Infinity" (not reached yet)
        Arrays.fill(minTime, Long.MAX_VALUE);

        // A PriorityQueue acts like a magical waiting line that always serves the shortest time first
        PriorityQueue<Edge> pq = new PriorityQueue<>();

        // 5. Start from ALL emergency centers at the same time
        for (int i = 0; i < k; i++) {
            int centerNode = centers[i];
            minTime[centerNode] = 0; // Time to reach a center from a center is 0
            pq.add(new Edge(centerNode, 0));
        }

        // 6. Run the Pathfinding Search (Dijkstra's Algorithm)
        while (!pq.isEmpty()) {
            // Get the city we can reach in the shortest amount of time
            Edge current = pq.poll();
            int currentCity = current.node;
            long currentTime = current.time;

            // If we already found a faster way to this city earlier, skip it
            if (currentTime > minTime[currentCity]) {
                continue;
            }

            // Look at all neighboring cities in our reversed graph
            for (Edge neighbor : reversedGraph.get(currentCity)) {
                long newTime = currentTime + neighbor.time;

                // If this new path is faster than what we previously recorded, update it!
                if (newTime < minTime[neighbor.node]) {
                    minTime[neighbor.node] = newTime;
                    pq.add(new Edge(neighbor.node, newTime));
                }
            }
        }

        // 7. Print the results for all cities from 1 to n
        for (int i = 1; i <= n; i++) {
            if (minTime[i] == Long.MAX_VALUE) {
                // If it's still infinity, it means we could never reach it
                System.out.println(-1);
            } else {
                System.out.println(minTime[i]);
            }
        }

        scanner.close();
    }
}








import java.util.*;

public class CheapestRouteCoupon {

    /*
     * OBJECTIVE: Find the cheapest route from city 1 to city n, with the option to
     * halve the cost of exactly one flight along the way.
     *
     * GIVEN:
     * - n cities, m unidirectional flights.
     * - Each flight has a start (a), end (b), and a cost (c).
     */

    // This class represents a flight destination and its normal cost
    static class Flight {
        int destination;
        long cost;

        public Flight(int destination, long cost) {
            this.destination = destination;
            this.cost = cost;
        }
    }

    // This class represents our "State" as we travel.
    // It holds where we are, how much we've spent, and if we've used the coupon.
    static class TravelState implements Comparable<TravelState> {
        int city;
        long totalSpent;
        int couponUsed; // 0 means not used (Ticket A), 1 means used (Ticket B)

        public TravelState(int city, long totalSpent, int couponUsed) {
            this.city = city;
            this.totalSpent = totalSpent;
            this.couponUsed = couponUsed;
        }

        // Always process the cheapest accumulated cost first
        @Override
        public int compareTo(TravelState other) {
            return Long.compare(this.totalSpent, other.totalSpent);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) return;

        int n = scanner.nextInt();
        int m = scanner.nextInt();

        // 1. Build the flight network
        List<List<Flight>> network = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            network.add(new ArrayList<>());
        }

        for (int i = 0; i < m; i++) {
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            long c = scanner.nextLong();
            network.get(a).add(new Flight(b, c));
        }

        // 2. The 2D Array to track minimum costs (The "Tickets")
        // minCost[0][city] = cheapest cost to reach 'city' WITHOUT using coupon
        // minCost[1][city] = cheapest cost to reach 'city' HAVING USED the coupon
        long[][] minCost = new long[2][n + 1];

        // Fill everything with "Infinity" to start
        Arrays.fill(minCost[0], Long.MAX_VALUE);
        Arrays.fill(minCost[1], Long.MAX_VALUE);

        // 3. Setup Priority Queue for Dijkstra
        PriorityQueue<TravelState> pq = new PriorityQueue<>();

        // Start at city 1, spent 0 cost, coupon is NOT used (state 0)
        minCost[0][1] = 0;
        pq.add(new TravelState(1, 0, 0));

        // 4. Run State-Space Dijkstra
        while (!pq.isEmpty()) {
            TravelState current = pq.poll();

            // If we already found a cheaper way to reach this city IN THIS SPECIFIC STATE, skip it
            if (current.totalSpent > minCost[current.couponUsed][current.city]) {
                continue;
            }

            // Look at all flights leaving our current city
            for (Flight flight : network.get(current.city)) {

                // Option 1: Take the flight normally (Don't use coupon)
                // We stay in whatever coupon state we are currently in.
                long normalCost = current.totalSpent + flight.cost;
                if (normalCost < minCost[current.couponUsed][flight.destination]) {
                    minCost[current.couponUsed][flight.destination] = normalCost;
                    pq.add(new TravelState(flight.destination, normalCost, current.couponUsed));
                }

                // Option 2: Take the flight AND use the coupon right now
                // We can ONLY do this if we haven't used the coupon yet (current.couponUsed == 0)
                if (current.couponUsed == 0) {
                    long discountedCost = current.totalSpent + (flight.cost / 2); // Halve the cost

                    // After using it, our new state is 1 (coupon used)
                    if (discountedCost < minCost[1][flight.destination]) {
                        minCost[1][flight.destination] = discountedCost;
                        pq.add(new TravelState(flight.destination, discountedCost, 1));
                    }
                }
            }
        }

        // The answer is the absolute cheapest way to reach city 'n'.
        // We compare the cost of reaching it without the coupon vs with the coupon.
        long finalAnswer = Math.min(minCost[0][n], minCost[1][n]);
        System.out.println(finalAnswer);

        scanner.close();
    }
}













import java.util.*;

public class NegativeCycleDetector {

    /*
     * OBJECTIVE: Use Bellman-Ford to find if there is a cycle where the total cost is negative.
     * If there is, trace our steps backward to print the cycle in order.
     *
     * GIVEN:
     * - n nodes, m edges (a to b with cost c).
     */

    // Simple class to hold a directed edge
    static class Edge {
        int u, v;
        long cost;

        public Edge(int u, int v, long cost) {
            this.u = u;
            this.v = v;
            this.cost = cost;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) return;

        int n = scanner.nextInt();
        int m = scanner.nextInt();

        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            edges.add(new Edge(scanner.nextInt(), scanner.nextInt(), scanner.nextLong()));
        }

        // distance array tracks the minimum distance to each node
        long[] distance = new long[n + 1];
        // parent array remembers "who updated my distance last?" so we can trace paths back
        int[] parent = new int[n + 1];

        // Fill distances with 0.
        // We use 0 instead of Infinity because the graph might be disconnected,
        // and we want to find negative cycles anywhere in the graph, not just from node 1.
        Arrays.fill(parent, -1);

        int lastUpdatedNode = -1;

        // 1. Bellman-Ford Algorithm Setup
        // A path can have at most (n - 1) edges. So we relax all edges 'n' times.
        // If we still find a shorter path on the 'nth' time, it means there is an infinite negative loop!
        for (int i = 1; i <= n; i++) {
            lastUpdatedNode = -1;

            // Go through every single edge
            for (Edge edge : edges) {
                // If taking this edge gives a shorter total distance to the destination node 'v'
                if (distance[edge.u] + edge.cost < distance[edge.v]) {
                    distance[edge.v] = distance[edge.u] + edge.cost;
                    parent[edge.v] = edge.u; // Remember where we came from
                    lastUpdatedNode = edge.v; // Keep track of the last node that got updated
                }
            }
        }

        // 2. Check the results
        if (lastUpdatedNode == -1) {
            // Nothing was updated on the nth pass, so there is no negative cycle
            System.out.println("-1");
        } else {
            // We found a negative cycle! Now we need to extract it.

            // Because lastUpdatedNode might just be a node 'downstream' of the cycle,
            // we trace backwards n times to guarantee we are actually inside the cycle itself.
            int cycleStartNode = lastUpdatedNode;
            for (int i = 0; i < n; i++) {
                cycleStartNode = parent[cycleStartNode];
            }

            // Now that we are definitely inside the cycle, trace backwards until we hit ourselves again
            List<Integer> cycle = new ArrayList<>();
            int curr = cycleStartNode;
            while (true) {
                cycle.add(curr);
                curr = parent[curr];
                // Once we loop back to our start node, we've found the whole cycle
                if (curr == cycleStartNode) {
                    cycle.add(curr); // Add the start node one last time to complete the circle
                    break;
                }
            }

            // Because we traced backwards (child to parent), the cycle is currently in reverse order.
            Collections.reverse(cycle);

            // Print the cycle
            for (int i = 0; i < cycle.size(); i++) {
                System.out.print(cycle.get(i) + (i == cycle.size() - 1 ? "" : " "));
            }
            System.out.println();
        }

        scanner.close();
    }
}














import java.util.*;

public class TiredOfFlights {

    /*
     * OBJECTIVE: Find the minimum cost to reach city 'n' from city '1' using at most 'k' flights,
     * with the option to halve the cost of exactly one flight.
     *
     * GIVEN:
     * - n airports, m flights, k maximum flights allowed.
     */

    // Class to represent a flight destination and its normal cost
    static class Flight {
        int destination;
        long cost;

        public Flight(int destination, long cost) {
            this.destination = destination;
            this.cost = cost;
        }
    }

    // Class to represent our current "State" during the journey
    static class TravelState implements Comparable<TravelState> {
        int city;
        long totalSpent;
        int couponUsed; // 0 means not used, 1 means used
        int flightsTaken; // Tracks how many flights we have taken so far

        public TravelState(int city, long totalSpent, int couponUsed, int flightsTaken) {
            this.city = city;
            this.totalSpent = totalSpent;
            this.couponUsed = couponUsed;
            this.flightsTaken = flightsTaken;
        }

        // Priority Queue Rule: Always process the cheapest accumulated cost first!
        @Override
        public int compareTo(TravelState other) {
            return Long.compare(this.totalSpent, other.totalSpent);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        if (!scanner.hasNextInt()) return;

        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int k = scanner.nextInt();

        // 1. Build the flight network
        List<List<Flight>> network = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            network.add(new ArrayList<>());
        }

        for (int i = 0; i < m; i++) {
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            long c = scanner.nextLong();
            network.get(a).add(new Flight(b, c));
        }

        // 2. Array to track the minimum FLIGHTS taken to reach a city in a specific coupon state.
        // minFlights[0][city] = fewest flights to reach 'city' WITHOUT using coupon
        // minFlights[1][city] = fewest flights to reach 'city' HAVING USED the coupon
        int[][] minFlights = new int[2][n + 1];

        // Fill with a large number (Infinity) to start
        Arrays.fill(minFlights[0], Integer.MAX_VALUE);
        Arrays.fill(minFlights[1], Integer.MAX_VALUE);

        // 3. Setup Priority Queue
        PriorityQueue<TravelState> pq = new PriorityQueue<>();

        // Start at city 1, spent 0, coupon not used (0), flights taken so far is 0
        pq.add(new TravelState(1, 0, 0, 0));

        // 4. Run Modified Dijkstra
        while (!pq.isEmpty()) {
            TravelState current = pq.poll();

            // SUCCESS CONDITION:
            // Because the Priority Queue ALWAYS gives us the cheapest cost first,
            // the VERY FIRST time we pop our destination (city n) from the queue,
            // it is mathematically guaranteed to be the cheapest valid path!
            if (current.city == n) {
                System.out.println(current.totalSpent);
                scanner.close();
                return; // We found the answer, stop the program
            }

            // PRUNING (The most important part for the 'k' limit):
            // If we've already found a cheaper way to reach this city that used the
            // SAME or FEWER flights, this current path is completely useless. Skip it!
            if (current.flightsTaken >= minFlights[current.couponUsed][current.city]) {
                continue;
            }

            // Otherwise, record this new lower amount of flights for this city/state
            minFlights[current.couponUsed][current.city] = current.flightsTaken;

            // Only look at next flights if we haven't hit our flight limit yet
            if (current.flightsTaken < k) {
                for (Flight flight : network.get(current.city)) {

                    // Option 1: Take the flight normally (Don't use coupon)
                    pq.add(new TravelState(
                            flight.destination,
                            current.totalSpent + flight.cost,
                            current.couponUsed, // Keep the same coupon state
                            current.flightsTaken + 1 // We took 1 more flight
                    ));

                    // Option 2: Take the flight AND use the coupon (if we still have it)
                    if (current.couponUsed == 0) {
                        pq.add(new TravelState(
                                flight.destination,
                                current.totalSpent + (flight.cost / 2),
                                1, // We are now in the 'used coupon' state
                                current.flightsTaken + 1 // We took 1 more flight
                        ));
                    }
                }
            }
        }

        // If the queue becomes empty and we never hit "if (current.city == n)",
        // it means it's impossible to reach London within k flights.
        System.out.println("Not possible");
        scanner.close();
    }
}



