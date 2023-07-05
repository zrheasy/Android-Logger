package com.zrh.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author zrh
 * @date 2023/7/5
 */
public class Logger {
    private Logger() {}

    private static List<Printer> printers = Collections.synchronizedList(new ArrayList<>());

    public static void addPrinter(Printer printer) {
        printers.add(printer);
    }

    public static <T extends Printer> T getPrinter(Class<T> clazz) {
        for (Printer printer : printers) {
            if (printer.getClass() == clazz) {
                return (T) printer;
            }
        }
        return null;
    }

    public static void println(Level level, String tag, String msg) {
        for (Printer printer : printers) {
            if (printer.isEnable()) printer.println(level, tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        println(Level.INFO, tag, msg);
    }

    public static void d(String tag, String msg) {
        println(Level.DEBUG, tag, msg);

    }

    public static void w(String tag, String msg) {
        println(Level.WARN, tag, msg);

    }

    public static void e(String tag, String msg) {
        println(Level.ERROR, tag, msg);
    }

    public static void e(String tag, Throwable error) {
        println(Level.ERROR, tag, getStackTraceString(error));
    }

    public static String getStackTraceString(Throwable error) {
        if (error == null) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        error.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }
}
