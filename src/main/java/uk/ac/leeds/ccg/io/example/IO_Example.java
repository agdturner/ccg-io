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
import java.util.HashMap;
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
             * name for the objects stored is typical. It is not usual to store
             * small items individually in a cache like a Long, but this is done
             * in this example for demonstration purposes. Typically it is only
             * worth storing things with a size greater than 1MB. If individual
             * things are not worth storing, then one way forwards is to put
             * these in arrays or collections and store these larger objects in
             * the cache instead of individual items.
             */
            String name = "Long";
            IO_Cache c = new IO_Cache(p, name, range);
            /**
             * It is normal to also keep look-ups of the things we want to
             * store. (N.B. It is possible to store look-ups of look-ups to
             * avoid limitations of Java collections only being capable of
             * storing Integer.MAX_VALUE number of things. The look-up is a Map
             * used to normally retrieve the things and has keys that are Long
             * values. These keys match with the locations in the cache where
             * the thing may be stored. In this way the things can be cached and
             * set to null in the look-up to free up memory. So, the user can
             * check for the thing first in the look-up and then if this returns
             * null, look in the cache. (In some cases this may also fail as the
             * cache or part of it no longer exists, in which case whatever the
             * thing is may have to be recreated, or the user may have to cope
             * with not having that thing.) Memory management is one use case
             * scenario for the cache, but it might also generally be used to
             * store data in an indexed way.
             */
            HashMap<Long, Object> lu = new HashMap<>();
            // The number of things to store.
            long n = 10001L;
            //int n = 100001; // Quite a lot of things to list!
            //int n = 1000001; // A lot of things to list!]
            /**
             * In this example, all the things are stored immediately in both
             * the cache and the lookup.
             */
            for (long i = 0; i < n; i++) {
                // Add 
                c.add(i);

                lu.put(i, i);
            }
            /**
             * The following prints out the directory structure so it can be
             * inspected.
             */
            System.out.println(Files.list(p));
            /**
             * Let us set something in the look-up to null.
             */
            long pos = 1000L;
            lu.put(pos, null);
            /**
             * Let us retrieve the thing from the cache and put it back in the
             * look-up.
             */
            Object o = c.get(pos);
            lu.put(pos, c.get(pos));
            System.out.println("The Object stored at position=" + pos
                    + " has type=" + o.getClass().getName()
                    + " and value " + (Long) o);
            /**
             * Delete the cache to tidy up all the files. (N.B. Java is
             * typically quicker to do this than a Windows OS). (N.B. If this
             * example program stops before this or during this, then there will
             * left over files that will probably want to be deleted somehow.
             */
            IO_Utilities.delete(p, true);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}
