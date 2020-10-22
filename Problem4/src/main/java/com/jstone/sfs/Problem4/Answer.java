package com.jstone.sfs.Problem4;

import lombok.Getter;
import lombok.Setter;

/**
 * An object that represents an answer to the problem.
 * 
 * @author Jeremy Stone
 */
@Getter
@Setter
public class Answer {
	private long sum;
	private int start;
	private int end;

	/**
	 * @param sum   The sum of a contiguous subsequence
	 * @param start The index, in the larger sequence, of the subsequence's first element
	 * @param end   The index, in the larger sequence, of the last element in the subsequence
	 */
	public Answer(long sum, int start, int end) {
		this.sum = sum;
		this.start = start;
		this.end = end;
	}
}
