package com.zmicierk.webcrawler;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import org.junit.jupiter.api.Test;
import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The class is intended for testing the package, includes methods that implement testing of all methods
 * of the following classes: WebCrawler, WebCrawlerThread, CSVTable. Includes Unit Testing,
 * Integration testing and System testing.
 */
class WebCrawlerTest {

    /**
     * Checking the correctness of the operation of the encodeArgument method of the WebCrawler class.
     */
    @Test
    void encodeArgumentTest() {
        String [] testArg = new String[] {
                        "-s", "https://en.wikipedia.org/wiki/Elon_Musk", "-d", "4", "-v", "30", "-t",
                        "Elon,Tesla,Gigafactory,Elon Mask", "-static", "-noheader", "-to", "10500", "-tojsb", "500",
                        "-tojs", "1700", "-nt", "12", "-tf", "test_top.csv", "-f", "top.csv"};
        WebCrawler.encodeArgument(testArg);
        assertEquals(WebCrawler.startUrl, testArg[1]);
        assertEquals(WebCrawler.maxDepth, Integer.parseInt(testArg[3]));
        assertEquals(WebCrawler.maxVisited, Integer.parseInt(testArg[5]));
        String[] testString = testArg[7].split(",");
        for (int i = 0; i < testString.length; i++) {
            assertEquals(WebCrawler.arOfTerms[i], testString[i]);
        }
        assertFalse(WebCrawler.enableJS);
        assertFalse(WebCrawler.printHeader);
        assertEquals(WebCrawler.timeOut, Integer.parseInt(testArg[11]));
        assertEquals(WebCrawler.timeOutJSBefore, Integer.parseInt(testArg[13]));
        assertEquals(WebCrawler.timeOutJS, Integer.parseInt(testArg[15]));
        assertEquals(WebCrawler.numToPrintInTopFile, Integer.parseInt(testArg[17]));
        assertEquals(WebCrawler.topFileName, testArg[19]);
        assertEquals(WebCrawler.rawFileName, testArg[21]);
    }

    /**
     * Checking the correctness of the operation of the getLine, getTotalMatches methods of the CSVTable class.
     */
    @Test
    void fullCSVTableTest() {
        String testArg = "https://en.wikipedia.org/wiki/Elon_Musk,1,2,3,4,5";
        CSVTable testCSVTable = new CSVTable(testArg);
        assertEquals(testCSVTable.getLine(), testArg);
        assertEquals(testCSVTable.getTotalMatches(), 15);
    }

    /**
     * Checking the correctness of the operation of the prepareCSV method of the WebCrawler class.
     * Requires an up-to-date file:"act_preptest.csv"
     * @throws Exception if test failed.
     */
    @Test
    void prepareCsvTest() throws Exception {
        String expRawFileName = "res" + File.separator + "preptest.csv";
        String actRawFileName = "res" + File.separator + "act_preptest.csv";
        String testString = "Elon,Tesla,Gigafactory,Elon Mask";
        try {
            PrintWriter writer = new PrintWriter(expRawFileName, "UTF-8");
            writer.close();
            File file = new File(expRawFileName);
            if (!file.delete()) {
                System.out.println("Can't delete file");
            }
        }
        catch (Exception e) {
            System.out.println("Can't write to file");
        }
        WebCrawlerThread prepareCsvCrawlerThread = new WebCrawlerThread(
                30, 4, 500, 1700, 12, 10500,
                "res" + File.separator + "cmtop_test.csv", expRawFileName,
                "https://en.wikipedia.org/wiki/Elon_Musk", testString, testString.split(","),
                false, false, false);
        prepareCsvCrawlerThread.prepareCSV(expRawFileName);
        checkFilesContext(actRawFileName, expRawFileName);
    }

