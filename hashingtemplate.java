/*
 * PROBLEM STATEMENT & GIVEN:
 * The user needs a comprehensive, intuitive guide to the most essential Java library 
 * functions and data structures used for solving algorithm and data structure problems.
 * 
 * OBJECTIVE:
 * Create a single, runnable Java file that demonstrates Strings, Math, ArrayLists, 
 * Linked Lists, HashSets, HashMaps, and Scanner. All explanations must be commented out 
 * so the file remains valid Java code. The logic must be extremely beginner-friendly.
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Scanner;

public class JavaLibraryCheatSheet {

    public static void main(String[] args) {
        
        // =====================================================================
        // 1. STRING MANIPULATION
        // Strings are immutable (cannot be changed once created).
        // =====================================================================
        
        String myWord = "programming";
        
        // .length() - Returns the number of characters
        int len = myWord.length(); 
        
        // .charAt(index) - Gets the character at a specific 0-based index
        char firstLetter = myWord.charAt(0); // 'p'
        
        // .equals(anotherString) - Compares the actual text, NEVER use == for strings
        boolean isSame = myWord.equals("java"); // false
        
        /*
         * StringBuilder is used when you need to build or change a string step-by-step
         * without wasting memory creating a million temporary strings.
         */
        StringBuilder sb = new StringBuilder();
        sb.append("Node1");
        sb.append(" -> ");
        sb.append("Node2");
        String path = sb.toString(); // "Node1 -> Node2"


        // =====================================================================
        // 2. MATH UTILITIES
        // The Math class is built-in; you never need to import it.
        // =====================================================================
        
        // Math.abs() - Returns the positive (absolute) version of a number
        // Very important for hashing to avoid negative array indexes!
        int positiveHash = Math.abs(-42); // 42
        
        // Math.max() and Math.min() - Finds the biggest or smallest of two numbers
        int biggest = Math.max(10, 100); // 100


        // =====================================================================
        // 3. DYNAMIC ARRAYS (ArrayList)
        // An array that grows automatically. You don't declare its size upfront.
        // =====================================================================
        
        ArrayList<String> dynamicList = new ArrayList<>();
        
        // .add(element) - Puts an item at the very end of the list
        dynamicList.add("Apple");
        dynamicList.add("Banana");
        
        // .size() - Tells you how many items are currently in the list
        int totalItems = dynamicList.size(); // 2
        
        // .get(index) - Retrieves the item at that position
        String fruit = dynamicList.get(0); // "Apple"
        
        // .contains(element) - Returns true if the item is in the list
        boolean hasApple = dynamicList.contains("Apple"); // true


        // =====================================================================
        // 4. LINKED CHAINS (LinkedList)
        // Operates like an ArrayList but uses connected Nodes under the hood. 
        // Best for adding/removing items in the middle or beginning.
        // =====================================================================
        
        LinkedList<Integer> chain = new LinkedList<>();
        
        // .add(element) - Puts item at the end
        chain.add(10);
        chain.add(30);
        
        // .add(index, element) - Forces an item into a specific spot, shifting the rest down
        chain.add(1, 20); // List is now: 10 -> 20 -> 30
        
        // .isEmpty() - A clean way to check if the list has zero items
        boolean isEmpty = chain.isEmpty(); // false


        // =====================================================================
        // 5. UNIQUE COLLECTIONS (HashSet)
        // A bag of items where duplicates are instantly ignored. Uses hashing
        // to make searching (.contains) lightning fast (O(1) time).
        // =====================================================================
        
        HashSet<Integer> uniqueNumbers = new HashSet<>();
        
        uniqueNumbers.add(5);
        uniqueNumbers.add(5); // This does nothing; 5 is already there
        
        // Instantly checks if a number exists without looping through everything
        boolean hasFive = uniqueNumbers.contains(5); // true


        // =====================================================================
        // 6. KEY-VALUE PAIRS (HashMap)
        // Stores two pieces of data together. You use the Key to look up the Value.
        // =====================================================================
        
        HashMap<String, Integer> wordWeights = new HashMap<>();
        
        // .put(key, value) - Saves the relationship
        wordWeights.put("cat", 15);
        wordWeights.put("dog", 3);
        
        // .get(key) - Retrieves the Value attached to that Key
        int catWeight = wordWeights.get("cat"); // 15
        
        // .containsKey(key) - Checks if the Key exists in the map
        boolean hasBird = wordWeights.containsKey("bird"); // false


        // =====================================================================
        // 7. READING CONSOLE INPUT (Scanner)
        // Used for reading what the user types in the terminal.
        // =====================================================================
        
        /* 
         * Note: Commented out to prevent the program from hanging while waiting 
         * for input if you just want to compile and run this file as a test.
         * 
         * Scanner scan = new Scanner(System.in);
         * 
         * System.out.println("Enter a number:");
         * int count = scan.nextInt(); // Reads the integer
         * 
         * // CRUCIAL STEP: Consumes the invisible "Enter" key press left behind by nextInt()
         * scan.nextLine(); 
         * 
         * System.out.println("Enter a word:");
         * String word = scan.nextLine(); // Reads the whole line of text
         * 
         * scan.close(); // Always close it to free up memory
         */
    }
}
