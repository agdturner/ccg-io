/*
 * Copyright 2019 Andy Turner, University of Leeds.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.leeds.ccg.io;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
//import static java.nio.file.StandardOpenOption.CREATE_NEW;
//import static java.nio.file.StandardOpenOption.DELETE_ON_CLOSE;
//import static java.nio.file.StandardOpenOption.DSYNC;
import static java.nio.file.StandardOpenOption.READ;
//import static java.nio.file.StandardOpenOption.SPARSE;
//import static java.nio.file.StandardOpenOption.SYNC;
//import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Contains convenient methods for primarily helping to read from and write to a
 * file system.
 */
public class IO_Utilities {

    /**
     * If dir is a directory this recursively goes through the contents and
     * creates an ArrayList of the paths of all files (not directories) to
     * return.
     *
     * @param dir The path to traverse.
     * @return An ArrayList of the paths of all files in dir and any
     * subdirectories.
     * @throws IOException If dir is not a directory and if otherwise such an
     * Exception is encountered.
     */
    public static List<Path> getFiles(Path dir) throws IOException {
        if (Files.isDirectory(dir)) {
            List<Path> r = new ArrayList<>();
            addFiles(dir, r);
            return r;
        } else {
            throw new IOException("Path " + dir.toString() + " is not a directory");
        }
    }

    /**
     * Recursively traverses a directory creating a set of File paths of files
     * (i.e. not directories).
     *
     * @param dir The path to add files to l from.
     * @param l The list to add to.
     * @throws java.io.IOException If encountered.
     */
    protected static void addFiles(Path dir, List<Path> l) throws IOException {
        try ( DirectoryStream<Path> s = Files.newDirectoryStream(dir)) {
            for (Path p : s) {
                if (Files.isDirectory(p)) {
                    addFiles(p, l);
                } else {
                    l.add(p);
                }
            }
        }
    }

