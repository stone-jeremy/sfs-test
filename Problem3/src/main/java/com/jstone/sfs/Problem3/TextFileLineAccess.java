package com.jstone.sfs.Problem3;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility for extracting a line from a large text file, with indexing to speed up further
 * extraction.
 * <p>
 * When extractLineFromFile is called, it checks for the existence of an index file, in the same
 * directory as the input file and with ".idx" affixed to the end of the filename. If this index
 * does not exist yet, it is created. Otherwise, it reads two integers from the index, representing
 * the line's offset and the offset of the next line (or EOF) in bytes. These offsets are then used
 * to perform an efficient read of only the selected byte range.
 * <p>
 * Assumptions:
 * <ul>
 * <li>The input file has at most 1 billion lines (as specified in the problem description).
 * <li>Each line has at most 1000 characters.
 * <li>The text encoding is UTF-8 (or a subset, like ASCII).
 * </ul>
 * <p>
 * These assumptions govern the choice of data types for indexing the file. The maximum file size is
 * 10^12 characters, and each character may be up to 4 bytes wide. We therefore need 6-byte unsigned
 * (or signed) long integers to represent byte offsets in the file. (Actually, we could pack things
 * even more tightly, with 42-bit unsigned integers, but this savings doesn't seem worth the
 * confusion it might introduce.)
 * 
 * @author Jeremy Stone
 */
public class TextFileLineAccess {
	private static final int SIZE_OF_LONG = 8;
	private static final int SIZE_OF_OFFSET = 6;
	private static final Charset CHARSET = StandardCharsets.UTF_8;

	// Byte order marks
	public static final String UTF8_BOM = "\uefbbbf";
	public static final String BOM = "\ufeff";

	/**
	 * Extract one line from a text file, using an index to speed up the process in case the file is
	 * very large.
	 * 
	 * @param path       The text file's path
	 * @param lineNumber The 0-based index of the line to extract
	 * @return The line, or null if lineNumber is out of range
	 */
	public static String extractLineFromFile(String path, int lineNumber) {
		String cachePathStr = String.format("%s.idx", path);
		Path cachePath = FileSystems.getDefault().getPath(cachePathStr);

		File inputFile = new File(path);

		FileSegmentOffsets lineOffsets = null;
		if (Files.exists(cachePath)) {
			lineOffsets = lookupLineOffsets(cachePathStr, lineNumber);
		} else {
			lineOffsets = cacheLineOffsets(inputFile, cachePathStr, lineNumber);
		}

		String line = null;
		if (lineOffsets != null) {
			line = readFileSegment(inputFile, lineOffsets.getStart(), lineOffsets.getLength());
			if (line != null) {
				line = StringUtils.strip(line, "\r\n");
			}
		}
		return line;
	}

	/**
	 * Look up the byte offset and length of a chosen line, in an index that has already been saved.
	 * 
	 * @param cachePath  The path of an index file
	 * @param lineNumber The line number to look up
	 * @return An object that specifies the byte offset and length of the line, or null if the line
	 *         number is out of range
	 */
	private static FileSegmentOffsets lookupLineOffsets(String cachePath, int lineNumber) {
		RandomAccessFile cache = null;
		FileSegmentOffsets result = null;
		try {
			cache = new RandomAccessFile(cachePath, "r");
			long start = 0;
			int length = 0;
			if (lineNumber < 0) {
				return null;
			} else if (lineNumber > 0) {
				int offsetLocation = (lineNumber - 1) * SIZE_OF_OFFSET;
				if (offsetLocation >= cache.length() - SIZE_OF_OFFSET) {
					return null;
				}
				cache.seek((lineNumber - 1) * SIZE_OF_OFFSET);
				start = readOffset(cache);
			}
			length = (int) (readOffset(cache) - start);
			result = new FileSegmentOffsets(start, length);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// Perhaps the file exists but is not readable.
			e.printStackTrace();
		} finally {
			try {
				if (cache != null) {
					cache.close();
				}
			} catch (IOException e) {
			}
		}
		if (result == null) {
			System.out.println("NULL result");
		}
		return result;
	}

