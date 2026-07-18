import java.util.Scanner;

public class RailwayInspection {
    
    // We use a very large number to represent "Infinity" (no path exists yet).
    // We don't use Long.MAX_VALUE directly to prevent overflow when adding two infinities together.
    static final long INF = 1000000000000000L; 

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // 1. Read N (Stations) and M (Tracks)
        if (!scanner.hasNextInt()) return; // Safety check for empty input
        int N = scanner.nextInt();
        int M = scanner.nextInt();
        
        // 2. Setup our distance matrix for All-Pairs Shortest Path
        long[][] dist = new long[N][N];
        
        // Fill the grid initially with Infinity
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == j) {
                    dist[i][j] = 0; // Distance to itself is always 0
                } else {
                    dist[i][j] = INF;
                }
            }
        }
        
        // 3. Read the M tracks and populate the distance matrix
        for (int i = 0; i < M; i++) {
            int u = scanner.nextInt(); // Source station
            int v = scanner.nextInt(); // Destination station
            long t = scanner.nextLong(); // Travel time
            
            // If there are multiple tracks between the same stations, keep the shortest one
            if (t < dist[u][v]) {
                dist[u][v] = t;
            }
        }
        
        // 4. Read the inspection hubs
        int hubA = scanner.nextInt();
        int hubB = scanner.nextInt();
        
        // ---------------------------------------------------------
        // 5. THE FLOYD-WARSHALL ALGORITHM (All-Pairs Shortest Path)
        // ---------------------------------------------------------
        // This checks if going through an intermediate station 'k' 
        // makes the path from 'i' to 'j' shorter.
        for (int k = 0; k < N; k++) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    // Only attempt to add if a path actually exists to avoid adding to Infinity
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        long newDistance = dist[i][k] + dist[k][j];
                        if (newDistance < dist[i][j]) {
                            dist[i][j] = newDistance;
                        }
                    }
                }
            }
        }
        
        // 6. Process the Queries
        int Q = scanner.nextInt();
        for (int i = 0; i < Q; i++) {
            int s = scanner.nextInt(); // start of journey
            int d = scanner.nextInt(); // destination of journey
            
            // Calculate total cost going through Hub A
            // We need a path from Start->HubA AND a path from HubA->Destination
            long costViaA = INF;
            if (dist[s][hubA] != INF && dist[hubA][d] != INF) {
                costViaA = dist[s][hubA] + dist[hubA][d];
            }
            
            // Calculate total cost going through Hub B
            // We need a path from Start->HubB AND a path from HubB->Destination
            long costViaB = INF;
            if (dist[s][hubB] != INF && dist[hubB][d] != INF) {
                costViaB = dist[s][hubB] + dist[hubB][d];
            }
            
            // 7. Compare the results and format the output
            if (costViaA == INF && costViaB == INF) {
                // Neither hub can be reached successfully
                System.out.println("-1");
            } else if (costViaA < costViaB) {
                // Hub A is strictly faster
                System.out.println(costViaA + " " + hubA);
            } else if (costViaB < costViaA) {
                // Hub B is strictly faster
                System.out.println(costViaB + " " + hubB);
            } else {
                // They are exactly tied! Pick the hub with the smaller ID number
                int smallerHub = Math.min(hubA, hubB);
                System.out.println(costViaA + " " + smallerHub);
            }
        }
        
        scanner.close();
    }
}











import java.util.Scanner;

// Save this file exactly as: 2405143_Problem1.java
public class Problem1 {
    
    /*
     * OBJECTIVE: Find the shortest route length between two given cities for a large number of queries.
     * 
     * GIVEN: 
     * - n (cities), m (roads), q (queries).
     * - A list of two-way roads between cities with a specific length.
     * - A list of queries asking for the shortest distance between two cities.
     */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Safety check to ensure there is input to read
        if (!scanner.hasNextInt()) return; 

        // 1. Read initial givens
        int n = scanner.nextInt(); // Number of cities
        int m = scanner.nextInt(); // Number of roads
        int q = scanner.nextInt(); // Number of queries
        
        // We use an array size of n + 1 because the cities are numbered 1 to n (not 0 to n-1)
        long[][] dist = new long[n + 1][n + 1];
        
        // A very large number to represent "no path exists". 
        // We avoid Long.MAX_VALUE to prevent overflow if we add two INF values together.
        long INF = 1000000000000000L; 

