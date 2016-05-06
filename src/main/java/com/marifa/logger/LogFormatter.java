package com.marifa.logger;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static StringBuilder sb;

    @Override
    public String format(LogRecord record) {
        sb = new StringBuilder();

        sb.append(new Date(record.getMillis()))
                .append(" ")
                .append(record.getLevel().getLocalizedName())
                .append(": ")
                .append(formatMessage(record))
                .append(LINE_SEPARATOR);
        return sb.toString();
    }
}