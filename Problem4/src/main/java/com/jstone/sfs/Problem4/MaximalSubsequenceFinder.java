package com.jstone.sfs.Problem4;

import java.util.List;

/**
 * Search algorithms for maximizing the sum of a contiguous subsequence of a list of integers.
 * <p>
 * Given a finite sequence of N numbers, the problem is to find the contiguous subsequence with the
 * greatest sum. If more than one subsequence has the same maximal sum, then priority is given to
 * subsequences with the shortest length, and among these, to the first such subsequence.
 * <p>
 * Assumptions:
 * <ol>
 * <li>The numbers in the sequence are integers. This can easily be changed to support
 * floating-point numbers.
 * <li>The list length is at most Integer.MAX_VALUE. This assumption can be removed by using a
 * different collection type and replacing arrays with collections. (But in reality, in that case a
 * parallelized algorithm of some sort might be better, e.g. using Spark.)
 * <li>The sum will not exceed Long.MAX_VALUE. This assumption can be removed by using BigInteger
 * for sums.
 * </ol>
 * 
 * @author Jeremy Stone
 */

public class MaximalSubsequenceFinder {

	/**
	 * Find the contiguous subsequence with the greatest sum, using a fast algorithm.
	 * <p>
	 * This algorithm scans the list once and finishes in O(N) time.
	 * <p>
	 * As we scan the list, when we are at element i, we keep track of
	 * <ol>
	 * <li>The subsequence with the greatest sum terminating at element i ("sum")
	 * <li>The index of the element where this subsequence began ("start")
	 * </ol>
	 * <p>
	 * In moving from element i to element i+1, we either
	 * <ol>
	 * <li>Extend the current subsequence, if the preceding sum is positive; or
	 * <li>Start a new subsequence, if the preceding sum is negative or 0.
	 * </ol>
	 * <p>
	 * We also maintain a result, which is initialized to the subsequence consisting of the first
	 * element. Whenever the current sum exceeds the greatest sum found previously, we update the
	 * result.
	 * <p>
	 * Correctness can be seen easily by induction, but it rests on the observation that if the sum of a
	 * preceding subsequence is negative, then regardless of the current element's value, taking it as a
	 * 1-element subsequence yields a greater sum than appending it to the preceding elements. Since we
	 * begin each step with a subsequence that has the greatest sum of any of any subsequence
	 * terminating at the element i-1, we end with a subsequence that has the greatest sum of any
	 * subsequence terminating at element i.
	 * 
	 * @param list The list to search
	 * @return An answer specifying the subsequence and its sum
	 */

	public static Answer fastest(List<Integer> list) {
		if (list.size() < 1) {
			System.err.println("ERROR: 0-length sequence encountered. No nonempty subsequences exist.");
			return null;
		}

		int n = list.size();
		long sum = list.get(0);
		int start = 0;

		long maxSum = sum;
		int maxStart = 0;
		int maxEnd = 0;

		for (int i = 1; i < n; i++) {
			if (sum > 0) {
				sum += list.get(i);
			} else {
				sum = list.get(i);
				start = i;
			}

			// Update the result if the new sum is larger, or if sums are equal but the
			// current subsequence is shorter.
			if ((sum > maxSum) || ((sum == maxSum) && (i - start < maxEnd - maxStart))) {
				maxStart = start;
				maxEnd = i;
				maxSum = sum;
			}
		}

		// Return a result with 1-based sequence indices.
		return new Answer(maxSum, maxStart + 1, maxEnd + 1);
	}

	/**
	 * Find the contiguous subsequence with the greatest sum, using a slow brute-force algorithm.
	 * <p>
	 * This algorithm runs in O(N^3) time.
	 * <p>
	 * Here we examine every contiguous subsequence of the list to find the one with the greatest sum.
	 * This can be used to check the correctness of faster algorithms.
	 * 
	 * @param list The list to search
	 * @return An answer specifying the subsequence and its sum
	 */

	public static Answer bruteForce(List<Integer> list) {
		if (list.size() < 1) {
			System.err.println("ERROR: 0-length sequence encountered. No nonempty subsequences exist.");
			return null;
		}

		int n = list.size();

		long sum = list.get(0);

		long maxSum = sum;
		int maxStart = 0;
		int maxEnd = 0;

		for (int i = 0; i < n; i++) {
			for (int j = i; j < n; j++) {
				sum = 0;
				for (int k = i; k <= j; k++) {
					sum += list.get(k);
				}
				if ((sum > maxSum) || ((sum == maxSum) && (j - i < maxEnd - maxStart))) {
					maxSum = sum;
					maxStart = i;
					maxEnd = j;
				}
			}
		}

		// Return a result with 1-based sequence indices.
		return new Answer(maxSum, maxStart + 1, maxEnd + 1);
	}
}
