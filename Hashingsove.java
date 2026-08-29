// Interface defining what every Hash Table must do
interface HashTable {
    void insert(String key, int value);
    void delete(String key);
    int search(String key); 
    int getCollisions();    
    
    // NEW: Method to print the probe sequence
    void printProbeSequence(String key);
}  

// ... inside ChainingHashTable class (add to the bottom) ...

    @Override
    public void printProbeSequence(String key) {
        // Objective: Fulfill the requirement that chaining does not support this feature.
        System.out.println("This method is not supported in the chaining method.");
    }



// ... inside OpenAddressingHashTable class (add to the bottom) ...

    @Override
    public void printProbeSequence(String key) {
        int hash = getPrimaryHash(key);
        int i = 0;
        
        // StringBuilder is great for beginners to cleanly build a string step-by-step
        StringBuilder sequence = new StringBuilder();

        while (i < tableSize) {
            // Use your existing helper to find the exact slot we are checking
            int index = getNextIndex(hash, i, key);
            
            // If this isn't the first number, add an arrow before it
            if (i > 0) {
                sequence.append(" -> ");
            }
            
            // Add the current slot to our sequence log
            sequence.append(index);

            // STOP CONDITION 1: We hit an empty slot. 
            // In open addressing, this means the search is over because if the key 
            // existed, it would have been placed here or earlier.
            if (table[index] == null) {
                break; 
            }
            
            // STOP CONDITION 2: We actually found the key!
            if (table[index] != TOMBSTONE && table[index].key.equals(key)) {
                break; 
            }
            
            // Otherwise, increment 'i' to jump to the next slot in the sequence
            i++;
        }
        
        // Print out the final connected sequence
        System.out.println(sequence.toString());
    }

// ... (Keep all your existing Main method code above this) ...
        System.out.println("-----------------------------------------------------------------------------------------");
        
        // NEW: Demonstration of printProbeSequence as requested by the assignment
        java.util.Scanner scanner = new java.util.Scanner(System.in);
        
        System.out.println("\nEnter the number of keys to test for probe sequence (n):");
        if(scanner.hasNextInt()) {
            int n = scanner.nextInt();
            scanner.nextLine(); // Consume the leftover newline character
            
            System.out.println("Enter the " + n + " keys:");
            String[] keysToTest = new String[n];
            for (int i = 0; i < n; i++) {
                keysToTest[i] = scanner.nextLine();
            }
            
            System.out.println("\nProbe Sequences (Using Double Hashing with Hash 1 for demonstration):");
            for (String k : keysToTest) {
                // We use double1 because probe sequences are for open addressing
                double1.printProbeSequence(k);
            }
        }
        scanner.close();
    }
}


















import java.util.Scanner;
import java.util.LinkedList;
import java.util.ArrayList;

/*
 * OBJECTIVE: Compute Intersection, Union, and Difference (A-B) of two integer sets.
 * GIVEN: Two arrays of integers representing Set A and Set B.
 * METHOD: Use a custom Chaining Hash Table to achieve O(1) average lookup and linear overall time.
 */

// ---------------------------------------------------------
// 1. CUSTOM HASH TABLE (Simplified Chaining Method)
// ---------------------------------------------------------
class SimpleIntHashTable {
    // We use an array of Linked Lists to handle collisions (Chaining)
    private LinkedList<Integer>[] table;
    private int size;

    // Constructor to initialize the table with a specific capacity
    @SuppressWarnings("unchecked")
    public SimpleIntHashTable(int capacity) {
        this.size = capacity;
        table = new LinkedList[size];
        for (int i = 0; i < size; i++) {
            table[i] = new LinkedList<>();
        }
    }

    // A very simple hash function for integers using modulo
    private int getHash(int key) {
        return Math.abs(key) % size;
    }

    // Insert an integer into the table (ignores duplicates to maintain set properties)
    public void insert(int key) {
        int index = getHash(key);
        // Only add if it doesn't already exist (Set rule: no duplicates)
        if (!table[index].contains(key)) {
            table[index].add(key);
        }
    }

    // Search for an integer to see if it exists in the table (O(1) average time)
    public boolean contains(int key) {
        int index = getHash(key);
        return table[index].contains(key);
    }
}

