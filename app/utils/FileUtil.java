package utils;

import java.io.IOException;
import java.nio.file.Path;

public class FileUtil {
    public static void createPath(Path path) throws IOException {
        boolean success = path.toFile().mkdirs();
        if (!success) {
            throw new IOException("Unable to create directory " + path.toAbsolutePath() + ".");
        }
    }
}