    /**
     * Checking the correctness of the operation of the prepareCSV, openRawCSV, printToCSV methods
     * of the WebCrawler class.
     * @throws Exception if test failed.
     */
    @Test
    void printToCsvTest() throws Exception {
        String expRawFileName = "res" + File.separator + "prtest.csv";
        String actRawFileName = "res" + File.separator + "act_prtest.csv";
        String testUrl = "https://en.wikipedia.org/wiki/Elon_Musk,1,2,3,4,5";
        String matches = "1,2,3,4,5";
        String testString = "Elon,Tesla,Gigafactory,Elon Mask";
        try {
            PrintWriter writer = new PrintWriter(expRawFileName, "UTF-8");
            writer.close();
            File file = new File(expRawFileName);
            if (!file.delete()) {
                System.out.println("Can't delete file");
            }
        }
        catch (Exception e) {
            System.out.println("Can't write to file");
        }
        WebCrawlerThread printToCsvCrawlerThread = new WebCrawlerThread(
                30, 4, 500, 1700, 12, 10500,
                "_test.csv", expRawFileName, "https://en.wikipedia.org/wiki/Elon_Musk",
                testString, testString.split(","), false, false, false);
        printToCsvCrawlerThread.prepareCSV(expRawFileName);
        printToCsvCrawlerThread.openRawCSV();
        printToCsvCrawlerThread.printToCSV(testUrl, matches);
        checkFilesContext(actRawFileName, expRawFileName);
    }

    /**
     * Checking the correctness of the operation of the prepareCSV, openRawCSV, countMatch methods
     * of the WebCrawler class.
     * Requires an up-to-date file:"act_cmtest.csv"
     * @throws Exception if test failed.
     */
    @Test
    void countMatchTest() throws Exception {
        String expRawFileName = "res" + File.separator + "cmtest.csv";
        String actRawFileName = "res" + File.separator + "act_cmtest.csv";
        String testString = "Object,cass,variable,data,programming languages";
        String startUrl = "https://en.wikipedia.org/wiki/Object-oriented_programming";
        try {
            PrintWriter writer = new PrintWriter(expRawFileName, "UTF-8");
            writer.close();
            File file = new File(expRawFileName);
            if (!file.delete()) {
                System.out.println("Can't delete file");
            }
        }
        catch (Exception e) {
            System.out.println("Can't write to file");
        }
        WebCrawlerThread countMatchCrawlerThread=new WebCrawlerThread(
                30, 4, 500, 1700, 12, 10500,
                "res" + File.separator + "cmtop_test.csv", expRawFileName, startUrl,
                testString, testString.split(","), false, false, false);
        countMatchCrawlerThread.prepareCSV(expRawFileName);
        countMatchCrawlerThread.openRawCSV();
        countMatchCrawlerThread.urlArray.add(startUrl);
        countMatchCrawlerThread.countMatch(startUrl);
        checkFilesContext(actRawFileName, expRawFileName);
    }

    /**
     * Checking the correctness of the operation of the prepareCSV, openRawCSV, countMatch, parse methods
     * of the WebCrawler class.
     * Requires an up-to-date file:"act_ptest.csv"
     * @throws Exception if test failed.
     */
    @Test
    void parseTest() throws Exception {
        String expRawFileName = "res" + File.separator + "ptest.csv";
        String actRawFileName = "res" + File.separator + "act_ptest.csv";
        String startUrl = "https://en.wikipedia.org/wiki/Object-oriented_programming";
        String testString = "Object,class,variable,data,programming languages";
        try {
            PrintWriter writer = new PrintWriter(expRawFileName, "UTF-8");
            writer.close();
            File file = new File(expRawFileName);
            if (!file.delete()) {
                System.out.println("Can't delete file");
            }
        }
        catch (Exception e) {
            System.out.println("Can't write to file");
        }
        WebCrawlerThread parseCrawlerThread = new WebCrawlerThread(
                30, 4, 500, 1700, 12, 10500,
                "_test.csv", expRawFileName, startUrl,
                testString, testString.split(","), false, false, false);
        parseCrawlerThread.prepareCSV(expRawFileName);
        parseCrawlerThread.openRawCSV();
        parseCrawlerThread.urlArray.add(startUrl);
        parseCrawlerThread.countMatch(startUrl);
        parseCrawlerThread.parse();
        checkFilesContext(actRawFileName, expRawFileName);
    }

    /**
     * Checking the correctness of the operation of the start method of the WebCrawler class.
     * Requires an up-to-date file:"act_st_top_test.csv", "act_sttest.csv"
     * @throws Exception if test failed.
     */
    @Test
    void startTest() throws Exception {
        String expRawFileName = "res" + File.separator + "sttest.csv";
        String actRawFileName = "res" + File.separator + "act_sttest.csv";
        String expTopFileName = "res" + File.separator + "st_top_test.csv";
        String actTopFileName = "res" + File.separator + "act_st_top_test.csv";
        String startUrl = "https://en.wikipedia.org/wiki/Object-oriented_programming";
        String testString = "Object,class,variable,data,programming languages";
        try {
            PrintWriter writer = new PrintWriter(expRawFileName, "UTF-8");
            writer.close();
            File file = new File(expRawFileName);
            if (!file.delete()) {
                System.out.println("Can't delete file");
            }
        }
        catch (Exception e) {
            System.out.println("Can't write to file");
        }
        WebCrawlerThread startCrawlerThread=new WebCrawlerThread(
                30, 4, 500, 1700, 12, 10500,
                expTopFileName, expRawFileName, startUrl,
                testString, testString.split(","), false, false, false);
        startCrawlerThread.start();
        checkFilesContext(actRawFileName, expRawFileName);
        checkFilesContext(actTopFileName, expTopFileName);
    }

