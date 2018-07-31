package com.browser.core.logging;

import android.content.Context;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.StatusPrinter;
import timber.log.Timber;

public class FileLoggingTree extends Timber.DebugTree {

    private static Logger mLogger = LoggerFactory.getLogger(FileLoggingTree.class);
    private static final String LOG_PREFIX = "manadr-log";

    public FileLoggingTree(Context context) {
        configureLogger(context);
    }

    private void configureLogger(Context context) {
        // reset the default context (which may already have been initialized)
        // since we want to reconfigure it
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(loggerContext);
        rollingFileAppender.setAppend(true);
        rollingFileAppender.setFile(getLatestLog(context).getAbsolutePath());

        SizeAndTimeBasedFNATP<ILoggingEvent> fileNamingPolicy = new SizeAndTimeBasedFNATP<>();
        fileNamingPolicy.setContext(loggerContext);
        fileNamingPolicy.setMaxFileSize("1MB");

        TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
        rollingPolicy.setContext(loggerContext);
        rollingPolicy.setFileNamePattern(getLogDir(context).getAbsolutePath() + "/" + LOG_PREFIX + ".%d{yyyy-MM-dd}.%i.log");
        rollingPolicy.setMaxHistory(5);
        rollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(fileNamingPolicy);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.start();

//        HTMLLayout htmlLayout = new HTMLLayout();
//        htmlLayout.setContext(loggerContext);
//        htmlLayout.setPattern("%d{HH:mm:ss.SSS}%level%thread%msg");
//        htmlLayout.start();
//        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
//        encoder.setContext(loggerContext);
//        encoder.setLayout(htmlLayout);
//        encoder.start();

        //Alternative text encoder - very clean pattern, takes up less space
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setCharset(Charset.forName("UTF-8"));
        encoder.setPattern("%date %level [%thread] %msg%n");
        encoder.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);
        rollingFileAppender.setEncoder(encoder);
        rollingFileAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.DEBUG);
        root.addAppender(rollingFileAppender);

        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(loggerContext);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {
        if (priority == Log.VERBOSE) {
            return;
        }

        String logMessage = tag + ": " + message;
        switch (priority) {
            case Log.DEBUG:
                mLogger.debug(logMessage);
                break;
            case Log.INFO:
                mLogger.info(logMessage);
                break;
            case Log.WARN:
                mLogger.warn(logMessage);
                break;
            case Log.ERROR:
                mLogger.error(logMessage);
                break;
        }
    }

    public static File getCloneLatestLog(Context context) {
        File log = getLatestLog(context);
        File tmp = new File(getLogDir(context), "latest-manadr-log.log");
        if (tmp.exists()) {
            try {
                FileUtils.forceDelete(tmp);
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        try {
            FileUtils.copyFile(log, tmp);
        } catch (IOException e) {
            Timber.e(e);
        }
        return tmp;
    }

    public static File getLatestLog(Context context) {
        return new File(getLogDir(context), LOG_PREFIX + "-latest.log");
    }

    public static File getLogDir(Context context) {
        File logDir = new File(context.getFilesDir(), "logs");
        if (!logDir.exists()) logDir.mkdirs();
        return logDir;
    }
}