// ---------------------------------------------------------
// 2. MAIN PROGRAM (Set Operations)
// ---------------------------------------------------------
public class SetOperations {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- 1. READ INPUT FOR SET A ---
        int sizeA = scanner.nextInt();
        int[] setA = new int[sizeA];
        for (int i = 0; i < sizeA; i++) {
            setA[i] = scanner.nextInt();
        }

        // --- 2. READ INPUT FOR SET B ---
        int sizeB = scanner.nextInt();
        int[] setB = new int[sizeB];
        for (int i = 0; i < sizeB; i++) {
            setB[i] = scanner.nextInt();
        }
        
        scanner.close();

        // We choose a table size roughly twice the number of inputs to minimize collisions
        int tableCapacity = (sizeA + sizeB) * 2; 

        // Create hash tables for Set A and Set B
        SimpleIntHashTable hashA = new SimpleIntHashTable(tableCapacity);
        SimpleIntHashTable hashB = new SimpleIntHashTable(tableCapacity);

        // Populate the hash tables with our input arrays
        for (int num : setA) {
            hashA.insert(num);
        }
        for (int num : setB) {
            hashB.insert(num);
        }

        // --- 3. PERFORM SET OPERATIONS ---
        
        // Lists to store the final results before printing
        ArrayList<Integer> intersection = new ArrayList<>();
        ArrayList<Integer> union = new ArrayList<>();
        ArrayList<Integer> difference = new ArrayList<>();

        // 3a. INTERSECTION (A ∩ B) and DIFFERENCE (A - B)
        // We loop through A. If B has the number, it's an intersection. If not, it's a difference.
        for (int num : setA) {
            if (hashB.contains(num)) {
                intersection.add(num);
            } else {
                difference.add(num);
            }
        }

        // 3b. UNION (A U B)
        // Union is all unique numbers from both sets. 
        // We can use a temporary Hash Table to filter out duplicates easily.
        SimpleIntHashTable unionHash = new SimpleIntHashTable(tableCapacity);
        for (int num : setA) {
            // Add to hash table to track that we've seen it, then add to result list
            if (!unionHash.contains(num)) {
                unionHash.insert(num);
                union.add(num);
            }
        }
        for (int num : setB) {
            // Only add to result list if A didn't already put it in the hash table
            if (!unionHash.contains(num)) {
                unionHash.insert(num);
                union.add(num);
            }
        }

        // --- 4. PRINT OUTPUT ---
        
        System.out.print("Intersection: ");
        for (int num : intersection) {
            System.out.print(num + " ");
        }
        System.out.println();

        System.out.print("Union: ");
        for (int num : union) {
            System.out.print(num + " ");
        }
        System.out.println();

        System.out.print("Diff (A-B): ");
        for (int num : difference) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}














import java.util.LinkedList;
import java.util.ArrayList;

/*
 * OBJECTIVE: Implement a Hash Table with Priority-Weighted Chaining.
 * GIVEN: 12 specific words, a custom positional Hash function (mod 13), 
 *        and a custom Weight function (based on first and last characters).
 * RULES: Chains must stay sorted by weight (highest at the head). 
 *        Ties go to the earlier inserted word. Duplicates are ignored.
 */

// ---------------------------------------------------------
// 1. DATA STRUCTURE SETUP
// ---------------------------------------------------------

// A simple Node to hold the word, its original sequence, and its weight
class WeightedNode {
    String word;
    int sequence;
    double weight;

    public WeightedNode(String word, int sequence, double weight) {
        this.word = word;
        this.sequence = sequence;
        this.weight = weight;
    }
}

class PriorityHashTable {
    // Fixed size of 13 as required
    private static final int N = 13;
    
    // Array of built-in Java Linked Lists
    private LinkedList<WeightedNode>[] table;

    @SuppressWarnings("unchecked")
    public PriorityHashTable() {
        table = new LinkedList[N];
        for (int i = 0; i < N; i++) {
            table[i] = new LinkedList<>();
        }
    }

    // ---------------------------------------------------------
    // 2. MATH & LOGIC FUNCTIONS
    // ---------------------------------------------------------

    // Hash1(k) = (Sum of (i * ASCII(k_i))) mod 13
    public int getHash(String word) {
        int sum = 0;
        // The problem specifies 1-indexed positions for the math
        for (int i = 0; i < word.length(); i++) {
            int position = i + 1; 
            char character = word.charAt(i);
            sum += (position * (int) character);
        }
        return sum % N;
    }