        // 2. Initialize the distance grid
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (i == j) {
                    dist[i][j] = 0; // Distance from a city to itself is 0
                } else {
                    dist[i][j] = INF; // Otherwise, initially assume we cannot reach it
                }
            }
        }
        
        // 3. Read the roads and fill the initial grid
        for (int i = 0; i < m; i++) {
            int u = scanner.nextInt(); // City A
            int v = scanner.nextInt(); // City B
            long c = scanner.nextLong(); // Length of the road
            
            // Because there can be multiple roads between the same cities, 
            // we only want to keep the shortest one.
            if (c < dist[u][v]) {
                dist[u][v] = c;
                dist[v][u] = c; // Important: The roads are two-way, so we update both directions!
            }
        }
        
        // 4. FLOYD-WARSHALL ALGORITHM (Finding the shortest path between ALL pairs)
        // k is our "intermediate" or "stepping stone" city
        for (int k = 1; k <= n; k++) {
            // i is our starting city
            for (int i = 1; i <= n; i++) {
                // j is our destination city
                for (int j = 1; j <= n; j++) {
                    
                    // First, ensure that a path actually exists through city k
                    if (dist[i][k] != INF && dist[k][j] != INF) {
                        
                        // If traveling i -> k -> j is shorter than the current known path i -> j, update it!
                        if (dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
        }
        
        // 5. Answer all the queries instantly using our completed grid
        for (int i = 0; i < q; i++) {
            int u = scanner.nextInt();
            int v = scanner.nextInt();
            
            if (dist[u][v] == INF) {
                System.out.println("-1"); // No path exists
            } else {
                System.out.println(dist[u][v]); // Print the shortest distance
            }
        }
        
        scanner.close();
    }
}








import java.util.Scanner;
import java.util.HashMap;

// Save this file exactly as: 2405143_Problem2.java
public class Problem2 {
    
    /*
     * OBJECTIVE: Determine if currency arbitrage is possible (ending up with > 1.0 of the starting currency).
     * 
     * GIVEN: 
     * - n (number of currencies).
     * - m (number of exchange rates).
     * - Specific one-way exchange rates between currencies.
     */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        if (!scanner.hasNextInt()) return;
        
        // 1. Read givens
        int n = scanner.nextInt();
        
        // We use a HashMap to easily map a currency name (like "USD") to an array index (like 0, 1, 2...)
        HashMap<String, Integer> currencyMap = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            String currencyName = scanner.next();
            currencyMap.put(currencyName, i);
        }
        
        int m = scanner.nextInt();
        
        // This time our grid holds exchange rates (decimals), so we use double
        double[][] rates = new double[n][n];
        
        // 2. Initialize the exchange rate grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    rates[i][j] = 1.0; // 1 unit of currency exchanges for 1 unit of itself
                } else {
                    rates[i][j] = 0.0; // Assume 0.0 rate if no exchange exists yet
                }
            }
        }
        
        // 3. Read the given exchange rates
        for (int i = 0; i < m; i++) {
            String sourceCurrency = scanner.next();
            double rate = scanner.nextDouble();
            String destCurrency = scanner.next();
            
            // Look up the corresponding array indices for these currencies
            int u = currencyMap.get(sourceCurrency);
            int v = currencyMap.get(destCurrency);
            
            // Keep the best possible rate
            if (rate > rates[u][v]) {
                rates[u][v] = rate;
            }
        }
        
        // 4. MODIFIED FLOYD-WARSHALL ALGORITHM (Finding the Maximum Multiplied Path)
        // k is our "stepping stone" currency
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    
                    // Instead of ADDING distances, we MULTIPLY exchange rates.
                    // Instead of looking for the SHORTEST path, we want the MAXIMUM profit.
                    double newRate = rates[i][k] * rates[k][j];
                    
                    if (newRate > rates[i][j]) {
                        rates[i][j] = newRate;
                    }
                }
            }
        }
        
        // 5. Check for Arbitrage
        boolean arbitragePossible = false;
        
        // If the exchange rate from any currency back to ITSELF is greater than 1.0, 
        // it means we made a profit!
        for (int i = 0; i < n; i++) {
            if (rates[i][i] > 1.0) {
                arbitragePossible = true;
                break; // We found arbitrage, no need to keep checking
            }
        }
        
        // 6. Print the result
        if (arbitragePossible) {
            System.out.println("Yes");
        } else {
            System.out.println("No");
        }
        
        scanner.close();
    }
}








import java.util.Scanner;
import java.util.HashMap;

public class ArbitrageAllCurrencies {
    
    /*
     * OBJECTIVE: Determine ALL specific currencies that can achieve arbitrage (profit).
     * 
     * GIVEN: 
     * - n: Number of different currencies.
     * - List of currency names.
     * - m: Number of available exchange rates.
     * - A list of one-way exchange rates between specific currencies.
     */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        if (!scanner.hasNextInt()) return; 
        
        int n = scanner.nextInt();
        
        // We use an Array to remember the original order for printing at the end
        String[] currencyNames = new String[n];
        
        // We use a HashMap for instant lookups: Currency Name -> Array Index
        HashMap<String, Integer> currencyMap = new HashMap<>();
        
        for (int i = 0; i < n; i++) {
            currencyNames[i] = scanner.next();
            currencyMap.put(currencyNames[i], i); // Store the index alongside the name
        }
        
        int m = scanner.nextInt();
        double[][] rates = new double[n][n];
        
        // Initialize the grid
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    rates[i][j] = 1.0; 
                } else {
                    rates[i][j] = 0.0; 
                }
            }
        }
        
        // Read the exchange rates using our HashMap for clean, fast lookups
        for (int i = 0; i < m; i++) {
            String source = scanner.next();
            double rate = scanner.nextDouble();
            String dest = scanner.next();
            
            // Look up the indices directly from the HashMap! No helper function needed.
            int u = currencyMap.get(source);
            int v = currencyMap.get(dest);
            
            if (rate > rates[u][v]) {
                rates[u][v] = rate;
            }
        }
        
        // FLOYD-WARSHALL ALGORITHM 
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    double newRate = rates[i][k] * rates[k][j];
                    if (newRate > rates[i][j]) {
                        rates[i][j] = newRate;
                    }
                }
            }
        }
        
        // Check for Arbitrage
        boolean foundAnyArbitrage = false;
        
        for (int i = 0; i < n; i++) {
            boolean canArbitrage = false;
            
            for (int k = 0; k < n; k++) {
                boolean canReachK = rates[i][k] > 0.0;
                boolean canReturnToI = rates[k][i] > 0.0;
                boolean kHasProfitLoop = rates[k][k] > 1.0;
                
                if (canReachK && canReturnToI && kHasProfitLoop) {
                    canArbitrage = true;
                    break; 
                }
            }
            
            // We use the Array here to guarantee we print in the original order
            if (canArbitrage) {
                System.out.println(currencyNames[i]);
                foundAnyArbitrage = true;
            }
        }
        
        if (!foundAnyArbitrage) {
            System.out.println("No Arbitrage");
        }
        
        scanner.close();
    }
}
