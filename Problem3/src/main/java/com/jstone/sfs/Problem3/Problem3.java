/*
 * Problem 3: Extract a line from a large file
 *
 * Author: Jeremy Stone
 * Last revision: 2020-10-21
 */

package com.jstone.sfs.Problem3;

/**
 * A utility that extracts one line from a text file, which may be very large.
 * <p>
 * On first execution, the text file is indexed in a compact form. The index contains byte offsets
 * of each line.
 * <p>
 * The utility is called from the command line with two parameters:
 * <p>
 * java [-cp &lt;classpath&gt;] com.jstone.sfs.Problem3.Problem3 &lt;input file path&gt; &lt;line
 * number&gt;
 * <ul>
 * <li>input file path: The path of the text file to read
 * <li>line number: The 0-based index of the line to extract
 * </ul>
 * <p>
 * The utility prints the requested line, or it writes to stderr if the line number is out of range.
 * <p>
 * Please see {@link TextFileLineAccess} for implementation details.
 * 
 * @author Jeremy Stone
 */
public class Problem3 {
	public static void main(String[] args) {
		// TODO Replace with a proper command-line parsing library like Airline or
		// Apache Commons CLI.

		// TODO Show the actual java command line, and remove the hard-coded
		// package/class.

		// Parse the command line.
		if (args.length < 2) {
			System.err.println("USAGE: java -jar <JAR file path> <input file path> <0-based line number>");
			return;
		}
		String inputPath = args[0];
		int lineNumber = Integer.parseInt(args[1]); // TODO We could check for correct format.

		String line = TextFileLineAccess.extractLineFromFile(inputPath, lineNumber);
		if (line == null) {
			System.err.println("ERROR: No such line");
		} else {
			System.out.println(line);
		}
	}
}
