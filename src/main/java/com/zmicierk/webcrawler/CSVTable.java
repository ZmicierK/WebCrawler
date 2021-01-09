package com.zmicierk.webcrawler;

/**
 * The class is designed to work with a csv string (summarize occurrences, perform sorting)
 * An instance of this class is a string, consisting of url address and the number of occurrences of terms on the page,
 * located at this address. This class also stores the total number of occurrences of all terms
 * for a given page, and also implements the compareTo method, which allows you to sort a collection of objects
 * of this class.
 */
public class CSVTable implements Comparable<CSVTable> {
    /** URL address of the current line */
    private final String urlInCSV;
    /** Comma-separated string consisting of the number of occurrences of terms on this page */
    private final String matchesStr;
    /** Total sum of the occurrences of the current line */
    private final int totalMatches;

    /**
     * Parses the input string into url and the number of occurrences, and calculates the sum of the occurrences.
     * @param CSVLine Comma-separated string consisting of URLs and the number of occurrences of terms.
     */
    public CSVTable(String CSVLine) {
        String[] words = CSVLine.split(",");
        this.urlInCSV = words[0] ;
        this.matchesStr = CSVLine.substring(CSVLine.indexOf(","));
        int sum = 0;
        for (int i = 1; i < words.length; i++) {
            sum += Integer.parseInt(words[i]);
        }
        this.totalMatches = sum;
    }

    /**
     * Returns a comma separated string consisting of url and number of occurrences.
     * @return Comma separated string consisting of url and number of occurrences.
     */
    public String getLine() {
        return urlInCSV + matchesStr;
    }

    /**
     * Return the sum of the occurrences of terms on this page.
     * @return The sum of the occurrences of terms on this page.
     */
    public int getTotalMatches() {
        return totalMatches;
    }

    /**
     * Compares this object with the specified object for order.
     * @param totalMatchesComp the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object is less than, equal to,
     * or greater than the specified object.
     */
    @Override
    public int compareTo(CSVTable totalMatchesComp) {
        int compareMatches = totalMatchesComp.getTotalMatches();
        return compareMatches - this.totalMatches;
    }
}