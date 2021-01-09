package com.zmicierk.webcrawler;

/**
 * The class is the main class of the application. The "main" method of this class is the entry point into the program.
 * The tasks of this class are as follows:
 * Recognition of input command line arguments and checking their correctness.
 * Initializing the WebCrawlerThread class. Starting the craving process
 * (calling the start method of the WebCrawlerThread class).
 */
public class WebCrawler {
    /** The maximum number of pages to visit. */
    public static int maxVisited = 10000;
    /** The maximum number of transitions in depth relative to the initial page. */
    public static int maxDepth = 8;
    /** Enables/disables JavaScript support. If true JS enabled, else disabled. */
    public static boolean enableJS = true;
    /** Time to wait for a response from the server. */
    public static int timeOut = 7500;
    /**
     * The delay which determines the background tasks to wait for (in milliseconds)
     * Used in waitForBackgroundJavaScriptStartingBefore
     * This method blocks until all background JavaScript tasks scheduled to start executing
     * before (now + timeOutJSBefore) have finished executing.
     */
    public static int timeOutJSBefore = 1000;
    /**
     * The maximum amount of time to wait (in milliseconds)
     * Used in waitForBackgroundJavaScript
     * This method blocks until all background JavaScript tasks have finished executing.
     */
    public static int timeOutJS = 5000;
    /** The number of records in the file specified in the topFileName parameter. */
    public static int numToPrintInTopFile = 10;
    /**
     * The path to the CSV file in which top by total hits statistical data will be serialized (sorted by total hits).
     * The number of entries in this file is specified in the numToPrintInTopFile parameter.
     */
    public static String topFileName = "OutTop.csv";
    /** The path to the CSV file in which all statistical data will be serialized (without predefined sorting) */
    public static String rawFileName = "Out.csv";
    /** URL from which the process starts its work.(seed) */
    public static String startUrl;
    /** A term string to define the number of occurrences per page. Terms are separated by commas. */
    public static String termString;
    /** An array of term strings to determine the number of occurrences per page. */
    public static String[] arOfTerms;
    /**
     * Determines whether to display the table heading in output files.
     * true - display the table header
     * false - do not display the table header
     */
    public static boolean printHeader = true;
    /**
     * If true, the search will be case sensitive.
     */
    public static boolean caseSensitive = false;

    /**
     * This method is the entry point to the program. Accepts command line arguments.
     * Calls the method of their recognition, initializes WebCrawlerThread, starts the crawling process.
     * @param args Command line arguments passed to the application at startup
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            printHelpToStandardOutput();
            System.exit(-1);
        }
        encodeArgument(args);
        WebCrawlerThread crawlThread=null;
        try {
            crawlThread = new WebCrawlerThread(maxVisited, maxDepth, timeOutJSBefore, timeOutJS,
                    numToPrintInTopFile, timeOut, topFileName, rawFileName, startUrl, termString, arOfTerms, printHeader,
                    enableJS, caseSensitive);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        try {
            System.exit(crawlThread.start());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * The method decodes the input string and sets the values of the class fields in accordance
     * with the decoding result.
     * @param args Input string to decode.
     */
    public static void encodeArgument(String[] args) {
        for(int i=0; i < args.length; i++) {
            switch (args[i]) {
                case "-static":
                    enableJS =false;
                    break;
                case "-noheader":
                    printHeader =false;
                    break;
                case "-cs":
                    caseSensitive =true;
                    break;
                case "-d":
                    try {
                        maxDepth =Integer.parseInt(args[++i]);
                    }
                    catch (Exception e) {
                        System.out.println("Illegal maxDepth argument");
                        System.exit(-1);
                    }
                    break;
                case "-v":
                    try {
                        maxVisited = Integer.parseInt(args[++i]);
                    }
                    catch (Exception e) {
                        System.out.println("Illegal maxVisited argument");
                        System.exit(-1);
                    }
                    break;
                case "-to":
                    try {
                        timeOut = Integer.parseInt(args[++i]);
                    }
                    catch (Exception e) {
                        System.out.println("Illegal timeOut argument");
                        System.exit(-1);
                    }
                    break;
                case "-tojsb":
                    try {
                        timeOutJSBefore = Integer.parseInt(args[++i]);
                    }
                    catch (Exception e) {
                        System.out.println("Illegal waitForBackgroundJavaScriptStartingBefore argument");
                        System.exit(-1);
                    }
                    break;
                case "-tojs":
                    try {
                        timeOutJS = Integer.parseInt(args[++i]);
                    }
                    catch (Exception e) {
                        System.out.println("Illegal waitForBackgroundJavaScript argument");
                        System.exit(-1);
                    }
                    break;
                case "-nt":
                    try {
                        numToPrintInTopFile = Integer.parseInt(args[++i]);
                    }
                    catch (Exception e) {
                        System.out.println("Illegal numToPrintInTopFile argument");
                        System.exit(-1);
                    }
                    break;
                case "-tf":
                    try {
                        topFileName = args[++i];
                    }
                    catch (Exception e) {
                        System.out.println("Illegal topFileName argument");
                        System.exit(-1);
                    }
                    break;
                case "-f":
                    try {
                        rawFileName = args[++i];
                    }
                    catch (Exception e) {
                        System.out.println("Illegal rawFileName argument");
                        System.exit(-1);
                    }
                    break;
                case "-s":
                    try {
                        startUrl = args[++i];
                    }
                    catch (Exception e) {
                        System.out.println("Illegal startUrl argument");
                        System.exit(-1);
                    }
                    break;
                case "-t":
                    try {
                        termString = args[++i];
                    }
                    catch (Exception e) {
                        System.out.println("Illegal termString argument");
                        System.exit(-1);
                    }
                    break;
                case "-help":
                case "-?":
                    printHelpToStandardOutput();
                    System.exit(0);
                default:
                    System.out.println("Invalid key: "+args[i]);
                    System.out.println("use key -? to get help");
                    System.exit(-1);
            }
        }
        try {
            arOfTerms = termString.split(",");
        }
        catch (Exception e) {
            System.out.println("Illegal TermsList argument");
            System.exit(-1);
        }

    }

