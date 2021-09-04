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
import java.util.ArrayList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for IO_Cache class.
 * 
 * @author Andy Turner
 * @version 1.0
 */
public class IO_CacheTest {

    public IO_CacheTest() {
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
     * Test creating a new file store adding to it and creating another file store 
     * based on the existing directory and adding to that.
     */
    @Test
    public void testFileStore1() throws IOException, Exception {
        //try {
            System.out.println("testFileStore1");
            // Set the path.
            Path p = Paths.get(System.getProperty("user.dir"), "data");
            // Set the name for the file store which should be unique and not used 
            // by any other tests which are executed in parallel.
            String name = "test2";
            Path p2 = Paths.get(p.toString(), name);
            // Delete any existing file store.
            if (true) {
                if (Files.exists(p2)) {
                    IO_Utilities.delete(p2, false);
                }
            }
            // Load a new file store.
            short range = 10;
            IO_Cache a = new IO_Cache(p, name, range);
            // Add 1001 directories.
            for (long l = 0; l < 1001; l++) {
                a.addDir();
            }
            // Details of data store
            System.out.println(a.toString());
            // Reload the existing file store.
            a = new IO_Cache(p2);
            // Details of data store
            System.out.println(a.toString());
            /**
             * If there are two file stores with the same baseDir then if one of
             * them changes the file store structure the other will have nextID,
             * lps, ranges and dirCounts that are inconsistent with what is
             * actually stored in the file system.
             */
            // Add another 1001 directories.
            for (long l = 0; l < 1001; l++) {
                a.addDir();
            }
            // Delete the file store.
            if (true) {
                if (Files.exists(p2)) {
                    IO_Utilities.delete(p2, false);
                }
            }
//        } catch (Exception ex) {
//            ex.printStackTrace(System.err);
//            Assertions.assertTrue(false);
//        }
            IO_Utilities.delete(p, true);
    }
    /**
     * Test of getLevels method, of class IO_Cache.
     */
    @Test
    public void testGetLevels_long_long() {
        System.out.println("getLevels");
        long n = 101L;
        long range = 10L;
        int expResult = 3;
        int result = IO_Cache.getLevels(n, range);
        Assertions.assertEquals(expResult, result);
        // Test 2
        n = 1001L;
        range = 10L;
        expResult = 4;
        result = IO_Cache.getLevels(n, range);
        Assertions.assertEquals(expResult, result);
        // Test 3
        n = 10001L;
        range = 10L;
        expResult = 5;
        result = IO_Cache.getLevels(n, range);
        Assertions.assertEquals(expResult, result);
        // Test 4
        n = 100001L;
        range = 10L;
        expResult = 6;
        result = IO_Cache.getLevels(n, range);
        Assertions.assertEquals(expResult, result);
        // Test 5
        n = 100001L;
        range = 100L;
        expResult = 3;
        result = IO_Cache.getLevels(n, range);
        Assertions.assertEquals(expResult, result);
        // Test 6
        n = 10000001L;
        range = 100L;
        expResult = 4;
        result = IO_Cache.getLevels(n, range);
        Assertions.assertEquals(expResult, result);
        // Test 6
        n = 12345678910L;
        range = 34L;
        expResult = 7;
        result = IO_Cache.getLevels(n, range);
        Assertions.assertEquals(expResult, result);
    }

    /**
     * Test of getRanges method, of class IO_Cache.
     */
    @Test
    public void testGetRanges_long_long() throws Exception {
        System.out.println("getRanges");
        long n = 1001L;
        long range = 10L;
        ArrayList<Long> expResult = new ArrayList<>();
        expResult.add(10000L);
        expResult.add(1000L);
        expResult.add(100L);
        expResult.add(10L);
        ArrayList<Long> result;
        try {
            result = IO_Cache.getRanges(n, range);
            Assertions.assertArrayEquals(expResult.toArray(), result.toArray());
        } catch (Exception ex) {
            int debug = 1;
        }
        // Test 2
        n = 10001L;
        range = 10L;
        expResult = new ArrayList<>();
        expResult.add(100000L);
        expResult.add(10000L);
        expResult.add(1000L);
        expResult.add(100L);
        expResult.add(10L);
        result = IO_Cache.getRanges(n, range);
        Assertions.assertArrayEquals(expResult.toArray(), result.toArray());
    }

    /**
     * Test of getDirIndexes method, of class IO_Cache.
     */
    @Test
    public void testGetDirIndexes_3args() throws Exception {
        System.out.println("getDirIndexes");
        long id = 10001L;
        long range = 10L;
        int levels = IO_Cache.getLevels(id, range);;
        ArrayList<Long> ranges = IO_Cache.getRanges(id, range);
        ArrayList<Integer> expResult  = new ArrayList<>();
        expResult.add(0);
        expResult.add(1);
        expResult.add(10);
        expResult.add(100);
        expResult.add(1000);
        ArrayList<Integer> result = IO_Cache.getDirIndexes(id, levels, ranges);
        Assertions.assertArrayEquals(expResult.toArray(), result.toArray());
    }
}
