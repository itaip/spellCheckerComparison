package com.itai.spellcheck.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.itai.spellcheck.common.Pair;
import com.itai.spellcheck.ebayspellchecker.EbaySpellChecker;
import com.itai.spellcheck.languageTool.LanguageTool;
import org.slf4j.LoggerFactory;

/**
 * @author Itai Peleg
 *         Created on 11/12/2014.
 */
public class Main {
    protected static List<String> inputs;
    protected static String microSeconds = "\u00B5" + "s";

    public static void main(String[] args) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);
        List<String> result = new ArrayList<>();
        try {
            Main main = new Main();
            main.loadFile();

            result.add(main.languageToolRun());
            result.add(main.ebaySoapRun());

            System.out.println("\n**************************************************");
            for (String str : result) {
                System.out.printf(str);
            }
            main.pingUrl("www.ebay.com");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String languageToolRun() throws Exception {
        System.out.println("\n**************************************************");
        System.out.println("\t\tLanguageToolRun");
        System.out.println("**************************************************");
        LanguageTool languageTool = new LanguageTool();
        int spellCount = 0;
        int wordCount = 0;
        int expCount = 0;
        long totalTime = 0;
        languageTool.spellCheck(inputs.get(0));
        for (String input : inputs) {
            long a = System.nanoTime();
            try {
                List<Pair<String, String>> pairs = languageTool.spellCheck(input);
                long b = System.nanoTime();
                totalTime += (b - a);
                spellCount += pairs.size();
                wordCount += input.split(" ").length;
            } catch (Exception e) {
                System.out.println("input= " + input + "\n" + e.getMessage());
                expCount++;
            }
        }
        String str = formatResult("LanguageTool", spellCount, wordCount, expCount, totalTime);
        System.out.printf(str);
        return str;
    }

    private String ebaySoapRun() throws Exception {
        EbaySpellChecker checker = new EbaySpellChecker();
        System.out.println("\n**************************************************");
        System.out.println("\t\tebaySoapRun");
        System.out.println("**************************************************");
        int spellCount = 0;
        int wordCount = 0;
        int expCount = 0;
        long totalTime = 0;
        checker.spellCheckOneToken(inputs.get(0));
        for (String input : inputs) {
            long a = System.nanoTime();
            try {
                Pair<String, String> pair = checker.spellCheckOneToken(input);
                long b = System.nanoTime();
                totalTime += (b - a);
                if (pair != null) {
                    spellCount++;
                }
                wordCount += input.split(" ").length;
            } catch (Exception e) {
                System.out.println("input= " + input + "\n" + e.getMessage());
                expCount++;
            }
        }
        String str = formatResult("eBaySoap", spellCount, wordCount, expCount, totalTime);
        System.out.printf(str);
        return str;
    }

    private String formatResult(String algo, int spellCount, int wordCount, int expCount, long totalTime) {
        long avgWordTime = TimeUnit.NANOSECONDS.toMicros(totalTime / wordCount);
        String avgWordTimeUnits = microSeconds;
        if (TimeUnit.MICROSECONDS.toMillis(avgWordTime) > 1) {
            avgWordTime = TimeUnit.MICROSECONDS.toMillis(avgWordTime);
            avgWordTimeUnits = "ms";
        }

        return String.format("%s - took %dms, total lines = %d, total words = %d, total correction = %d, " +
                        "exceptionCount= %d, avgTime per call= %dms, avgTime per correction= %d, avgTime per word= " +
                        "%d%s\n", algo, TimeUnit.NANOSECONDS.toMillis(totalTime), inputs.size(),
                wordCount, spellCount, expCount, TimeUnit.NANOSECONDS.toMillis(totalTime / inputs.size()),
                TimeUnit.NANOSECONDS.toMillis(totalTime / spellCount),
                avgWordTime, avgWordTimeUnits);
    }

    private void loadFile() throws Exception {
        URL url = Thread.currentThread().getContextClassLoader().getResource("input.txt");
        BufferedReader br = new BufferedReader(new FileReader(new File(url.toURI())));
        String line;
        inputs = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            inputs.add(line);
        }
        br.close();
        System.out.println("load " + inputs.size() + " lines from file");
    }

    public void pingUrl(final String address) throws Exception {
        final URL url = new URL("http://" + address);
        final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(1000 * 10); // mTimeout is in seconds
        final long startTime = System.currentTimeMillis();
        urlConn.connect();
        final long endTime = System.currentTimeMillis();
        if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.print("Ping to " + address + " was success, ");
            System.out.println("Time (ms) : " + (endTime - startTime));
        }
    }

}
