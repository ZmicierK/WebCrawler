package com.zmicierk.webcrawler;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class is the core of the system. It implements all the crawling logic.
 * The methods of this class perform the following tasks:
 * Crawling web pages in accordance with a given depth and a limit on the number of visited pages,
 * generating statistics for each of them, which is the number of occurrences of specified terms for each page
 * that is processed during the craving process. Outputting statistics to a file, generating a file
 * containing top pages with the maximum total number of occurrences of the specified terms and sorting by
 * this parameter, also outputting these statistics to the standard output stream.
 */
public class WebCrawlerThread {
    /** List of visited pages */
    public ArrayList<String> urlArray = new ArrayList<>();
    /** List of pages to visit in next parse cycle */
    public ArrayList<String> urlQuaFormed = new ArrayList<>();
    /** List of pages to visit in current parse cycle */
    public ArrayList<String> urlQuaCur = new ArrayList<>();
    /** The number of pages visited at the moment. */
    public int visited;
    /** The maximum number of pages to visit. */
    public int maxVisited;
    /** The maximum number of transitions in depth relative to the initial page. */
    public int maxDepth;
    /** A class object that simulates a web browser. */
    public final WebClient webClient = new WebClient(BrowserVersion.CHROME);
    /**
     * The delay which determines the background tasks to wait for (in milliseconds)
     * Used in waitForBackgroundJavaScriptStartingBefore
     * This method blocks until all background JavaScript tasks scheduled to start executing
     * before (now + timeOutJSBefore) have finished executing.
     */
    private final int timeOutJSBefore;
    /**
     * The maximum amount of time to wait (in milliseconds)
     * Used in waitForBackgroundJavaScript
     * This method blocks until all background JavaScript tasks have finished executing.
     */
    private final int timeOutJS;
    /** The number of records in the file specified in the topFileName parameter. */
    private final int numToPrintInTopFile;
    /**
     * The path to the CSV file in which top by total hits statistical data will be serialized (sorted by total hits).
     * The number of entries in this file is specified in the numToPrintInTopFile parameter.
     */
    private final String topFileName;
    /** The path to the CSV file in which all statistical data will be serialized (without predefined sorting) */
    private final String rawFileName;
    /** URL from which the process starts its work.(seed) */
    private final String startUrl;
    /** A term string to define the number of occurrences per page. Terms are separated by commas. */
    private final String termString;
    /** An array of term strings to determine the number of occurrences per page. */
    private final String[] arOfTerms;
    /**
     * Determines whether to display the table heading in output files.
     * true - display the table header
     * false - do not display the table header
     */
    private final boolean printHeader;
    /**
     * Defines the search method:
     * 0x80 (Pattern.CANON_EQ) - Case sensitive
     * 0x02 (Pattern.CASE_INSENSITIVE) - Case insensitive
     */
    private final int caseSensitive;
    /**
     * A class object which Prints formatted representations of objects to a text-output stream.
     * Used to output lines to the file specified in the fileName parameter.
     */
    private PrintWriter writerRaw;
    /**
     * Defines the exit status:
     *  0 Successful completion (no pages to process)
     *  1 Successful completion (maximum depth(-d argument) reached)
     *  2 Successful completion (page visit limit(-v argument) reached)
     * -1 Unsuccessful completion
     */
    private int exitStatus = -1;

