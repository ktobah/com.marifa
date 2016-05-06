package com.marifa.mappings;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import org.apache.log4j.Level;

public class DetectLanguage {

    public DetectLanguage(String profileDirectory) {
        try {
            DetectorFactory.loadProfile(profileDirectory);
        } catch (LangDetectException e) {
            Mappings.logMessage(Level.FATAL, e.getMessage());
        }
    }

    public String detect(String text) {
        Detector detector = null;
        try {
            detector = DetectorFactory.create();
        } catch (LangDetectException e) {
            Mappings.logMessage(Level.FATAL, e.getMessage());
        }
        detector.append(text);
        try {
            return detector.detect();
        } catch (LangDetectException e) {
            Mappings.logMessage(Level.FATAL, e.getMessage());
        }
        return null;
    }
}
