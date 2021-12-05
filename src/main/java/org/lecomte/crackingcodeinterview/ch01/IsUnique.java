package org.lecomte.crackingcodeinterview.ch01;

import java.util.HashMap;

/**
 * Determine if a string has all unique characters
 */
public class IsUnique {

    /**
     * Use hashmap O(n)
     */
    public static boolean isUniqueHashmap(String s) {
        var alreadyFound = new HashMap<Character, Boolean>();

        for (var i = 0; i < s.length(); i++) {
            var currentChar = s.charAt(i);
            if (alreadyFound.containsKey(currentChar)) {
                return false;
            } else {
                alreadyFound.put(currentChar, true);
            }
        }

        return true;
    }

    /**
     * Cannot use additional data structures
     * Brute force solution O(n²)
     */
    public static boolean isUniqueBruteForce(String s) {
        for (var i = 0; i < s.length(); i++) {
            var currentChar = s.charAt(i);

            for (var j = 0; j < s.length(); j++) {
                if (j != i && currentChar == s.charAt(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    // TODO when O(n log n) sort implemented, use it to sort the string
    // and have a better solution thant brute force without using additionnal data structure
}