    /**
     * It is used to initialize class fields in accordance with the specified arguments during the initialization
     * process, the logical correctness of the passed arguments is checked, the start page is available and
     * the files can be created.
     * @param maxVisited The maximum number of pages to visit.
     * @param maxDepth The maximum number of transitions in depth relative to the initial page.
     * @param timeOutJSBefore
     * The delay which determines the background tasks to wait for (in milliseconds)
     * Used in waitForBackgroundJavaScriptStartingBefore
     * This method blocks until all background JavaScript tasks scheduled to start executing
     * before (now + timeOutJSBefore) have finished executing.
     * @param timeOutJS
     * The maximum amount of time to wait (in milliseconds)
     * Used in waitForBackgroundJavaScript
     * This method blocks until all background JavaScript tasks have finished executing.
     * @param numToPrintInTopFile The number of records in the file specified in the topFileName parameter.
     * @param timeOut Time to wait for a response from the server.
     * @param topFileName
     * The path to the CSV file in which top by total hits statistical data will be serialized (sorted by total hits).
     * The number of entries in this file is specified in the numToPrintInTopFile parameter.
     * @param rawFileName The path to the CSV file in which all statistical data will be serialized (without predefined sorting)
     * @param startUrl URL from which the process starts its work.(seed)
     * @param termString
     * A term string to define the number of occurrences per page.
     * Terms are separated by commas.
     * @param arOfTerms An array of term strings to determine the number of occurrences per page.
     * @param printHeader
     * Determines whether to display the table heading in output files.
     * true - Display the table header
     * false - Do not display the table header
     * @param enableJS Enables/disables JavaScript support.
     * @param caseSensitive If true, the search will be case sensitive.
     * @throws Exception if the arguments passed are invalid.
     */
    public WebCrawlerThread(int maxVisited, int maxDepth, int timeOutJSBefore, int timeOutJS, int numToPrintInTopFile,
                            int timeOut, String topFileName, String rawFileName, String startUrl, String termString,
                            String[] arOfTerms, boolean printHeader, boolean enableJS, boolean caseSensitive) throws Exception {
        if (maxDepth < 0) throw new Exception("maxDepth must be positive");
        if (maxVisited < 1) throw new Exception("maxVisited must be bigger than 0");
        if (maxVisited < numToPrintInTopFile) throw new Exception("maxVisited can't be lower than NumToPrintInTopFile");
        if (timeOutJS < 0) throw new Exception("timeOutJS must be positive");
        if (timeOutJSBefore<0) throw new Exception("timeOutJSBefore must be positive");
        if (timeOut < 0) throw new Exception("timeOut must be positive");
        checkFilePerm(rawFileName);
        checkFilePerm(topFileName);
        webClient.getOptions().setJavaScriptEnabled(enableJS);
        webClient.getOptions().setTimeout(timeOut);
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setDownloadImages(false);
        webClient.getOptions().setGeolocationEnabled(false);
        try {
            webClient.getPage(startUrl);
        }
        catch (Exception e) {
            throw new Exception("Illegal startUrl argument");
        }
        if (caseSensitive) {
            this.caseSensitive = Pattern.CANON_EQ;
        } else {
            this.caseSensitive = Pattern.CASE_INSENSITIVE;
        }
        this.maxVisited = maxVisited;
        this.maxDepth = maxDepth;
        this.timeOutJSBefore = timeOutJSBefore;
        this.timeOutJS = timeOutJS;
        this.numToPrintInTopFile = numToPrintInTopFile;
        this.topFileName = topFileName;
        this.rawFileName = rawFileName;
        this.startUrl = startUrl;
        this.termString = termString;
        this.arOfTerms = arOfTerms;
        this.printHeader = printHeader;
    }

    /**
     * Start WebCrawler process according to set class fields.
     * @return exitStatus
     *  0 Successful completion (no pages to process)
     *  1 Successful completion (maximum depth(-d argument) reached)
     *  2 Successful completion (page visit limit(-v argument) reached)
     * -1 Unsuccessful completion
     * @throws Exception if something went wrong
     */
    public int start() throws Exception {
        prepareCSV(rawFileName);
        openRawCSV();
        urlArray.add(startUrl);
        countMatch(startUrl);
        parse();
        writerRaw.close();
        getTopCSV();
        return exitStatus;
    }

    /**
     * Runs the countMatch function for each UrlQuaCur (List of pages to visit in current parse cycle) value.
     * After traversing all links at the current level (UrlQuaCur is empty), it goes to the next level and
     * transfers UrlQuaFormed (List of pages to visit in next parse cycle) to UrlQuaCur and then clears UrlQuaFormed.
     * In the course of work, depth control is performed, adding the processed page to UrlArray (List of visited pages).
     */
    void parse() {
        int curDepth = 1;
        while (curDepth != maxDepth) {
            urlQuaCur.clear();
            urlQuaCur.addAll(urlQuaFormed);
            urlQuaFormed.clear();
            for (String urlProcessed : urlQuaCur) {
                writerRaw.flush();
                if (visited >= maxVisited) {
                    exitStatus = 2;
                    return;
                }
                urlArray.add(urlProcessed);
                countMatch(urlProcessed);
            }
            curDepth++;
        }
        if (urlQuaFormed.isEmpty()) {
            exitStatus = 0;
        } else {
            exitStatus = 1;
        }
    }