    /**
     * A complete system test treats the entire system as a black box.
     * Requires an up-to-date file:"act_systest.csv", "act_sys_top_test.csv"
     * @throws Exception if test failed.
     */
    @Test
    @ExpectSystemExitWithStatus(2)
    void systemTest() throws Exception {
        String expRawFileName = "res" + File.separator + "systest.csv";
        String actRawFileName = "res" + File.separator + "act_systest.csv";
        String expTopFileName = "res" + File.separator + "sys_top_test.csv";
        String actTopFileName = "res" + File.separator + "act_sys_top_test.csv";
        String [] testArg=new String[] {
                "-s", "https://en.wikipedia.org/wiki/Object-oriented_programming", "-d", "4", "-v", "30", "-t",
                "Object,class,variable,data,programming languages", "-static", "-noheader", "-to", "10500", "-tojsb",
                "500", "-tojs", "1700", "-nt", "12", "-tf", expTopFileName, "-f", expRawFileName};
        try {
            PrintWriter writer = new PrintWriter(expRawFileName, "UTF-8");
            writer.close();
            File file = new File(expRawFileName);
            if (!file.delete()) {
                System.out.println("Can't delete file");
            }
        }
        catch (IOException e) {
            System.out.println("Can't write to file");
        }
        WebCrawler.main(testArg);
        checkFilesContext(actRawFileName, expRawFileName);
        checkFilesContext(actTopFileName, expTopFileName);
    }

    /**
     * Checking the correctness of the operation of the getTopCSV method of the WebCrawler class.
     * Requires an up-to-date file:"get_top_test_raw.csv", "act_get_top.csv"
     * @throws Exception if test failed.
     */
    @Test
    void getTopCSVTest() throws Exception {
        String expTopFileName = "res" + File.separator + "get_top_test_proc.csv";
        String rawFileName = "res" + File.separator + "get_top_test_raw.csv";
        String rawTempFileName = "res" + File.separator + "testTemp.csv";
        String actTopFileName = "res" + File.separator + "act_get_top.csv";
        String startUrl = "https://en.wikipedia.org/wiki/Object-oriented_programming";
        String testString = "Object,class,variable,data,programming languages";
        copyFile(rawFileName, rawTempFileName);
        WebCrawlerThread topCrawlerThread=new WebCrawlerThread(
                30, 4, 500, 1700, 10, 10500,
                expTopFileName, rawFileName, startUrl, testString, testString.split(","), false,
                false, false);
        copyFile(rawTempFileName, rawFileName);
        topCrawlerThread.getTopCSV();
        checkFilesContext(actTopFileName, expTopFileName);
    }

    /**
     * The function compares the files passed as parameters, if the files do not match throws IOException.
     * @param ActFileName Reference file (file 1)
     * @param ExpFileName Comparison file (file 2)
     * @throws Exception if files do not match
     */
    static void checkFilesContext(String ActFileName, String ExpFileName) throws Exception {
        FileReader fileReaderExpected = new FileReader(ExpFileName);
        BufferedReader buffReaderExpected= new BufferedReader(fileReaderExpected);
        FileReader fileReaderActual = new FileReader(ActFileName);
        BufferedReader buffReaderActual = new BufferedReader(fileReaderActual);
        while (buffReaderExpected.ready()) {
            assertEquals(buffReaderExpected.readLine(), buffReaderActual.readLine());
        }
        assertNull(buffReaderExpected.readLine());
        assertNull(buffReaderActual.readLine());

    }

    /**
     * Copies the file from sourceFilePatch to destFilePatch.
     * @param sourceFilePatch Source file path.
     * @param destFilePatch Destination file path.
     * @throws Exception If copying failed.
     */
    static void copyFile(String sourceFilePatch, String destFilePatch) throws Exception {
        File source = new File(sourceFilePatch);
        File dest = new File(destFilePatch);
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}