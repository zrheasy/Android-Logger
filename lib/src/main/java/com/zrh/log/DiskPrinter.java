package com.zrh.log;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kotlin.jvm.Synchronized;

/**
 * @author zrh
 * @date 2023/7/5
 */
public class DiskPrinter implements Printer {
    private final Handler mHandler;
    private final File mRootDir;
    // 每个日志文件大小，默认1m
    private int maxFileLength = 1024 * 1024;
    // 日志文件的最大数量，最大存储大小 = maxFileLength * maxFileCount
    private int maxFileCount = Integer.MAX_VALUE;

    private File mLastFile = null;

    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.ENGLISH);

    public DiskPrinter(File rootDir) {
        mRootDir = rootDir;
        if (!mRootDir.exists()) {
            mRootDir.mkdirs();
        }

        HandlerThread handlerThread = new HandlerThread("DiskPrinter");
        handlerThread.start();
        mHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                String text = msg.obj.toString();
                writeMessageToFile(text);
            }
        };

        File[] files = getDescFileList(mRootDir);
        if (files.length > 0) {
            mLastFile = files[0];
        } else {
            mLastFile = getNewFile();
        }
    }

    public void setMaxFileLength(int maxFileLength) {
        this.maxFileLength = maxFileLength;
    }

    public void setMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }

    @Override
    public void println(Level level, String tag, String msg) {
        Message message = mHandler.obtainMessage();
        message.obj = createMessage(level, tag, msg);
        mHandler.sendMessage(message);
    }

    /**
     * 在系统发生崩溃时调用
     */
    public void printCrash(String tag, Throwable error) {
        String msg = createMessage(Level.ERROR, tag, Logger.getStackTraceString(error));
        writeMessageToFile(msg);
    }

    @Synchronized
    private String createMessage(Level level, String tag, String msg) {
        StringBuilder sb = new StringBuilder();
        String time = mTimeFormat.format(new Date());
        sb.append(time).append(" ").append(level.getName()).append("/").append(tag).append(": ").append(msg);
        sb.append("\n");
        return sb.toString();
    }

    @Synchronized
    private void writeMessageToFile(String msg) {
        File file = getLogFile(msg.length());
        try (FileWriter fileWriter = new FileWriter(file, true)) {
            fileWriter.append(msg);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getLogFile(int appendLength) {
        if (mLastFile.length()+appendLength >= maxFileLength) {
            // create new file
            File[] files = getDescFileList(mRootDir);
            if (files.length == maxFileLength) {
                // delete oldest file
                files[files.length - 1].delete();
            }
            mLastFile = getNewFile();
        }

        return mLastFile;
    }

    private File getNewFile() {
        return new File(mRootDir, System.currentTimeMillis() + ".log");
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    public File zipLatestLogs(File outputDir, String fileName, long maxSize) throws IOException {
        long size = 0;
        List<File> fileList = new ArrayList<>();
        for (File file : getDescFileList(mRootDir)) {
            if (size >= maxSize) break;
            size += file.length();
            fileList.add(file);
        }
        Collections.sort(fileList);
        return ZipUtils.zip(outputDir, fileName, fileList);
    }

    private File[] getDescFileList(File dir) {
        File[] result = new File[0];
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                result = files;
            }
        }
        // 按照日期从高到低排序
        Arrays.sort(result, Collections.reverseOrder());
        return result;
    }

}