    /**
     * This method searches for the specified terms on the page located at the passed url, passes the search results
     * to the printToCSV method, and adds links from this page to the UrlQuaFormed if they are not contained in
     * UrlArray, UrlQuaFormed and UrlQuaCur.
     * Before adding, all links are reduced to a single form.
     * @param url Link to the page to be processed.
     */
    void countMatch(String url) {
        webClient.waitForBackgroundJavaScriptStartingBefore(timeOutJSBefore);
        webClient.waitForBackgroundJavaScript(timeOutJS);
        HtmlPage page;
        try {
            page = webClient.getPage(url);
        } catch (Exception e) {
            return;
        }
        visited++;
        StringBuilder matches = new StringBuilder();
        for (String Term : arOfTerms) {
            Pattern pattern = Pattern.compile(Term, caseSensitive);
            Matcher matcher = pattern.matcher(page.asText());
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            matches.append(count).append(",");
        }
        matches = new StringBuilder(matches.substring(0, matches.length() - 1));
        if (url.indexOf('/', 12) == -1) {
            url = url + '/';
        }
        String baseUrl = url.substring(0, url.indexOf('/', 12));
        List <HtmlAnchor> anchors = page.getAnchors();
        for (Object o : anchors) {
            HtmlAnchor anchor = (HtmlAnchor) o;
            String curHref = anchor.getHrefAttribute();
            if (curHref.length() < 2 || curHref.startsWith("#")) {
                continue;
            }
            if (curHref.contains("#")) {
                curHref = curHref.substring(0, curHref.indexOf('#'));
            }
            if (curHref.startsWith("//")) {
                curHref = curHref.replaceFirst("//", "https://");
            }
            if (curHref.startsWith("www.") || curHref.contains("//www.")) {
                curHref = curHref.replaceFirst("www.", "");
            }
            if (curHref.indexOf('/') != -1) if (curHref.substring(0, curHref.indexOf('/')).contains(".")) {
                curHref = "https://" + curHref;
            }
            if (curHref.startsWith("/")) {
                curHref = baseUrl + curHref;
            }
            if (!curHref.contains("://")) {
                curHref = url.substring(0, url.lastIndexOf('/')) + curHref;
            }
            if (!urlArray.contains(curHref) && !urlQuaFormed.contains(curHref) && !urlQuaCur.contains(curHref)) {
                urlQuaFormed.add(curHref);
            }
        }
        printToCSV(url, matches.toString());
    }

    /**
     * Outputs passed url and entries to csv file. Before writing, replaces commas with comma code in Percent-encoding.
     * Initiates flushing data from the buffer to a file
     * @param url url address (may contain commas)
     * @param Matches Comma separated string of occurrences
     */
    void printToCSV(String url, String Matches) {
        writerRaw.println(url.replace(",", "%2C") + "," + Matches);
        writerRaw.flush();
    }

    /**
     * Captures a file for writing throughout the entire process.
     * @throws Exception if having problems grabbing the file
     */
    void openRawCSV() throws Exception {
        FileWriter fileWriterRaw = new FileWriter(rawFileName, true);
        writerRaw = new PrintWriter(fileWriterRaw);
    }

    /**
     * Displays the specified number of URL addresses and entries for them in the csv file and
     * in the standard output Stream, sorted by the total number of entries.
     * @throws Exception if having problems grabbing the file
     */
    void getTopCSV() throws Exception {
        prepareCSV(topFileName);
        ArrayList<CSVTable> tempCsvTable = new ArrayList<>();
        FileReader fileReader = new FileReader(rawFileName);
        BufferedReader buffReader= new BufferedReader(fileReader);
        if (printHeader) buffReader.readLine();
        while (buffReader.ready()) {
            tempCsvTable.add(new CSVTable(buffReader.readLine()));
        }
        Collections.sort(tempCsvTable);
        FileWriter fileWriter = new FileWriter(topFileName, true);
        PrintWriter writer = new PrintWriter(fileWriter);
        for (int i = 0; i < numToPrintInTopFile; i++) {
            writer.println(tempCsvTable.get(i).getLine());
            System.out.println(tempCsvTable.get(i).getLine());
        }
        writer.close();
    }

    /**
     * Prepares the file passed as a parameter for writing. Rebuilds if file already exists,
     * add table header if printHeader - true.
     * @param fileName The path to the file to perform preparation.
     * @throws Exception if having problems grabbing the file
     */
    void prepareCSV(String fileName) throws Exception {
        PrintWriter writer = new PrintWriter(fileName, "UTF-8");
        if (printHeader) writer.println("URL," + termString);
        writer.close();
    }

    /**
     * Checks if the file passed as a parameter can be written to.
     * @param fileName The path to the file for which the writeability check is performed.
     * @throws Exception If writing is not possible, or there is a problem while deleting the test file.
     */
    void checkFilePerm(String fileName) throws Exception {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");
            writer.close();
            File file = new File(fileName);
            if (!file.delete()) {
                throw new Exception("Can't delete validity test file. Patch: " + file.getAbsolutePath());
            }
        }
        catch (Exception e) {
            throw new Exception("Can't write to file: " + fileName);
        }
    }
}