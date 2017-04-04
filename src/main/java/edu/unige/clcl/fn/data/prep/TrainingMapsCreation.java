package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.SerializedObjects;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alex Kabbach
 */
public class TrainingMapsCreation {

	private static final int FE_SPLITS_FRAME_INDEX = 3;
	private static final int FE_SPLITS_TARGET_INDEX = 5;
	private static final int FE_SPLITS_SENTENCE_INDEX = 7;
	private static final int FE_SPLITS_FE_START_INDEX = 8;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) throws IOException {
		final String feSplitsFile = args[0];
		final String posTaggedSplitsFile = args[1];
		final String luMapFile = args[2];
		final String feMapFile = args[3];

		TrainingMapsCreation tMapCreation = new TrainingMapsCreation();

		THashMap<String, THashSet<String>> newFEMap = tMapCreation
				.createFrameFEMap(feSplitsFile);
		THashMap<String, THashSet<String>> oldFEMap = (THashMap<String, THashSet<String>>) SerializedObjects
				.readSerializedObject(
						"/Users/AKB/Dropbox/GitHub/semafor/data/framenet.frame.element.map.old");
		THashMap<String, THashSet<String>> oldLUMap = (THashMap<String, THashSet<String>>) SerializedObjects
				.readSerializedObject(
						"/Users/AKB/Dropbox/GitHub/semafor/data/framenet.original.map.old");
		THashMap<String, THashSet<String>> newLUMap = tMapCreation
				.createFrameLUMap(feSplitsFile, posTaggedSplitsFile);
		tMapCreation.logger
				.info("Comparing keys (frames) between old and new Frame Element Maps");
		tMapCreation.compareKeys(oldFEMap, newFEMap);
		tMapCreation.logger
				.info("Comparing keys (frames) between old and new Lexical Unit Maps");
		tMapCreation.compareKeys(oldLUMap, newLUMap);
		tMapCreation.logger
				.info("Comparing values (FEs) between old and new Frame Element Maps");
		tMapCreation.compareValues(oldFEMap, newFEMap);
		tMapCreation.logger
				.info("Comparing values (LUs) between old and new Lexical Unit Maps");
		tMapCreation.compareValues(oldLUMap, newLUMap);
		SerializedObjects.writeSerializedObject(newFEMap, feMapFile);
		SerializedObjects.writeSerializedObject(newLUMap, luMapFile);
	}

	private List<String> splitBy(String line, String regex) {
		return Arrays.asList(line.split(regex));
	}

	private List<String> splitByWhiteSpace(String text) {
		return splitBy(text, "\\s+");
	}

	private List<String> splitByTab(String line) {
		return splitBy(line, "\\t");
	}

	private Map<String, Integer> getSentenceIndexMap(String sentenceSplits)
			throws IOException {
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

	/**
	 * Populates the given map object with frames (as keys) and sets of target words that evoke
	 * those frames in the given corresponding sentences (as values)
	 *
	 * @author dipanjan
	 */
	private void compareKeys(THashMap<String, THashSet<String>> originalMap,
			THashMap<String, THashSet<String>> newMap) {
		logger.info("New Map Size = " + newMap.size());
		logger.info("Old Map Size = " + originalMap.size());
		int counter = 0;
		for (Map.Entry<String, THashSet<String>> entry : newMap.entrySet()) {
			if (!originalMap.containsKey(entry.getKey())) {
				logger.info("Original map does not contain frame: " + entry
						.getKey());
				counter += 1;
			}
		}
		if (counter != 0) {
			logger.info("--------------------------------");
		}
		for (Map.Entry<String, THashSet<String>> entry : originalMap
				.entrySet()) {
			if (!newMap.containsKey(entry.getKey())) {
				logger.info(
						"New map does not contain frame: " + entry.getKey());
				counter += 1;
			}
		}
		logger.info("Number of differences in total = " + counter);
	}

	private void compareValues(THashMap<String, THashSet<String>> originalMap,
			THashMap<String, THashSet<String>> newMap) {
		int counter = 0;
		for (Map.Entry<String, THashSet<String>> entry : newMap.entrySet()) {
			if (originalMap.containsKey(entry.getKey())) {
				THashSet<String> originalValues = originalMap
						.get(entry.getKey());
				THashSet<String> newValues = entry.getValue();
				if (originalValues.size() != newValues.size()) {
					logger.info("Different values set size for frame: " + entry
							.getKey());
					logger.info(
							"	Original values set size = " + originalValues
									.size());
					logger.info(
							"	New values set size = " + newValues.size());
					for (String value : newValues) {
						if (!originalValues.contains(value)) {
							logger.info("		Value = " + value
									+ " is not in the original map");
							counter += 1;
						}
					}
					if (counter != 0) {
						logger.info("--------------------------------");
					}
					for (String value : originalValues) {
						if (!newValues.contains(value)) {
							logger.info("		Value = " + value
									+ " is not in the new map");
							counter += 1;
						}
					}
				}
			}
		}
		logger.info("New Map Size = " + newMap.size());
		logger.info("Old Map Size = " + originalMap.size());
		logger.info("Number of differences in total = " + counter);
	}

	private String getFrame(List<String> tokens) {
		return tokens.get(FE_SPLITS_FRAME_INDEX);
	}

	private THashSet<String> getFEs(List<String> tokens) {
		THashSet<String> fes = new THashSet<>();
		for (int i = FE_SPLITS_FE_START_INDEX; i < tokens.size(); i += 2) {
			fes.add(tokens.get(i));
		}
		return fes;
	}

	private THashMap<String, THashSet<String>> createFrameFEMap(
			String frameElementSplits) throws IOException {
		THashMap<String, THashSet<String>> frameFEMap = new THashMap<>();
		Files.lines(Paths.get(frameElementSplits)).forEach((line) -> {
			List<String> tokens = splitByTab(line);
			String frame = getFrame(tokens);
			THashSet<String> fes = getFEs(tokens);
			if (frameFEMap.containsKey(frame)) {
				frameFEMap.get(frame).addAll(fes);
			} else {
				frameFEMap.put(frame, fes);
			}
		});
		return frameFEMap;
	}

	private List<Integer> getTargetIndexes(List<String> feSplitTokens) {
		return splitBy(feSplitTokens.get(FE_SPLITS_TARGET_INDEX), "_").stream()
				.map(Integer::parseInt).collect(Collectors.toList());
	}

	private int getSentenceIndex(List<String> feSplitTokens) {
		return Integer.parseInt(feSplitTokens.get(FE_SPLITS_SENTENCE_INDEX));
	}

	private String getTaggedLU(List<String> taggedTokens,
			List<Integer> targetIndexes) {
		String taggedLU = "";
		for (Integer index : targetIndexes) {
			taggedLU += taggedTokens.get(index);
			taggedLU += " ";
		}
		return taggedLU.trim();
	}

	private THashMap<String, THashSet<String>> createFrameLUMap(
			String frameElementSplits, String posTaggedSplits)
			throws IOException {
		THashMap<String, THashSet<String>> frameLUMap = new THashMap<>();
		List<String> posTaggedSentences = Files
				.lines(Paths.get(posTaggedSplits)).collect(Collectors.toList());
		Files.lines(Paths.get(frameElementSplits)).forEach((line) -> {
			List<String> tokens = splitByTab(line);
			String frame = getFrame(tokens);
			List<Integer> targetIndexes = getTargetIndexes(tokens);
			int sentenceIndex = getSentenceIndex(tokens);
			List<String> taggedTokens = splitByWhiteSpace(
					posTaggedSentences.get(sentenceIndex));
			String taggedLU = getTaggedLU(taggedTokens, targetIndexes);
			if (frameLUMap.containsKey(frame)) {
				frameLUMap.get(frame).add(taggedLU);
			} else {
				THashSet<String> lus = new THashSet<>();
				lus.add(taggedLU);
				frameLUMap.put(frame, lus);
			}
		});
		return frameLUMap;
	}
}
