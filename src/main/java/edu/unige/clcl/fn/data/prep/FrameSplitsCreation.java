package edu.unige.clcl.fn.data.prep;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Format:
 * FRAME_VALUE_0	FRAME_VALUE_1	#(frame + FEs)	Frame	LU	#target	target
 * #sentence
 * Indexes (for targets) are based on the tokenized splits, NOT on the sentence
 * splits
 *
 * @author Alex Kabbach
 */
public class FrameSplitsCreation {

	public static final String FRAME_VALUE_0 = "0";
	public static final String FRAME_VALUE_1 = "1.0";

	public static void main(String[] args) throws IOException {
		/*final String frameNetDataDir = args[0];
		final String trainSentenceSplits = args[1];
		final String testSentenceSplits = args[2];
		final String testSetDocsFile = args[3];*/

		final String frameNetDataDir = "/Users/AKB/Dropbox/FrameNetData/fndata-1.5";
		final String resourcesDir = "/Users/AKB/Dropbox/GitHub/semafor/data";
		final String testSentenceSplits = resourcesDir
				+ "/cv.test.sentences"; // TODO: use global variable?
		final String trainSentenceSplits = resourcesDir
				+ "/cv.train.sentences"; // TODO: use global variable?
		final String testSetDocsFile = "/Users/AKB/Dropbox/GitHub/semafor/resources/fn.fulltext.test.set.documents";

		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";

		Set<String> testSetDocNameSet = getTestSetDocNameSet(testSetDocsFile);
		//createFrameFESplits(lexUnitDir, fullTextDir, testSetDocNameSet, testSentenceSplits, trainSentenceSplits);
	}

	private static Set<String> getTestSetDocNameSet(String testSetDocsFile)
			throws IOException {
		Set<String> testSetDocNameSet = new HashSet<>();
		Files.lines(Paths.get(testSetDocsFile)).forEach(testSetDocNameSet::add);
		return testSetDocNameSet;
	}

	private static Map<String, Integer> getSentenceToNumberMap(
			String sentenceSplits) throws IOException {
		Map<String, Integer> map = new HashMap<>();
		int sentenceIterator = 0;
		List<String> sentences = Files.lines(Paths.get(sentenceSplits))
				.collect(Collectors.toList());
		for (String sentence : sentences) {
			map.put(sentence, sentenceIterator);
			sentenceIterator += 1;
		}
		return map;
	}

	private static Set<String> getTestFrameFESet() {
		Set<String> testFrameFESet = new HashSet<>();

		return testFrameFESet;
	}

	private static Set<String> getTrainFrameFESet() {
		Set<String> trainFrameFESet = new HashSet<>();

		return trainFrameFESet;
	}

	private static void createFrameSplits() {
		/*
		Map<String, Integer> testSentenceToNumberMap = getSentenceToNumberMap(testSentenceSplits);
		Set<String> testFrameSet = getTestFrameFESet();
		Map<String, Integer> trainSentenceToNumberMap = getSentenceToNumberMap(trainSentenceSplits);
		Set<String> trainFrameSet = getTrainFrameFESet();
		Files.write(Paths.get(outTestFile), testFrameSet, Charset.defaultCharset());
		Files.write(Paths.get(outTrainFile), trainFrameSet, Charset.defaultCharset());*/
	}
}
