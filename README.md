# [ccg-io](https://github.com/agdturner/ccg-io)
A [modularised](https://en.wikipedia.org/wiki/Java_Platform_Module_System) lightweight java library offering some input/output utilities including an effective way to cache data.
The library provides 4 classes:
- [IO_Cache](https://github.com/agdturner/ccg-io/tree/main/src/main/java/uk/ac/leeds/ccg/io/IO_Cache.java) - for storing and organising data in a [file system](https://en.wikipedia.org/wiki/File_system) directory tree [cache](https://en.wikipedia.org/wiki/Cache_(computing)). ([see below for a more detailed description](#IO_Cache))
- [IO_Utilities](https://github.com/agdturner/ccg-io/tree/main/src/main/java/uk/ac/leeds/ccg/io/IO_Utilities.java) - containing static methods useful for input and output.
- [IO_Path](https://github.com/agdturner/ccg-io/tree/main/src/main/java/uk/ac/leeds/ccg/io/IO_Path.java) - a simple wrapper for java.nio.file.Path so that instances can be serialized.

## Latest release
Developed and tested on [Java Development Kit, version 15](https://openjdk.java.net/projects/jdk/15/).
```
<!-- https://mvnrepository.com/artifact/io.github.agdturner/ccg-io -->
<dependency>
    <groupId>io.github.agdturner</groupId>
    <artifactId>ccg-io</artifactId>
    <version>1.1.0</version>
</dependency>
```
[JAR](https://repo1.maven.org/maven2/io/github/agdturner/ccg-io/1.1.0/ccg-java-io-1.1.0.jar)

## Example use
[https://github.com/agdturner/ccg-io-example](A Mavenised repository with example use of ccg-io) - this initialises a cache and then goes through a sequence of CRUD (Create, Read, Update, Delete) operations to modify the cache). The [pom.xml](https://github.com/agdturner/ccg-io-example/tree/pom.xml) provides what is needed for Maven. The main method is in [CacheCRUD.java](https://github.com/agdturner/ccg-io-example/tree/main/src/main/java/uk/ac/leeds/ccg/example/io/CacheCRUD.java).

Whilst the Unit Tests provides some clues about how to use the code, the above linked example is more user-friendly as it provides an example set up for using the library properly (in a way consistent with modularisation and maven).

## Development history
### Origin
This code was originally developed for an academic research project over a decade before it was migrated here from [agdt-java-generic](https://github.com/agdturner/agdt-generic) in September 2021 as part of a major code reorganisation.

## LICENSE
- [APACHE LICENSE, VERSION 2.0](https://www.apache.org/licenses/LICENSE-2.0)

## Details

### [IO_Cache](https://github.com/agdturner/ccg-io/tree/main/src/main/java/uk/ac/leeds/ccg/io/IO_Cache.java)
For storing files in a [file system](https://en.wikipedia.org/wiki/File_system) directory tree in a well organised (easy to retrieve) and extendable (easy to store more) [cache](https://en.wikipedia.org/wiki/Cache_(computing)). Each file is stored in a leaf directory. Leaf directories are found at level 0 of the file store. The 1st leaf directory has the name 0, the 2nd leaf directory has the name 1, the nth leaf directory has the name n where n is a positive integer. A data store is comprised of a base directory in which there is a root directory. The root directory indicates how many files are stored in the data store using a range given in the directory name. The minimum of the range is 0 and the maximum is a positive integer number. These two numbers are separated by {@link #SEP} e.g. "0_99". The root directory will contain one or more subdirectories named in a similar style to the root directory e.g. "0_9". The maximum number will be less than or equal to that of the root directory. The range is a parameter that can be set when initialising a data store. It controls how many subdirectories there can be at each level, and ultimately this controls how many levels of directories there are in the file store which is all dependent on the number of files stored in total.

Files are stored in leaf directories. Each directory is given a standardised name such that it is easy to find and infer the path to the leaf directories.

If range was set to 10, there would be at most 10 subdirectories in each level of the file store.

Data stores are initialised with 3 levels and dynamically grow to store more files. For range = 10 the initial tree can be represented as follows:
```
Level
2        - 1           - root

0        - 0_9         - 0_99
```
For range = 10 and n = 100001 the tree can be represented as follows:
```
Level
6      - 5             - 4             - 3             - 2             - 1               - root

0      - 0_9           - 0_99          - 0_999         - 0_9999        - 0_99999        - 0_999999
1
2
3
4
5
6
7
8
9
10     - 10_19
11
12
...
19
20     - 20_29
...
...
99     - 90_99
100    - 100_109       - 100_199
...
...
...
999    - 990_999       - 900_999
1000   - 1000_1009     - 1000_1099     - 1000_1999
...
...
...
...
9999   - 9990_9999     - 9900_9999     - 9000_9999
10000  - 10000_10009   - 10000_10099   - 10000_10999   - 10000_19999
...
...
...
...
...
99999  - 99990_99999   - 99900_99999   - 99000_99999   - 90000_99999
100000 - 100000_100009 - 100000_100099 - 100000_100999 - 100000_109999 - 100000_199999
100001
```

Such data stores are useful for a wide range of purposes. For instance, they can be used to log informaton and cache data to help with memory management.

Although a cache can store many files, there are limits depending on the range value set. The theoretical limit is close to Long.MAX_VALUE / range. But there can be no more than Integer.MAX_VALUE levels. Perhaps a bigger restriction is the size of the storage element. One issue with this is that when the cache grows deeper, the paths to the data change and so care is needed not to read from or write to the cache when it is growing in this way. 

### [IO_Utilities](https://github.com/agdturner/ccg-io/tree/main/src/main/java/uk/ac/leeds/ccg/io/IO_Utilities.java)
General input/output utility class for reading from files, writing to files, copying and moving files.

## Acknowledgements and thanks
- The [University of Leeds](http://www.leeds.ac.uk) and externally funded research grants supported the development of this library.
