## Problem 4

### Prerequisites and development environment

This project is built using Java 11.

For IDE and build tool, I chose Eclipse and Gradle. My version of Eclipse is 4.17.0.

I've often used Maven but lean toward Gradle nowadays, and I'm also pretty fond of IntelliJ + Gradle.

### Building

To build the project, please run this command in the project directory:

    gradle build

The project can also be built in Eclipse.

For this project, I've included some basic JUnit tests, which can be run as follows:

    gradle test

### Execution

To extract a line from a text file, run this command in the project directory:

    java -jar build/libs/Problem4-1.0.jar <path to sequence file> [<algorithm>]

The sequence file should be a list of integers, one per line.

For the optional algorithm parameter, you can choose one of the following:

- bruteforce, a slow algorithm that checks every subsequence
- fastest, an algorithm that runs in O(N) time

The default is "fastest."