	/**
	 * Index the byte offsets of all the line beginnings in a text file.
	 * <p>
	 * This is ordinarily called when the utility is run for the first time on a given text file. In
	 * this context, the client has requested a particular line, and we are only indexing the whole file
	 * in order to speed up later operations. So that it will not be necessary to re-read the index to
	 * find the offsets of the requested line, this method takes the line number as a third parameter,
	 * and in addition to the important side effect of creating the index, this method also returns
	 * offsets for that line.
	 * <p>
	 * This method supports CR, LF, and CRLF line breaks.
	 * 
	 * @param file                The file to index
	 * @param cachePath           The path at which an index file should be created
	 * @param lineNumberRequested A line number to locate during the indexing
	 * @return
	 */
	private static FileSegmentOffsets cacheLineOffsets(File file, String cachePath, int lineNumberRequested) {
		CountingInputStream inputStream = null;
		OutputStream cache = null;

		FileSegmentOffsets result = null;

		System.err.print(String.format("Writing index to %s... ", cachePath));

		try {
			inputStream = new CountingInputStream(new FileInputStream(file));
			cache = new FileOutputStream(cachePath);

			int lineNumber = 0;
			long offset = 0;
			long lineOffset = 0;
			long prevLineOffset = 0;
			boolean justReadCR = false;

			try {
				while (true) {
					char c = TextIO.readUTF8Char(inputStream);
					offset = inputStream.getCount();
					if ((c == '\r') || ((c == '\n') && !justReadCR)) {
						if (lineNumber == lineNumberRequested) {
							result = new FileSegmentOffsets(lineOffset, (int) (offset - lineOffset));
						}
						if (prevLineOffset > 0) {
							writeOffset(cache, prevLineOffset);
						}
						prevLineOffset = lineOffset;
						lineOffset = offset;
						lineNumber++;
						if (c == '\r') {
							justReadCR = true;
						} else {
							justReadCR = false;
						}
					} else if (c == '\n') {
						// We've encountered a CRLF line terminator. Advance to the offset of the next
						// line to skip the LF.
						lineOffset = offset;
						justReadCR = false;
					} else {
						justReadCR = false;
					}
				}
			} catch (EOFException e) {
				if (prevLineOffset > 0) {
					writeOffset(cache, prevLineOffset);
				}
				if (lineOffset > 0) {
					writeOffset(cache, lineOffset);
				}
				if (offset > 0) {
					writeOffset(cache, offset);
				}
				if (lineNumber == lineNumberRequested) {
					result = new FileSegmentOffsets(lineOffset, (int) (offset - lineOffset));
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (cache != null) {
					cache.close();
				}
			} catch (IOException e) {
			}
		}

		System.err.println("done.");
		return result;
	}

	/**
	 * Read a portion of a text file, specified by a byte offset and length, and return the result as a
	 * string.
	 * <p>
	 * Seeking is performed efficiently, using filesystem operations rather than reading the file to
	 * that point.
	 * 
	 * @param file     The file to read
	 * @param offset   The offset, in bytes, at which to start reading
	 * @param numBytes The number of bytes to read
	 * @return The content read, interpreted as a UTF-8-encoded string
	 */
	private static String readFileSegment(File file, long offset, int numBytes) {
		RandomAccessFile randomAccess = null;
		String fileSegment = null;
		try {
			// Seek to the position. We use RandomAccessFile for this, because it performs
			// low-level filesystem operations to seek instead of reading characters and
			// discarding them.
			randomAccess = new RandomAccessFile(file, "r");
			randomAccess.seek(offset);

			byte[] buffer = new byte[numBytes];
			TextIO.readBytes(randomAccess, buffer, 0, numBytes);
			fileSegment = new String(buffer, CHARSET);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (randomAccess != null) {
					randomAccess.close();
				}
			} catch (IOException e) {
			}
		}
		if ((fileSegment != null) && (offset == 0)) {
			fileSegment = trimBOM(fileSegment);
		}
		return fileSegment;
	}

	/**
	 * Remove the byte order mark that sometimes begins a text file.
	 * <p>
	 * The BOM is removed only if it occurs as the first character.
	 * 
	 * @param s The string to trim
	 * @return The trimmed string
	 */
	private static String trimBOM(String s) {
		if (s.startsWith(BOM) || s.startsWith(UTF8_BOM)) {
			s = s.substring(1);
		}
		return s;
	}

	private static long readOffset(RandomAccessFile cache) throws IOException {
		byte[] bytes = new byte[SIZE_OF_LONG];
		TextIO.readBytes(cache, bytes, SIZE_OF_LONG - SIZE_OF_OFFSET, SIZE_OF_OFFSET);
		return ByteBuffer.wrap(bytes).getLong();
	}

	/**
	 * Write a byte offset, using only 6 bytes to keep the file compact.
	 * 
	 * @param destination The file to write to
	 * @param x           The offset to write
	 * @throws IOException if an I/O error occurs
	 */
	private static void writeOffset(OutputStream cache, long x) throws IOException {
		byte[] bytes = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(x).array();
		cache.write(bytes, SIZE_OF_LONG - SIZE_OF_OFFSET, SIZE_OF_OFFSET);
	}
}
