package com.ingloriousmind.android.imtimetracking.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * file util
 *
 * @author lavong.soysavanh
 */
public class FileUtil {

    /**
     * private app dir
     */
    public static File appDir;

    /**
     * @return archived pdf files
     */
    public static List<File> getArchivedPdfFiles() {
        List<File> pdfFiles = new ArrayList<>();
        File[] files = appDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getAbsolutePath().toLowerCase().endsWith(".pdf");
            }
        });
        if (files != null && files.length > 0) {
            pdfFiles.addAll(Arrays.asList(files));
        }
        return pdfFiles;
    }
}
