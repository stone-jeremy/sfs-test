package com.jstone.sfs.Problem3;

import lombok.Getter;
import lombok.Setter;

/**
 * An object that specifies a byte range in a file.
 * 
 * @author Jeremy Stone
 */
@Getter
@Setter
public class FileSegmentOffsets {
	private long start;
	private int length;

	/**
	 * @param start The 0-based offset of the beginning of the byte range
	 * @param length The number of bytes in the range
	 */
	public FileSegmentOffsets(long start, int length) {
		this.start = start;
		this.length = length;
	}
}
