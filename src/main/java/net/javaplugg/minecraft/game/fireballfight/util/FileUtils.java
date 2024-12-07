package net.javaplugg.minecraft.game.fireballfight.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@SuppressWarnings("deprecation")
public class FileUtils extends org.apache.commons.io.FileUtils {

    public static void cleanDirectory(Object directory) throws IOException {
        File directory0 = adapt(directory);
        if (!directory0.isDirectory()) {
            throw new IllegalArgumentException("Directory " + directory0 + " is not a directory");
        }
        File[] files = directory0.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            FileUtils.forceDelete(file);
        }
    }

    public static void copyDirectoryContentsToDirectory(Object source, Object destination) throws IOException {
        File source0 = adapt(source);
        File destination0 = adapt(destination);
        if (!source0.isDirectory()) {
            throw new IllegalArgumentException("Source " + source0 + " is not a directory");
        }
        if (!destination0.isDirectory()) {
            throw new IllegalArgumentException("Destination " + destination0 + " is not a directory");
        }
        File[] files = source0.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            FileUtils.copyToDirectory(file, destination0);
        }
    }

    public static File getRandomFile(Object directory) {
        File directory0 = adapt(directory);
        if (!directory0.isDirectory()) {
            throw new IllegalArgumentException("Directory " + directory0 + " is not a directory");
        }
        File[] filesArray = directory0.listFiles();
        if (filesArray == null || filesArray.length < 1) {
            throw new IllegalArgumentException("Could not choose random file: There are no files in " + directory0 + " or some other error occurred");
        }
        int randomIndex = (int) Math.floor(Math.random() * filesArray.length);
        return filesArray[randomIndex];
    }


    private static File adapt(Object object) {
        File file;
        if (object instanceof File) {
            file = (File) object;
        } else if (object instanceof Path) {
            file = ((Path) object).toFile();
        } else {
            throw new RuntimeException("Object " + object + " is not a File or a Path");
        }
        return file;
    }
}
