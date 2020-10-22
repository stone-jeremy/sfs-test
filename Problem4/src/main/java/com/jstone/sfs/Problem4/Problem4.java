/*
 * Problem 4: Find contiguous subsequence with maximal sum
 *
 * Author: Jeremy Stone
 * Last revision: 2020-10-21
 */

package com.jstone.sfs.Problem4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test application for algorithms that maximize the sum of a contiguous subsequence of a list of
 * integers.
 * <p>
 * For problem and algorithm details, please see {@link MaximalSubsequenceFinder}.
 * <p>
 * The application is called from the command line with one or two parameters:
 * <p>
 * java [-cp &lt;classpath&gt;] com.jstone.sfs.Problem4.Problem4 &lt;input file path&gt;
 * [&lt;algorithm&gt;]
 * <ul>
 * <li>input file path: The path of the text file containing a list of integers. Each integer should
 * occupy one line.
 * <li>algorithm: The algorithm name to use
 * </ul>
 * <p>
 * Two algorithms are supported:
 * <ul>
 * <li>bruteforce
 * <li>fastest
 * </ul>
 * <p>
 * These are documented at {@link MaximalSubsequenceFinder}.
 * <p>
 * The application writes the results to stdout.
 *
 * @author Jeremy Stone
 */
public class Problem4 {
	public static void main(String[] args) {

		// Parse the command line.
		// TODO Replace with a proper command-line parsing library like Airline or Apache Commons CLI.
		// TODO Show the actual java command line, and remove the hard-coded package/class.
		if (args.length < 1) {
			System.err.println("USAGE: java com.jstone.sfs.Problem4 <input file path> [<algorithm>]");
			return;
		}
		String inputPath = args[0];
		String algorithm = "fastest";
		if (args.length >= 2) {
			algorithm = args[1];
		}

		// Read a list of integers from the input file.
		List<Integer> list = readInput(inputPath);
		if (list == null) {
			return;
		}

		// Apply the selected algorithm to find the subsequence with maximum sum.
		Answer answer = null;
		switch (algorithm) {
		case "bruteforce":
			answer = MaximalSubsequenceFinder.bruteForce(list);
			break;
		case "fastest":
		default:
			answer = MaximalSubsequenceFinder.fastest(list);
		}

		if (answer != null) {
			System.out.println("Maximum contiguous subsequence");
			System.out.println(String.format("Begins at index:            %d", answer.getStart()));
			System.out.println(String.format("Ends at index (inclusive):  %d", answer.getEnd()));
			System.out.println(String.format("Sum:                        %d", answer.getSum()));
			System.out.println("(Indices are 1-based.)");
		} else {
			System.err.println("ERROR: The search algorithm returned no result.");
		}
	}

	/**
	 * Read a list of integers from a text file.
	 * <p>
	 * Each line of the file should contain one integer. The parser uses Integer.parseInt() and does not
	 * check the input file format.
	 * 
	 * @param path The path of the input file
	 * @return The list of integers
	 */
	public static List<Integer> readInput(String path) {
		List<Integer> list = new ArrayList<Integer>();
		File file = new File(path);
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			while ((text = reader.readLine()) != null) {
				if (text.strip().length() > 0) {
					list.add(Integer.parseInt(text));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		return list;
	}
}