    /**
     * Writes Object o to a file at f.
     *
     * @param o Object to be written.
     * @param f File to write to.
     * @throws IOException If encountered.
     */
    public static void writeObject(Object o, Path f) throws IOException {
        /**
         * The following commented out line is left here in case it is thought
         * in the future that it would be a good idea to add it. On the surface
         * adding it may look like a good idea so as to prevent IOExceptions
         * being thrown when the parent directory does not exist, but in
         * practice it likely slows things down, and the user should ensure the
         * directory exists before calling the method.
         */
        //Files.createDirectories(f.getParent());
        try ( ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(f, CREATE))) {
            oos.writeUnshared(o);
            oos.flush();
            oos.reset();
        }
    }

    /**
     * Read an Object and check the Type.
     *
     * @param <T> The type to cast into.
     * @param p Path to a file be read from.
     * @param type An instance of the class type to cast into.
     * @return Object read from the file at p.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If for some reason the Object
     * cannot otherwise be deserialized.
     */
    public static <T> T readObject(Path p, T type) throws IOException,
            ClassNotFoundException {
        return (T) readObject(p);
    }

    /**
     * Read an Object from a file at p.
     *
     * @param p Path to a file be read from.
     * @return Object read from the file at p.
     * @throws java.io.IOException If encountered.
     * @throws java.lang.ClassNotFoundException If for some reason the Object
     * cannot otherwise be deserialized.
     */
    public static Object readObject(Path p) throws IOException,
            ClassNotFoundException {
        try ( ObjectInputStream ois = new ObjectInputStream(
                Files.newInputStream(p, READ))) {
            return ois.readUnshared();
        }
    }

    /**
     * Writes Object o to a file at p and logs the name of the Object written
     * and the path.
     *
     * @param o Object to be written.
     * @param p The Path of the file to write to.
     * @param name String for reporting.
     * @throws java.io.IOException If encountered.
     */
    public static void writeObject(Object o, Path p, String name)
            throws IOException {
        writeObject(o, p);
    }

    /**
     * Copies a file from f to d.
     *
     * @param f A Path of a file to be copied.
     * @param d The Path of a directory to copy the file into.
     * @throws java.io.IOException If encountered.
     */
    protected static void copyFile(Path f, Path d) throws IOException {
        copyFile(f, d, f.getFileName().toString());
    }

    /**
     * Copies a file from f to d renaming it to fn in the process. If there is
     * no directory at d then this is created.
     *
     * This might be improved using Files.copy(f, target, REPLACE_EXISTING) and
     * similar...
     *
     * @param f A Path of a file to be copied.
     * @param d The Path of a directory to copy to.
     * @param fn The name for the file that will be created in d.
     * @throws java.io.IOException If encountered.
     */
    public static void copyFile(Path f, Path d,
            String fn) throws IOException {
        if (!Files.exists(f)) {
            throw new IOException("Path " + f + " is not to a file.");
        }
        if (!Files.exists(d)) {
            Files.createDirectories(d);
        }
        Path p = Paths.get(d.toString(), fn);
        if (!Files.exists(p)) {
            Files.createFile(p);
        }
        try ( BufferedInputStream bis = getBufferedInputStream(f);  BufferedOutputStream bos = getBufferedOutputStream(p)) {
            /**
             * bufferSize should be power of 2 (e.g. Math.pow(2, 12)), but
             * nothing too big.
             */
            int bufferSize = 2048;
            long length = Files.size(f);
            long nArrayReads = length / bufferSize;
            long nSingleReads = length - (nArrayReads * bufferSize);
            byte[] b = new byte[bufferSize];
            for (int i = 0; i < nArrayReads; i++) {
                bis.read(b);
                bos.write(b);
            }
            for (int i = 0; i < nSingleReads; i++) {
                bos.write(bis.read());
            }
            bos.flush();
        }
    }

    /**
     * @param f File.
     * @return BufferedInputStream
     * @throws java.io.FileNotFoundException If the file exists but is a
     * directory rather than a regular file, does not exist but cannot be
     * created, or cannot be opened for any other reason.
     */
    public static BufferedInputStream getBufferedInputStream(Path f)
            throws FileNotFoundException, IOException {
        return new BufferedInputStream(Files.newInputStream(f, READ));
    }

    /**
     * For getting a {@link BufferedOutputStream} to write to a file at
     * {@code f}.
     *
     * @param f The {@link Path} of the file to be written.
     * @return A {@link BufferedOutputStream} for writing to {@code f}.
     * @throws java.io.IOException If the file exists but is a directory rather
     * than a regular file, does not exist but cannot be created, or cannot be
     * opened for any other reason.
     */
    public static BufferedOutputStream getBufferedOutputStream(Path f)
            throws IOException {
        return new BufferedOutputStream(Files.newOutputStream(f, WRITE));
    }

    /**
     * For getting a {@link BufferedWriter}.
     *
     * @param f The {@link Path} for a file to be written to.
     * @param append if true then file is appended to otherwise file is
     * overwritten.
     * @return A {@link BufferedWriter} for writing to {@code f}.
     * @throws java.io.IOException If one is encountered and not otherwise
     * handled.
     */
    public static BufferedWriter getBufferedWriter(Path f, boolean append)
            throws IOException {
        return new BufferedWriter(getPrintWriter(f, append));
    }

    /**
     * @param f The {@link Path} for a file to be written.
     * @return An {@link ObjectInputStream} for reading from a file at {@code f}
     * @throws java.io.IOException If encountered and not otherwise handled.
     */
    public static ObjectInputStream getObjectInputStream(Path f)
            throws IOException {
        return new ObjectInputStream(getBufferedInputStream(f));
    }

    /**
     * @param f The {@link Path} of the file to write.
     * @return An {@link ObjectOutputStream} for writing to a file at {@code f}.
     * @throws java.io.IOException If encountered and not handled.
     */
    public static ObjectOutputStream getObjectOutputStream(Path f)
            throws IOException {
        return new ObjectOutputStream(getBufferedOutputStream(f));
    }

    /**
     * A class for recursively copying a directory.
     */
    private static class CopyDir extends SimpleFileVisitor<Path> {

        private final Path sourceDir;
        private final Path targetDir;

        public CopyDir(Path sourceDir, Path targetDir) {
            this.sourceDir = sourceDir;
            this.targetDir = targetDir;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
            try {
                Path targetFile = targetDir.resolve(sourceDir.relativize(file));
                Files.copy(file, targetFile);
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                BasicFileAttributes attributes) {
            try {
                Path newDir = targetDir.resolve(sourceDir.relativize(dir));
                Files.createDirectory(newDir);
            } catch (IOException ex) {
                System.err.println(ex);
            }
            return FileVisitResult.CONTINUE;
        }

    }

    private static void copyDirectory(Path dirToCopy, Path dirToCopyTo)
            throws IOException {
        Files.walkFileTree(dirToCopy, new CopyDir(dirToCopy, dirToCopyTo));
    }

    /**
     * @param fileOrDirToCopy File.
     * @param dirToCopyTo Directory.
     * @throws java.io.IOException If IOException encountered.
     */
    public static void copy(Path fileOrDirToCopy, Path dirToCopyTo)
            throws IOException {
        Files.createDirectories(dirToCopyTo);
        if (!Files.isDirectory(dirToCopyTo)) {
            throw new IOException("Expecting File " + dirToCopyTo
                    + "To be a directory in Generic_IO.copy(File,File)");
        }
        if (Files.isRegularFile(fileOrDirToCopy)) {
            copyFile(fileOrDirToCopy, dirToCopyTo);
        } else {
            copyDirectory(fileOrDirToCopy, dirToCopyTo);
        }
    }

    /**
     * Delete all files and directories in a directory.
     *
     * @param d The directory containing everything to delete.
     * @param log If true then deletions are logged.
     * @throws java.io.IOException If encountered and not logged. This will be
     * thrown if d does not denote a path to an existing directory.
     */
    public static void delete(Path d, boolean log) throws IOException {
        if (log) {
            try ( Stream<Path> walk = Files.walk(d)) {
                walk.sorted(Comparator.reverseOrder())
                        .peek(System.out::println) // Log deletions to std.out.
                        .forEach(p -> {
                            delete(p);
                        });
            }
        } else {
            try ( Stream<Path> walk = Files.walk(d)) {
                walk.sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            delete(p);
                        });
            }
        }
    }

    /**
     * Delete file if it exists.
     *
     * @param p The path to the file to delete if it exists.
     */
    public static void delete(Path p) {
        try {
            Files.deleteIfExists(p);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    /**
     * @param p The IO_Path of a file.
     * @return BufferedReader
     * @throws java.io.FileNotFoundException If the file exists but is a
     * directory rather than a regular file, does not exist but cannot be
     * created, or cannot be opened for any other reason.
     */
    public static BufferedReader getBufferedReader(IO_Path p)
            throws FileNotFoundException, IOException {
        return getBufferedReader(p.getPath());
    }

    /**
     * @param f The Path of a file.
     * @return BufferedReader
     * @throws java.io.FileNotFoundException If the path f does not end at a
     * file or a directory.
     * @throws java.io.IOException If the path f ends at a directory rather than
     * a regular file, or if the path f ends at a file, but this cannot be
     * opened for some reason.
     */
    public static BufferedReader getBufferedReader(Path f)
            throws FileNotFoundException, IOException {
        return getBufferedReader(f, "UTF-8");
    }

    /**
     * @param f File.
     * @param charsetName The name of a supported
     * {@link java.nio.charset.Charset charset} e.g. "UTF-8"
     * @return BufferedReader
     * @throws java.io.UnsupportedEncodingException If InputStreamReader cannot
     * be constructed from charsetName.
     * @throws java.io.IOException If encountered.
     */
    public static BufferedReader getBufferedReader(Path f, String charsetName)
            throws UnsupportedEncodingException, IOException {
        return new BufferedReader(new InputStreamReader(
                Files.newInputStream(f, READ), charsetName));
    }

    /**
     * Write {@code s} to a file at {@code p}.
     *
     * @param p The path to the file to write to.
     * @param s The String to write.
     * @throws IOException If encountered.
     */
    public static void write(Path p, String s) throws IOException {
        // Convert the string to a  byte array.
        byte data[] = s.getBytes();
        try ( OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(data, 0, data.length);
        }
    }

    /**
     * @param f The File to write to.
     * @param append If true an existing file will be appended otherwise it will
     * be overwritten.
     * @return PrintWriter
     * @throws IOException If the file exists but is a directory rather than a
     * regular file, does not exist but cannot be created, or cannot be opened
     * for any other reason.
     */
    public static PrintWriter getPrintWriter(Path f, boolean append)
            throws IOException {
        if (append) {
            return new PrintWriter(Files.newBufferedWriter(f, WRITE, CREATE,
                    APPEND));
        } else {
            return new PrintWriter(Files.newBufferedWriter(f, WRITE, CREATE));
        }
    }

    /**
     * @param dir The directory to list.
     * @return A list of files and directories in dir.
     * @throws IOException If encountered.
     */
    public static List<Path> getList(Path dir) throws IOException {
        try ( Stream<Path> s = Files.list(dir)) {
            return s.collect(Collectors.toList());
        }
    }

    /**
     * Method to calculate the length of the file path.
     *
     * @param f Path for which the normalised path length is returned.
     * @return normalised path length.
     * @throws java.io.IOException If encountered.
     */
    public static int getFilePathLength(Path f) throws IOException {
        String s = f.normalize().toString();
        return s.length();
    }

    /**
     * Returns a newly created File in the directory dir.
     *
     * @param dir The directory in which the newly created file is created
     * @return The File created.
     * @throws java.io.IOException If dir exists and is not a directory.
     */
    public static Path createNewFile(Path dir) throws IOException {
        return createNewFile(dir, "", "");
    }

    /**
     * Returns a newly created File (which may be a directory).
     *
     * @param dir The directory into which the new File is to be created.
     * @param prefix The first part of the filename.
     * @param suffix The last part of the filename.
     * @return The file of a newly created file in dir. The name of the file
     * will begin with prefix and end with suffix. If a file already exists with
     * a name which is just the prefix appended to the suffix, then a number is
     * inserted between these two parts of the filename. The first number tried
     * is 0, the number then increases by 1 each try.
     * @throws java.io.IOException If dir exists and is not a directory.
     */
    public static Path createNewFile(Path dir, String prefix, String suffix)
            throws IOException {
        if (Files.exists(dir)) {
            if (!Files.isDirectory(dir)) {
                throw new IOException("Attempting to create a file in " + dir
                        + " but this is not a directory.");
            }
        } else {
            Files.createDirectories(dir);
        }
        Path r = null;
        try {
            if (prefix == null) {
                prefix = "";
            }
            if (suffix == null) {
                suffix = "";
            }
            do {
                r = getNewFile(dir, prefix, suffix);
            } while (!Files.exists(Files.createFile(r)));
        } catch (IOException ioe0) {
            String methodName = IO_Utilities.class.getName()
                    + ".createNewFile(Path,String,String)";
            if (r != null) {
                System.err.println("Path " + r.toString() + " in " + methodName);
            } else {
                System.err.println("Path null in " + methodName);
            }
            ioe0.printStackTrace(System.err);
        }
        return r;
    }

    /**
     * This attempts to return a Path to a new file in the directory
     * {@code dir}. The file will have a name starting {@code prefix} and ending
     * {@code suffix}. If such a file already exists then a number n is inserted
     * between the {@code prefix} and {@code suffix}, where n is a positive
     * long. Firstly n = 0 is tried and if this file already exists then n = 1
     * is tried and so on until a unique file is returned.
     *
     * @param dir The directory in which to return the File.
     * @param prefix The first part of the filename.
     * @param suffix The last part of the filename.
     * @return A File for a file which is thought not to exist.
     */
    private static Path getNewFile(Path dir, String prefix, String suffix) {
        Path r;
        if (prefix.isEmpty() && suffix.isEmpty()) {
            long n = 0;
            do {
                r = Paths.get(dir.toString(), "" + n);
                n++;
            } while (Files.exists(r));
        } else {
            r = Paths.get(dir.toString(), prefix + suffix);
            if (Files.exists(r)) {
                long n = 0;
                do {
                    r = Paths.get(dir.toString(), prefix + n + suffix);
                    n++;
                } while (Files.exists(r));
            }
        }
        return r;
    }

}