    /**
     * Used to display help information about the application.
     */
    public static void printHelpToStandardOutput() {
        System.out.println("NAME");
        System.out.println("  WebCrawler - Start crawling process\n");
        System.out.println("SYNOPSIS");
        System.out.println("  WebCrawler.jar [args]\n");
        System.out.println("DESCRIPTION");
        System.out.println("  Web crawler is a program that visits web pages starting from a given one and follows" +
                " all the links presented on the pages (in accordance with a given depth and a limit on the number" +
                " of pages visited), in addition, in the process of work, it performs some useful actions on the page" +
                " in this case collects statistics (the number of occurrences of the specified terms) \n");
        System.out.println("ARGUMENTS");
        System.out.println(" The order in which the arguments are specified does not matter.\n");
        System.out.println(" Mandatory:");
        System.out.println("  -s startUrl \n\tURL from which the process starts its work.(seed)");
        System.out.println("  -t termString \n\tA term string to define the number of occurrences per page. " +
                "Terms are separated by commas.\n");
        System.out.println(" Optional:");
        System.out.println("  -d maxDepth \n\tThe maximum number of transitions in depth relative to the initial " +
                "page.\n\tDefault: 8");
        System.out.println("  -static \n\tDisable JavaScript\n\tDefault: JavaScript enabled");
        System.out.println("  -noheader \n\tDo not print the table header to files" +
                "\n\tDefault: table header printed to files");
        System.out.println("  -cs \n\tPerform a case sensitive search.\n\tDefault: Case insensitive");
        System.out.println("  -v maxVisited \n\tThe maximum number of pages to visit.\n\tDefault: 10000");
        System.out.println("  -to timeOut(in milliseconds) \n\tTime to wait for a response from the server." +
                "\n\tDefault: 7500");
        System.out.println("  -tojsb timeOutJSBefore(in milliseconds) \n\tThe delay which determines the background " +
                "tasks to wait until all background JavaScript tasks scheduled to start executing" +
                "before (now + timeOutJSBefore) have finished executing.\n\tDefault: 1000");
        System.out.println("  -tojs timeOutJS(in milliseconds) \n\tThe maximum amount of time to wait until" +
                " all background JavaScript tasks have finished executing.\n\tDefault: 5000");
        System.out.println("  -nt numToPrintInTopFile \n\tThe number of records in the topFile.\n\tDefault: 10");
        System.out.println("  -tf topFileName \n\tThe path to the CSV file in which top by total hits statistical data " +
                "will be serialized (sorted by total hits). The number of entries in this file is specified in the" +
                " -nt parameter.\n\tDefault: OutTop.csv in current dir");
        System.out.println("  -f fileName \n\tThe path to the CSV file in which all statistical data will be serialized" +
                "(without predefined sorting)\n\tDefault: Out.csv in current dir\"");
        System.out.println("  -? -help \n\tprint this help message.\n");
        System.out.println("EXAMPLE");
        System.out.println("  WebCrawler -a \"https://en.wikipedia.org/wiki/Java_(programming_language)\" -d 4 -v 30" +
                " -t \"Java,Oracle,James Gosling,programming language\" -static -noheader -to 10500" +
                " -nt 12 -tf TopFileExample.csv -f RawFileExample.csv");
        System.out.println("  This means that you need to crawl starting from the page " +
                "https://en.wikipedia.org/wiki/Java_(programming_language) with a depth of 4, a maximum number of" +
                " pages visited 30, search terms are: \"Java, Oracle, James Gosling, programming language\", the " +
                "java script will not be executed, the output tables will not have a header, the waiting time is " +
                "10500, The number of records in the topFile - 12 The path to the CSV file in which all statistical " +
                "data will be serialized (without predefined sorting) - RawFileExample.csv(located in current dir) " +
                "The path to the CSV file in which top by total hits statistical data will be serialized " +
                "(sorted by total hits) - TopFileExample.csv(located in current dir)\n");
        System.out.println("EXIT STATUS");
        System.out.println("   0 Successful completion (no pages to process)");
        System.out.println("   1 Successful completion (maximum depth(-d argument) reached)");
        System.out.println("   2 Successful completion (page visit limit(-v argument) reached)");
        System.out.println("  -1 Unsuccessful completion");
    }
}