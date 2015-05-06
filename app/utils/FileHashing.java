package utils;

import org.apache.commons.codec.digest.DigestUtils;
import play.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

public class FileHashing {

    /*
     * Copy from http://stackoverflow.com/questions/3010071/how-to-calculate-md5-checksum-on-directory-with-java-or-groovy
     */
    public static String calcMD5HashForDir(File dirToHash) {

        assert (dirToHash.isDirectory());
        Vector<FileInputStream> fileStreams = new Vector<>();

        Logger.info("Found files for hashing:");
        collectInputStreams(dirToHash, fileStreams);

        SequenceInputStream seqStream =
                new SequenceInputStream(fileStreams.elements());

        try {
            String md5Hash = DigestUtils.md5Hex(seqStream);
            seqStream.close();
            return md5Hash;
        }
        catch (IOException e) {
            throw new RuntimeException("Error reading files to hash in "
                    + dirToHash.getAbsolutePath(), e);
        }

    }

    private static void collectInputStreams(File dir,
                                            List<FileInputStream> foundStreams) {

        File[] fileList = dir.listFiles();
        Arrays.sort(fileList,               // Need in reproducible order
                new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return f1.getName().compareTo(f2.getName());
                    }
                });

        for (File f : fileList) {
            if (f.isDirectory()) {
                collectInputStreams(f, foundStreams);
            }
            else {
                try {
                    Logger.info("\t" + f.getAbsolutePath());
                    foundStreams.add(new FileInputStream(f));
                }
                catch (FileNotFoundException e) {
                    throw new AssertionError(e.getMessage()
                            + ": file should never not be found!");
                }
            }
        }

    }
}
