/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.jstone.sfs.Problem4;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Test cases for MaximalSubsequenceFinder.
 * <p>
 * These tests could probably be expanded with some more limit cases.
 * 
 * @author Jeremy Stone
 */
public class MaximalSubsequenceFinderTest {
	@Test
	public void testBruteForceAlgorithm() {
		testAlgorithm("bruteforce", List.of(), null);
		testAlgorithm("bruteforce", List.of(1, 2, 3), new Answer(6, 1, 3));
		testAlgorithm("bruteforce", List.of(1, 2, -20, 2, 4, 1, -10, 3, 6, -10, 2, 7), new Answer(9, 8, 9));
	}

	@Test
	public void testFastestAlgorithm() {
		testAlgorithm("fastest", List.of(), null);
		testAlgorithm("fastest", List.of(1, 2, 3), new Answer(6, 1, 3));
		testAlgorithm("fastest", List.of(1, 2, -20, 2, 4, 1, -10, 3, 6, -10, 2, 7), new Answer(9, 8, 9));
	}

	private void testAlgorithm(String algorithmName, List<Integer> list, Answer expectedAnswer) {
		Answer answer = null;
		switch (algorithmName) {
		case "bruteforce":
			answer = MaximalSubsequenceFinder.bruteForce(list);
			break;
		case "fastest":
		default:
			answer = MaximalSubsequenceFinder.fastest(list);
		}
		if (expectedAnswer == null) {
			assertTrue(String.format("%s algorithm should return null.", algorithmName), answer == null);
		} else {
			assertTrue(String.format("%s algorithm should return (sum=6, start=1, end=3).", algorithmName),
					(answer.getSum() == expectedAnswer.getSum()) && (answer.getStart() == expectedAnswer.getStart())
							&& (answer.getEnd() == expectedAnswer.getEnd()));
		}
	}
}