    // weight(k) = ((ASCII(first) + ASCII(last)) mod 100) / 100.0
    public double getWeight(String word) {
        char firstChar = word.charAt(0);
        char lastChar = word.charAt(word.length() - 1);
        
        int asciiSum = (int) firstChar + (int) lastChar;
        int modValue = asciiSum % 100;
        
        // Divide by 100.0 (decimal) to ensure we get a fraction like 0.15
        return modValue / 100.0;
    }

    // ---------------------------------------------------------
    // 3. SORTED INSERTION LOGIC
    // ---------------------------------------------------------
    
    public void insert(String word, int sequence) {
        int hash = getHash(word);
        double weight = getWeight(word);
        WeightedNode newNode = new WeightedNode(word, sequence, weight);

        LinkedList<WeightedNode> chain = table[hash];

        // Step 1: Check for duplicates. If it exists, discard and stop.
        for (WeightedNode node : chain) {
            if (node.word.equals(word)) {
                return; 
            }
        }

        // Step 2: Find the correct sorted position (Descending Order)
        int insertIndex = 0;
        
        // Loop through the chain. We keep moving down the list as long as the 
        // existing node's weight is GREATER THAN OR EQUAL TO our new node's weight.
        // The "EQUAL TO" handles the tie-breaker: it forces the new node to skip past 
        // the older node, naturally placing the new node behind it.
        while (insertIndex < chain.size() && chain.get(insertIndex).weight >= weight) {
            insertIndex++;
        }

        // Step 3: Insert directly at the found index
        chain.add(insertIndex, newNode);
    }

    // ---------------------------------------------------------
    // 4. PRINTING HELPERS
    // ---------------------------------------------------------
    
    public void printNonEmptyBuckets() {
        System.out.println("\nBucket | Chain (head -> tail)");
        for (int i = 0; i < N; i++) {
            if (!table[i].isEmpty()) {
                System.out.print(String.format("%-6d | ", i));
                
                // Print each node in the chain formatted to 2 decimal places
                for (int j = 0; j < table[i].size(); j++) {
                    WeightedNode node = table[i].get(j);
                    System.out.print(node.word + " (" + String.format("%.2f", node.weight) + ")");
                    
                    // Add an arrow if it's not the last item in the list
                    if (j < table[i].size() - 1) {
                        System.out.print(" -> ");
                    }
                }
                System.out.println();
            }
        }
    }

    public void printEmptyBuckets() {
        ArrayList<Integer> emptyIndices = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            if (table[i].isEmpty()) {
                emptyIndices.add(i);
            }
        }
        
        // Print the array of empty indices separated by commas
        for (int i = 0; i < emptyIndices.size(); i++) {
            System.out.print(emptyIndices.get(i));
            if (i < emptyIndices.size() - 1) {
                System.out.print(",");
            }
        }
        System.out.println(" | (empty)");
    }
}

// ---------------------------------------------------------
// 5. MAIN EXECUTION
// ---------------------------------------------------------

public class Main {
    public static void main(String[] args) {
        PriorityHashTable ht = new PriorityHashTable();
        
        // The exact sequence of words provided in the instructions
        String[] inputWords = {
            "cat", "dog", "bat", "rat", "sun", "fun", 
            "run", "top", "pot", "opt", "art", "tar"
        };

        // 1. Print the Hash and Weight Table
        System.out.println("Word\t| Hash1\t| weight\t| Word\t| Hash1\t| weight");
        System.out.println("-----------------------------------------------------------------");
        
        // Print in two columns to match the sample output format
        for (int i = 0; i < 6; i++) {
            String word1 = inputWords[i];
            String word2 = inputWords[i + 6];
            
            System.out.println(
                word1 + "\t| " + ht.getHash(word1) + "\t| " + String.format("%.2f", ht.getWeight(word1)) + "\t\t| " +
                word2 + "\t| " + ht.getHash(word2) + "\t| " + String.format("%.2f", ht.getWeight(word2))
            );
        }

        // 2. Insert all words into the Hash Table
        for (int i = 0; i < inputWords.length; i++) {
            // Sequence numbers are 1-indexed (1 through 12)
            ht.insert(inputWords[i], i + 1);
        }

        // 3. Print the Chain Contents
        ht.printNonEmptyBuckets();
        
        System.out.println();
        ht.printEmptyBuckets();
    }
}
