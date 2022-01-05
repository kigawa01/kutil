package net.kigawa.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

public class Logger {
    private static Logger logger;
    private final java.util.logging.Logger javaLogger;
    private final String Name;

    protected Logger(String name, java.util.logging.Logger parentLogger, Level logLevel, Path logDirPath, Handler... handlers) {
        Name = name;

        if (parentLogger != null) name = parentLogger.getName() + "." + name;
        javaLogger = java.util.logging.Logger.getLogger(name);

        javaLogger.setLevel(logLevel);

        if (logDirPath != null) {
            logDirPath.toFile().mkdirs();
            Calendar calendar = Calendar.getInstance();
            StringBuffer logName = Util.addYearToDate(new StringBuffer(Name), "-");
            File logFile = new File(logDirPath.toFile(), Extension.log.addExtension(logName.toString()));
            int i = 0;
            while (logFile.exists()) {
                logFile = new File(logDirPath.toFile(), Extension.log.addExtension(logName + "-" + i));
                i++;
            }

            try {
                logFile.createNewFile();
                Handler handler = new FileHandler(logFile.getAbsolutePath());
                javaLogger.addHandler(handler);
                handler.setFormatter(new Formatter());
            } catch (IOException e) {
                Logger.getInstance().warning(e);
            }

        }

        for (Handler handler : handlers) {
            javaLogger.addHandler(handler);
        }
    }

    public static void enable(String name, java.util.logging.Logger parentLogger, Level logLevel, File logDir, Handler... handlers) {
        logger = new Logger(name, parentLogger, logLevel, logDir.toPath(), handlers);
    }

    public static Logger getInstance() {
        if (logger == null) logger = new Logger("logger", null, null, null);
        return logger;
    }

    public void fine(Object o) {
        log(o, Level.FINE);
    }

    public void warning(Object o) {
        log(o, Level.WARNING);
    }

    public void severe(Object o) {
        log(o, Level.SEVERE);
    }

    public void info(Object o) {
        log(o, Level.INFO);
    }

    public void config(Object o) {
        log(o, Level.CONFIG);
    }

    public void all(Object o) {
        log(o, Level.ALL);
    }

    public void finer(Object o) {
        log(o, Level.FINER);
    }

    public void finest(Object o) {
        log(o, Level.FINEST);
    }

    public void off(Object o) {
        log(o, Level.OFF);
    }

    public synchronized void log(Object o, Level level) {
        if (o.getClass().isArray()) {
            log(o.toString(), level);
            for (Object o1 : (Object[]) o) {
                log(o1, level);
            }
            return;
        }
        if (o instanceof Throwable) {
            Throwable throwable = (Throwable) o;
            log(throwable.toString(), level);
            log(throwable.getStackTrace(), level);
            log(throwable.getSuppressed(), level);
            return;
        }
        if (o instanceof StackTraceElement) {
            StackTraceElement element = (StackTraceElement) o;
            log("\tat " + element, level);
        }
        javaLogger.log(level, o.toString());
    }

    public java.util.logging.Logger getJavaLogger() {
        return javaLogger;
    }

    /**
     * @deprecated
     */
    public void logger(String message) {
        fine(message);
    }
}
