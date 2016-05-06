package com.marifa.mappings;

import com.cybozu.labs.langdetect.LangDetectException;
import com.marifa.parser.ArabicStemmer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Mappings {

    private static Logger log = Logger.getLogger(Mappings.class);
    private static ArabicStemmer arabicStemmer;
    private static DetectLanguage detectLanguage;
    private Map<String, String> mappings, rootsMappings;
    private int directMappings = 0, stemmerMappings = 0, namespaceMappings = 0;
    private String namespacePrefix;

    public Mappings(String namespacePrefix) throws LangDetectException, IOException, URISyntaxException {
        this.namespacePrefix = namespacePrefix;
        log.setLevel(Level.INFO);

        //This line one code might create a problem when generating the jar
        //Todo: Fix the file path.
        detectLanguage = new DetectLanguage(Thread.currentThread().getContextClassLoader().getResource("profiles").getPath());
        arabicStemmer = new ArabicStemmer();

        try {
            Reader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("mappings.properties"), "UTF-8"); //Thread.currentThread().getContextClassLoader().getResource("mappings.properties").getFile()
            logMessage(Level.INFO, "Retrieving the mappings from the mappings.properties file...");
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line, key, value;
            String[] keysValue;
            mappings = new HashMap<>();
            rootsMappings = new HashMap<>();

            while ((line = bufferedReader.readLine()) != null) {
                keysValue = line.split("=");
                key = keysValue[0];
                value = keysValue[1];
                mappings.put(key, value);
                arabicStemmer.normalizeText(value);
                rootsMappings.put(key, arabicStemmer.findRoot());
            }
            logMessage(Level.INFO, "Done! Total number of mappings retrieved: " + mappings.size());
        } catch (FileNotFoundException e) {
            logMessage(Level.FATAL, e.getMessage());
        } catch (IOException e) {
            logMessage(Level.FATAL, e.getMessage());
        }
    }

    public static void logMessage(Level level, String msg) {
        log.log(level, msg);
    }

    public static boolean detectLanguage(String text) {
        if (detectLanguage.detect(text).equalsIgnoreCase("ar"))
            return true;
        else
            return false;
    }

    public String getKey(String value) throws LangDetectException, IOException {
        value = value.toLowerCase();
        for (Object key : mappings.keySet()) {
            if (!detectLanguage.detect(value).equalsIgnoreCase("ar")) {
                if (key.toString().contains(value)) {
                    logMessage(Level.INFO, "\"" + value + "\" is mapped directly to the bibo property \"" + key.toString().replace('-', ':') + "\"");
                    directMappings++;
                    return key.toString().replace('-', ':');
                }
            } else {
                if (mappings.get(key).equalsIgnoreCase(value)) {
                    logMessage(Level.INFO, "\"" + value + "\" is mapped directly to the bibo property \"" + key.toString().replace('-', ':') + "\"");
                    directMappings++;
                    return key.toString().replace('-', ':');
                } else {
                    arabicStemmer.normalizeText(value);
                    if (rootsMappings.get(key).equals(arabicStemmer.findRoot())) {
                        logMessage(Level.INFO, "\"" + value + "\" is mapped, using the stemmer, to the bibo property \"" + key.toString().replace('-', ':') + "\"");
                        stemmerMappings++;
                        return key.toString().replace('-', ':');
                    }
                    if (rootsMappings.get(key).contains(arabicStemmer.findRoot())) {
                        logMessage(Level.INFO, "\"" + value + "\" is mapped, using the stemmer, to the bibo property \"" + key.toString().replace('-', ':') + "\"");
                        stemmerMappings++;
                        return key.toString().replace('-', ':');
                    }
                }
            }
        }
        logMessage(Level.INFO, "The column \"" + value + "\" does not exist in the mappings. Column mapped to " + namespacePrefix + ":" + value.replace(' ', '_'));
        namespaceMappings++;
        return namespacePrefix + ":" + value.replace(' ', '_');
    }

    public void printMappingStats() {
        logMessage(Level.INFO, "Total mappings: " + (directMappings + stemmerMappings + namespaceMappings) + " Direct mappings: " + directMappings +
                " Stemmer mappings: " + stemmerMappings + " Namespace mappings: " + namespaceMappings);
        logMessage(Level.INFO, "**************************************");
    }

    //Used for debugging purposes.
    public void printRootMappings() {
        System.out.println(rootsMappings);
    }
}
