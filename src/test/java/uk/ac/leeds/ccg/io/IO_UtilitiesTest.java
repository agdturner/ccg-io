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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Tests for {@link IO_Utilities} class.
 * 
 * @author Andy Turner
 * @version 1.0
 */

public class IO_UtilitiesTest {

    public IO_UtilitiesTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of writeObject method, of class IO_Utilities.
     */
    @Test
    public void testWriteObject_Object_File() {
        try {
            Object o = "Hello World!";
            String prefix = "test";
            String suffix = ".dat";
            Path f = getTestFile(prefix, suffix);
            IO_Utilities.writeObject(o, f);
            // Make sure it is a new file
            f = getNewTestFile(prefix, suffix);
            IO_Utilities.writeObject(o, f);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public Path getNewTestFile(String prefix, String suffix) throws IOException {
        Path p = Paths.get(System.getProperty("user.dir"), "data");
        return Paths.get(p.toString(), prefix + suffix);
    }

    public Path getTestFile(String prefix, String suffix) throws IOException {
        Path p = Paths.get(System.getProperty("user.dir"), "data");
        return IO_Utilities.createNewFile(p, prefix, suffix);
    }

    /**
     * Test of getFilePathLength method, of class IO_Utilities.
     *
     * @throws java.io.IOException If encountered.
     */
    @Test
    public void testGetFilePathLength_File() throws IOException {
        Path p = Paths.get(System.getProperty("user.dir"), "data");
        int limit = 100;
        int result = IO_Utilities.getFilePathLength(p);
        System.out.println(result);
        Assertions.assertTrue(result < limit);
    }

    /**
     * Test of getNumericallyOrderedFiles method, of class IO_Utilities.
     */
    @Test
    public void testCreateNewFile() {
        try {
            Path p = Paths.get(System.getProperty("user.dir"), "data");
            String prefix = "test";
            String suffix = ".dat";
            Path f = IO_Utilities.createNewFile(p, prefix, suffix);
            Assertions.assertTrue(Files.exists(f));
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

}
