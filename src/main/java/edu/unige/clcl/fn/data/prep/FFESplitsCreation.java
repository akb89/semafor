package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import edu.unige.clcl.fn.data.prep.utils.FFEUtils;
import edu.unige.clcl.fn.data.prep.utils.SentenceToTokenizedIndexMapping;
import edu.unige.clcl.fn.data.prep.models.TokenIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Format (withFEInfo == false)
 * FRAME_VALUE_0	FRAME_VALUE_1	#(frame + FEs)	Frame	LU	#target	target
 * #sentence
 * Indexes (for targets) are based on the tokenized splits, NOT on the sentence
 * splits
 *
 * Format (withFEInfo == true)
 * FE_VALUE_0	FE_VALUE_1	#(frame + FEs)	Frame	LU	#target	target
 * #sentence	FE	feStart:feEnd
 * Indexes (for FE spans and targets) are based on the tokenized splits,
 * NOT on the sentence splits
 *
 * @author Alex Kabbach
 */
public class FFESplitsCreation {

	// Below values match what was used in (Kshirsagar et al., 2015)
	private static final String FRAME_VALUE_0 = "0";
	private static final String FRAME_VALUE_1 = "1.0";
	private static final String FE_VALUE_0 = "1";
	private static final String FE_VALUE_1 = "0.0";

	private static final Logger logger = LoggerFactory.getLogger(FFESplitsCreation.class);

	public static void main(String[] args) throws IOException {
		final String frameNetDataDir = args[0];
		final String testSentenceSplits = args[1];
		final String testTokenizedSentenceSplits = args[2];
		final String trainSentenceSplits = args[3];
		final String trainTokenizedSentenceSplits = args[4];
		final String testSetDocsFile = args[5];
		final String trainFESplits = args[6];
		final String testFESplits = args[7];
		final boolean withFEInfo = Boolean.parseBoolean(args[8]);
		final boolean withExemplars = Boolean.parseBoolean(args[9]);

		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";

		FFESplitsCreation ffeSplitsCreation = new FFESplitsCreation();
		if(withFEInfo){
			logger.info("Generating training and testing frame elements splits "
							+ "from sentences splits...");
		}else{
			logger.info("Generating training and testing frame splits "
							+ "from sentences splits...");
		}
		Set<String> testSetDocNameSet = FFEUtils
				.getTestSetDocNameSet(testSetDocsFile);
		if (withExemplars) {
			ffeSplitsCreation
					.createFFESplits(testSetDocNameSet, testSentenceSplits,
							testTokenizedSentenceSplits, trainSentenceSplits,
							trainTokenizedSentenceSplits, testFESplits,
							trainFESplits, withFEInfo, fullTextDir, lexUnitDir);
		} else {
			ffeSplitsCreation
					.createFFESplits(testSetDocNameSet, testSentenceSplits,
							testTokenizedSentenceSplits, trainSentenceSplits,
							trainTokenizedSentenceSplits, testFESplits,
							trainFESplits, withFEInfo, fullTextDir);
		}
		if(withFEInfo){
			logger.info("Done generating training and testing frame elements "
							+ "splits from sentences splits");
		}else{
			logger.info("Done generating training and testing frame "
							+ "splits from sentences splits");
		}
	}

	private void addElementsToMap(String frameName, String luName, String text,
			Element annotationSet, Map<String, Integer> sentenceIndexMap,
			List<String> tokenizedSentences, Map<Integer, Set<String>> feMap,
			boolean withFEInfo) {
		String targetWithStartCharIndex = FFEUtils
				.getTargetWithStartCharIndex(text, annotationSet);
		if (targetWithStartCharIndex.isEmpty()) {
			return;
		}
		String target = FFEUtils.splitBy(targetWithStartCharIndex, "#").get(0);
		int startChar = Integer.parseInt(
				FFEUtils.splitBy(targetWithStartCharIndex, "#").get(1));
		int sentenceIndex = sentenceIndexMap.get(text);
		String tokenizedText = tokenizedSentences.get(sentenceIndex);
		Map<TokenIndex, TokenIndex> tokenIndexMap = SentenceToTokenizedIndexMapping
				.getTokenIndexMap(text, tokenizedText);
		String targetIndex = FFEUtils
				.getTargetIndex(text, target, startChar, tokenIndexMap);
		if (targetIndex.isEmpty()) {
			logger.warn("Could not retrieve target index for "
					+ targetWithStartCharIndex + " in sentence: " + text);
			return;
		}
		String line;
		if(withFEInfo){
			int feFrameNumber = FFEUtils.getFrameFENumber(annotationSet);
			String fesChunk = FFEUtils
					.getFEsChunk(text, annotationSet, tokenIndexMap);
			line =
					FE_VALUE_0 + "\t"
							+ FE_VALUE_1 + "\t"
							+ feFrameNumber + "\t"
							+ frameName + "\t"
							+ luName + "\t"
							+ targetIndex + "\t"
							+ target + "\t"
							+ sentenceIndex + "\t"
							+ fesChunk;
		}else {
			int feFrameNumber = 1; // Count only the frame for frame splits
			line =
					FRAME_VALUE_0 + "\t" + FRAME_VALUE_1 + "\t" + feFrameNumber
							+ "\t" + frameName + "\t" + luName + "\t"
							+ targetIndex + "\t" + target + "\t"
							+ sentenceIndex;
		}
		if (feMap.containsKey(sentenceIndex)) {
			feMap.get(sentenceIndex).add(line.trim());
		} else {
			Set<String> testFESet = new HashSet<>();
			testFESet.add(line.trim());
			feMap.put(sentenceIndex, testFESet);
		}
	}

