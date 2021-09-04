/*
 * Copyright 2021 Andy Turner, University of Leeds.
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
package uk.ac.leeds.ccg.io.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import uk.ac.leeds.ccg.io.IO_Cache;
import uk.ac.leeds.ccg.io.IO_Utilities;

/**
 * Provides a simple example of how to use IO_Cache.
 *
 * @author Andy Turner
 */
public class IO_Example {

    public static void main(String[] args) {
        try {
            Path p = Paths.get(System.getProperty("user.dir"), "data", "test");
            // Create a new cache.
            /**
             * The range is the number of things to store in each directory.
             */
            short range = 100;
            /**
             * Give a name to the cache and the things stored in it. The class
             * name for the objects stored is typical.
             */
            String name = "Integer";
            IO_Cache c = new IO_Cache(p, name, range);
            // The number of things to store.
            int n = 10001;
            //int n = 100001; // Quite a lot of things to list!
            //int n = 1000001; // A lot of things to list!
            for (int i = 0; i < n; i++) {
                c.add(i);
            }
            // Print out the directory structure.
            System.out.println(Files.list(p));
            /**
             * Delete the cache. (If the program stops before this or during
             * this, then there will left over files that will probably want to
             * be deleted somehow.
             */
            IO_Utilities.delete(p, true);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}
