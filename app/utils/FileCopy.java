package utils;

import java.io.IOException;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.nio.file.attribute.BasicFileAttributes;
import static java.nio.file.FileVisitResult.*;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;

public class FileCopy {

    /**
     * Copy source file to target location. If {@code prompt} is true then
     * prompt user to overwrite target if it exists. The {@code preserve}
     * parameter determines if file attributes should be copied/preserved.
     */
    static private void copyFile(Path source, Path target, boolean prompt, boolean preserve, boolean overwrite) {
        CopyOption[] options = (preserve) ?
                new CopyOption[] { COPY_ATTRIBUTES, REPLACE_EXISTING } :
                new CopyOption[] { REPLACE_EXISTING };
        if (!prompt || Files.notExists(target) || overwrite) {
            try {
                Files.copy(source, target, options);
            } catch (IOException x) {
                System.err.format("Unable to copy: %s: %s%n", source, x);
            }
        }
    }


    /**
     * A {@code FileVisitor} that copies a file-tree ("cp -r")
     */
    static private class TreeCopier implements FileVisitor<Path> {
        private final Path source;
        private final Path target;
        private final boolean prompt;
        private final boolean preserve;
        private final boolean overwrite;

        TreeCopier(Path source, Path target, boolean prompt, boolean preserve, boolean overwrite) {
            this.source = source;
            this.target = target;
            this.prompt = prompt;
            this.preserve = preserve;
            this.overwrite = overwrite;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            // before visiting entries in a directory we copy the directory
            // (okay if directory already exists).
            CopyOption[] options = (preserve) ?
                    new CopyOption[]{COPY_ATTRIBUTES} : new CopyOption[0];

            Path newdir = target.resolve(source.relativize(dir));
            try {
                Files.copy(dir, newdir, options);
            } catch (FileAlreadyExistsException x) {
                // ignore
            } catch (IOException x) {
                System.err.format("Unable to create: %s: %s%n", newdir, x);
                return SKIP_SUBTREE;
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            copyFile(file, target.resolve(source.relativize(file)),
                    prompt, preserve, overwrite);
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
            // fix up modification time of directory when done
            if (exc == null && preserve) {
                Path newdir = target.resolve(source.relativize(dir));
                try {
                    FileTime time = Files.getLastModifiedTime(dir);
                    Files.setLastModifiedTime(newdir, time);
                } catch (IOException x) {
                    System.err.format("Unable to copy all attributes to: %s: %s%n", newdir, x);
                }
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            if (exc instanceof FileSystemLoopException) {
                System.err.println("cycle detected: " + file);
            } else {
                System.err.format("Unable to copy: %s: %s%n", file, exc);
            }
            return CONTINUE;
        }
    }

    public static void copyDirectory(Path source, Path dest, boolean overwrite) throws IOException {
        EnumSet<FileVisitOption> opts = EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        TreeCopier tc = new TreeCopier(source, dest, false, false, true);
        Files.walkFileTree(source, opts, Integer.MAX_VALUE, tc);
    }

    public static void copyDirectory(Path source, Path dest) throws IOException {
        copyDirectory(source, dest, true);
    }
}
