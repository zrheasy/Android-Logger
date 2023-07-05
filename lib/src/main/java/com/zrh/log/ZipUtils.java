package com.zrh.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author zrh
 * @date 2023/7/5
 */
public class ZipUtils {
    /**
     * 将列表文件合并到一个文件中进行zip压缩
     */
    public static File zip(File outputDir, String fileName, List<File> fileList) throws IOException {
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        fileName = getNameWithoutSuffix(fileName);
        File temp = new File(outputDir, fileName + ".temp");
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(temp))) {
            zipOutputStream.putNextEntry(new ZipEntry(fileName + ".log"));
            for (File file : fileList) {
                try (FileInputStream fileInputStream = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fileInputStream.read(buffer)) != -1) {
                        zipOutputStream.write(buffer, 0, len);
                    }
                }
            }
            zipOutputStream.closeEntry();
        }

        File output = new File(outputDir, fileName + ".zip");
        if (output.exists()) output.delete();
        temp.renameTo(output);

        return output;
    }

    private static String getNameWithoutSuffix(String name) {
        int index = name.lastIndexOf('.');
        if (index != -1) {
            return name.substring(0, index);
        }
        return name;
    }
}
