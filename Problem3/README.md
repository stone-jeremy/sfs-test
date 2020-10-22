## Problem 3

### Prerequisites and development environment

This project is built using Java 11.

For IDE and build tool, I chose Eclipse and Gradle. My version of Eclipse is 4.17.0.

I've often used Maven but lean toward Gradle nowadays, and I'm also pretty fond of IntelliJ + Gradle.

### Building

To build the project, please run this command in the project directory:

    gradle build shadowJar

The project can also be built in Eclipse.

### Execution

To extract a line from a text file, run this command in the project directory:

    java -jar build/libs/Problem3-1.0-all.jar <path to text file> <line number to extract>

Notice the "-all" suffix in the JAR filename. This is produced by the "shadow" Gradle plugin, which creates a fat JAR including open-source libraries, to simplify deployment.

Please notice, too, that an index file is created in the same directory as the text file, with ".idx" appended to the text file's name. The index is not deleted and should be removed manually.

### Enhancements

#### Indexing pattern

Currently, the entire text file is indexed during the first execution.  One possible enhancement would be to change the way that the index (cache) is built, so that during a given run where line N is requested, no lines beyond N are indexed. Thus no unnecessary indexing would be done.

So if, on the first run, line M were requested, then lines 0 through M would be indexed. If, on a subsequent run, line N were requested, where M<N, then lines M+1 through N would be indexed. For any sequence of requests, the total execution time would be generally lower (except when one of the requests was for the last line, in which case the total execution time would be the same). There might also be advantages to distributing this execution time instead of doing all the indexing during the first execution.

#### Alternatives

I would also mention that for this kind of access pattern, when very large files or collections are concerned, other approaches might be suggested. One that comes to mind would be to use a Spark cluster, although we might tend to think of this more when some kind of search pattern needs to be applied.