	private void addFullTextFEData(Map<Integer, Set<String>> trainFEMap,
			String fullTextDir, Set<String> testSentenceSet,
			Set<String> testSetDocNameSet,
			Map<String, Integer> trainSentenceIndexMap,
			List<String> trainTokenizedSentences, boolean withFEInfo) throws IOException {
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString()
					.endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0,
						filePath.getFileName().toString().indexOf(".xml"));
				if (!testSetDocNameSet.contains(docName)) {
					NodeList sentences = fullTextDoc.getDocumentElement()
							.getElementsByTagName("sentence");
					for (int i = 0; i < sentences.getLength(); i++) {
						Element sentence = (Element) sentences.item(i);
						if (FFEUtils.containsFrameNetAnnotation(sentence)) {
							String text = sentence.getElementsByTagName("text")
									.item(0).getTextContent()
									.replaceAll("\\s+$", "");
							if (!testSentenceSet.contains(text)) {
								NodeList annoSets = sentence
										.getElementsByTagName("annotationSet");
								for (int j = 0; j < annoSets.getLength(); j++) {
									Element annotationSet = (Element) annoSets
											.item(j);
									if (!annotationSet.getAttribute("luName")
											.isEmpty()) {
										String frameName = annotationSet
												.getAttribute("frameName");
										String luName = annotationSet
												.getAttribute("luName");
										addElementsToMap(frameName, luName,
												text, annotationSet,
												trainSentenceIndexMap,
												trainTokenizedSentences,
												trainFEMap, withFEInfo);
									}
								}
							}
						}
					}
				}
			}
		});
	}

	private void addExemplarFEData(Map<Integer, Set<String>> trainFEMap,
			String lexUnitDir, Set<String> testSentenceSet,
			Map<String, Integer> trainSentenceIndexMap,
			List<String> trainTokenizedSentences, boolean withFEInfo) throws IOException {
		Files.walk(Paths.get(lexUnitDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString()
					.endsWith(".xml")) {
				Element lexUnitElement = XmlUtils
						.parseXmlFile(filePath.toString(), false)
						.getDocumentElement();
				String luName = lexUnitElement.getAttribute("name");
				String frameName = lexUnitElement.getAttribute("frame");
				NodeList subCorpora = lexUnitElement
						.getElementsByTagName("subCorpus");
				for (int i = 0; i < subCorpora.getLength(); i++) {
					Element subCorpus = (Element) subCorpora.item(i);
					NodeList sentences = subCorpus
							.getElementsByTagName("sentence");
					for (int j = 0; j < sentences.getLength(); j++) {
						Element sentence = (Element) sentences.item(j);
						if (FFEUtils.containsFrameNetAnnotation(sentence)) {
							String text = sentence.getElementsByTagName("text")
									.item(0).getTextContent()
									.replaceAll("\\s+$", "");
							if (!testSentenceSet.contains(text)) {
								NodeList annoSets = sentence
										.getElementsByTagName("annotationSet");
								for (int k = 0; k < annoSets.getLength(); k++) {
									Element annotationSet = (Element) annoSets
											.item(k);
									addElementsToMap(frameName, luName, text,
											annotationSet,
											trainSentenceIndexMap,
											trainTokenizedSentences,
											trainFEMap, withFEInfo);
								}
							}
						}
					}
				}
			}
		});
	}

	private Map<Integer, Set<String>> getTestFEMap(String fullTextDir,
			Set<String> testSetDocNameSet,
			Map<String, Integer> testSentenceIndexMap,
			List<String> testTokenizedSentences, boolean withFEInfo)
			throws IOException {
		Map<Integer, Set<String>> testFEMap = new TreeMap<>();
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString()
					.endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0,
						filePath.getFileName().toString().indexOf(".xml"));
				if (testSetDocNameSet.contains(docName)) {
					NodeList sentences = fullTextDoc.getDocumentElement()
							.getElementsByTagName("sentence");
					for (int i = 0; i < sentences.getLength(); i++) {
						Element sentence = (Element) sentences.item(i);
						if (FFEUtils.containsFrameNetAnnotation(sentence)) {
							String text = sentence.getElementsByTagName("text")
									.item(0).getTextContent()
									.replaceAll("\\s+$", "");
							NodeList annoSets = sentence
									.getElementsByTagName("annotationSet");
							for (int j = 0; j < annoSets.getLength(); j++) {
								Element annotationSet = (Element) annoSets
										.item(j);
								if (!annotationSet.getAttribute("luName")
										.isEmpty()) {
									String frameName = annotationSet
											.getAttribute("frameName");
									String luName = annotationSet
											.getAttribute("luName");
									addElementsToMap(frameName, luName, text,
											annotationSet, testSentenceIndexMap,
											testTokenizedSentences, testFEMap,
											withFEInfo);
								}
							}
						}
					}
				}
			}
		});
		return testFEMap;
	}

	private Map<Integer, Set<String>> getTrainFEMap(Set<String> testSentenceSet,
			Set<String> testSetDocNameSet,
			Map<String, Integer> trainSentenceIndexMap,
			List<String> trainTokenizedSentences, boolean withFEInfo, String... inputDir)
			throws IOException {
		Map<Integer, Set<String>> trainFEMap = new TreeMap<>();
		String fullTextDir = inputDir[0];
		addFullTextFEData(trainFEMap, fullTextDir, testSentenceSet,
				testSetDocNameSet, trainSentenceIndexMap,
				trainTokenizedSentences, withFEInfo);
		if (inputDir.length > 1) {
			String lexUnitDir = inputDir[1];
			addExemplarFEData(trainFEMap, lexUnitDir, testSentenceSet,
					trainSentenceIndexMap, trainTokenizedSentences, withFEInfo);
		}
		return trainFEMap;
	}

	private void createFFESplits(Set<String> testSetDocNameSet,
			String testSentenceSplits, String testTokenizedSentenceSplits,
			String trainSentenceSplits, String trainTokenizedSentenceSplits,
			String outTestFile, String outTrainFile, boolean withFEInfo,
			String... inputDir)
			throws IOException {
		String fullTextDir = inputDir[0];
		Map<String, Integer> testSentenceIndexMap = FFEUtils
				.getSentenceIndexMap(testSentenceSplits);
		List<String> testTokenizedSentences = Files
				.lines(Paths.get(testTokenizedSentenceSplits))
				.collect(Collectors.toList());
		Map<Integer, Set<String>> testFEMap = getTestFEMap(fullTextDir,
				testSetDocNameSet, testSentenceIndexMap,
				testTokenizedSentences, withFEInfo);
		List<String> testFEList = testFEMap.values().stream()
				.flatMap(Set::stream).collect(Collectors.toList());
		Map<String, Integer> trainSentenceIndexMap = FFEUtils
				.getSentenceIndexMap(trainSentenceSplits);
		List<String> trainTokenizedSentences = Files
				.lines(Paths.get(trainTokenizedSentenceSplits))
				.collect(Collectors.toList());
		Set<String> testSentenceSet = Files.lines(Paths.get(testSentenceSplits))
				.collect(Collectors.toSet());
		Map<Integer, Set<String>> trainFEMap;
		if (inputDir.length > 1) {
			String lexUnitDir = inputDir[1];
			trainFEMap = getTrainFEMap(testSentenceSet, testSetDocNameSet,
					trainSentenceIndexMap, trainTokenizedSentences, withFEInfo,
					fullTextDir, lexUnitDir);
		} else {
			trainFEMap = getTrainFEMap(testSentenceSet, testSetDocNameSet,
					trainSentenceIndexMap, trainTokenizedSentences, withFEInfo,
					fullTextDir);
		}
		List<String> trainFEList = trainFEMap.values().stream()
				.flatMap(Set::stream).collect(Collectors.toList());
		Files.write(Paths.get(outTestFile), testFEList, StandardCharsets.UTF_8);
		Files.write(Paths.get(outTrainFile), trainFEList,
				StandardCharsets.UTF_8);
	}
}
