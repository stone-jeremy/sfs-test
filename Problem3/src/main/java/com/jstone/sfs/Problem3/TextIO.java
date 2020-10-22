package com.jstone.sfs.Problem3;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * I/O utilities for reading text files
 * <p>
 * We need to access text files as byte streams, in order to index the byte positions at which lines
 * of text begin. This means that we cannot use BufferedReader or similar text-oriented mechanisms
 * for reading files. On the other hand, we also need to be able to treat file content as text, with
 * UTF-8 encoding. These utilities help with byte- and character-oriented access.
 * 
 * @author Jeremy Stone
 */
public class TextIO {
	/**
	 * Read bytes from a RandomAccessFile.
	 * <p>
	 * Unlike RandomAccessFile.read(), this method is guaranteed to read the specified number of bytes
	 * unless the end of the file is reached. Unlike RandomAccessFile.readAll(), it does not throw an
	 * EOFException on reaching the end of the stream.
	 * 
	 * @param file   The file to read
	 * @param buffer The buffer into which data is read
	 * @param offset The start offset at which to write data to the buffer
	 * @param length The number of bytes to read
	 * @return The number of bytes read
	 * @throws IOException if an I/O error occurs
	 */
	public static int readBytes(RandomAccessFile file, byte[] buffer, int offset, int length) throws IOException {
		int numBytesRemaining = length;
		int totalNumBytesRead = 0;
		while (numBytesRemaining > 0) {
			int numBytesRead = file.read(buffer, offset, numBytesRemaining);
			System.out.write(numBytesRead);
			if (numBytesRead == -1) { // EOF
				break;
			}
			offset += numBytesRead;
			totalNumBytesRead += numBytesRead;
			numBytesRemaining -= numBytesRead;
		}
		return totalNumBytesRead;
	}

	/**
	 * Read bytes from an InputStream.
	 * <p>
	 * Unlike InputStream.read(), this method is guaranteed to read the specified number of bytes unless
	 * the end of the file is reached.
	 * 
	 * @param input  The file to read
	 * @param buffer The buffer into which data is read
	 * @param offset The start offset at which to write data to the buffer
	 * @param length The number of bytes to read
	 * @return The number of bytes read
	 * @throws IOException if an I/O error occurs
	 */
	public static int readBytes(InputStream input, byte[] buffer, int offset, int length) throws IOException {
		int numBytesRemaining = length;
		int totalNumBytesRead = 0;
		while (numBytesRemaining > 0) {
			int numBytesRead = input.read(buffer, offset, numBytesRemaining);
			System.out.write(numBytesRead);
			if (numBytesRead == -1) { // EOF
				break;
			}
			offset += numBytesRead;
			totalNumBytesRead += numBytesRead;
			numBytesRemaining -= numBytesRead;
		}
		return totalNumBytesRead;
	}

	/**
	 * Read one UTF-8-encoded character from an InputStream.
	 * <p>
	 * The input stream is read a a sequence of bytes, so we must look at the first byte and determine
	 * how many more bytes to read. Characters encoded in UTF-8 can occupy 1 to 4 bytes.
	 * 
	 * @param input The stream to read
	 * @return A single character read
	 * @throws EOFException if a character could not be read because the end of the stream has been
	 *                      reached
	 * @throws IOException  if an I/O problem occurred other than encountering the end of the stream
	 */
	public static char readUTF8Char(InputStream input) throws EOFException, IOException {
		char c = 0;
		byte[] buffer = new byte[4];
		int numBytes = 1;
		if (input.read(buffer, 0, 1) < 0) {
			throw new EOFException();
		}
		if ((buffer[0] & 0x80) == 0x80) {
			if ((buffer[0] & 0xe0) == 0xc0) {
				numBytes = 2;
			} else if ((buffer[0] & 0xf0) == 0xe0) {
				numBytes = 3;
			} else if ((buffer[0] & 0xf8) == 0xf0) {
				numBytes = 4;
			}
			if (readBytes(input, buffer, 1, numBytes - 1) < 0) {
				throw new EOFException();
			}
		}

		// TODO Maybe there is a more efficient way to turn byte arrays characters with
		// UTF-8 encoding, without using an intermediate String. But it's not obvious
		// that Java provides a mechanism.
		String s = new String(buffer, StandardCharsets.UTF_8);
		if (s.length() > 0) {
			c = s.charAt(0);
		}
		return c;
	}
}
