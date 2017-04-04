package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import edu.unige.clcl.fn.data.prep.utils.SentenceToTokenizedIndexMapping;
import edu.unige.clcl.fn.data.prep.models.TokenIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Format:
 * FE_VALUE_0	FE_VALUE_1	#(frame + FEs)	Frame	LU	#target	target	#sentence	FE	feStart:feEnd
 * Indexes (for FE spans and targets) are based on the tokenized splits, NOT on the sentence splits
 * @author Alex Kabbach
 */
public class FESplitsCreation {

	private static final String FE_VALUE_0 = "1";
	private static final String FE_VALUE_1 = "0.0";

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) throws IOException {
		final String frameNetDataDir = args[0];
		final String testSentenceSplits = args[1];
		final String testTokenizedSentenceSplits = args[2];
		final String trainSentenceSplits = args[3];
		final String trainTokenizedSentenceSplits = args[4];
		final String testSetDocsFile = args[5];
		final String trainFESplits = args[6];
		final String testFESplits = args[7];

		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";

		FESplitsCreation feSplitsCreation = new FESplitsCreation();
		feSplitsCreation.logger.info("Generating training and testing frame elements splits from sentences splits...");

		Set<String> testSetDocNameSet = feSplitsCreation.getTestSetDocNameSet(testSetDocsFile);
		feSplitsCreation.createFESplits(lexUnitDir, fullTextDir, testSetDocNameSet,
				testSentenceSplits, testTokenizedSentenceSplits,
				trainSentenceSplits, trainTokenizedSentenceSplits, testFESplits, trainFESplits);

		feSplitsCreation.logger.info("Done generating training and testing frame elements splits from sentences splits");
	}

	private void testTokenIndex(String test, int start, int end){
		toTokenIndex(test, start, end);
	}

	private void test(String sentenceSplits, String tokenizedSentenceSplits)
			throws IOException {
		String text = "As husband of Louis VII's sister , Constance , Raymond of Toulouse could count on help from his brother-in-law .";
		String targetWithIndex = "count#73";
		Map<String, Integer> sentenceMap = getSentenceIndexMap(sentenceSplits);
		List<String> tokenizedSentences =
				Files.lines(Paths.get(tokenizedSentenceSplits))
						.collect(Collectors.toList());
		Map<TokenIndex, TokenIndex> indexMap =
				SentenceToTokenizedIndexMapping
						.getTokenIndexMap(text,
								tokenizedSentences.get(sentenceMap.get(text)));
		String targetIndex = getTargetIndex(text, targetWithIndex, indexMap);
		System.out.println(targetIndex);
		if(targetIndex.isEmpty()){
			System.out.println("isEmpty");
		}
	}

	private List<String> splitBy(String text, String regex){
		return Arrays.asList(text.split(regex));
	}

	private List<String> splitByWhiteSpace(String text){
		return splitBy(text, "\\s+");
	}

	private Set<String> getTestSetDocNameSet(String testSetDocsFile)
			throws IOException {
		return Files.lines(Paths.get(testSetDocsFile))
				.collect(Collectors.toSet());
	}

	private boolean containsFrameNetAnnotation(Element sentenceElement){
		NodeList annotationSets =
				sentenceElement.getElementsByTagName("annotationSet");
		for(int i=0; i<annotationSets.getLength(); i++){
			NodeList layers = annotationSets.item(i).getChildNodes();
			for(int j=0; j<layers.getLength(); j++){
				if(!layers.item(j).getNodeName().equals("#text")){
					Element layer = (Element)layers.item(j);
					if(!layer.getAttribute("name").equals("FE")){
						return true;
					}
				}
			}
		}
		return false;
	}

	private Map<String, Integer> getSentenceIndexMap(String sentenceSplits)
			throws IOException {
		Map<String, Integer> map = new HashMap<>();
		int sentenceIterator = 0;
		List<String> sentences = Files.lines(Paths.get(sentenceSplits)).collect(
				Collectors.toList());
		for (String sentence : sentences) {
			map.put(sentence, sentenceIterator);
			sentenceIterator += 1;
		}
		return map;
	}

	private int getFrameFENumber(Element annotationSet){
		int frameFENumber = 1; // At least one for the frame
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for(int i=0; i< layers.getLength(); i++){
			Element layer = (Element)layers.item(i);
			if(layer.getAttribute("name").equals("FE")){
				NodeList labels = layer.getElementsByTagName("label");
				for(int j=0; j<labels.getLength(); j++){
					Element label = (Element)labels.item(j);
					// Only count FEs visible in the sentence (no CNI, DNI etc.)
					if(label.hasAttribute("start")
							&& label.hasAttribute("end")){
						frameFENumber += 1;
					}
				}
			}
		}
		return frameFENumber;
	}

	// TODO: test this and maybe optimize a bit
	private TokenIndex getTokenizedTokenIndex(
			Map<TokenIndex, TokenIndex> tokenIndexMap,
			int startTokenIndex, int endTokenIndex){
		int startIndex = -1;
		int endIndex = -1;
		for(TokenIndex key: tokenIndexMap.keySet()){
			if(key.getStart() <= startTokenIndex
					&& startTokenIndex <= key.getEnd()){
				startIndex = tokenIndexMap.get(key).getStart();
			}
			if(key.getStart() <= endTokenIndex
					&& endTokenIndex <= key.getEnd()){
				endIndex = tokenIndexMap.get(key).getEnd();
			}
		}
		if(startIndex == -1 || endIndex == -1){
			return null;
		}
		return new TokenIndex(startIndex, endIndex);
	}

	private String getTargetIndex(String text, String targetWithIndex,
			Map<TokenIndex, TokenIndex> tokenIndexMap){
		int targetStartChar = Integer.parseInt(
				splitBy(targetWithIndex, "#").get(1));
		int targetEndChar = targetStartChar +
				splitBy(targetWithIndex, "#").get(0).length() - 1;
		TokenIndex tokenIndex =
				toTokenIndex(text, targetStartChar, targetEndChar);
		if(tokenIndex == null){
			logger.warn("Could not find TokenIndex ("
					+ tokenIndex.getStart() +", "
					+ tokenIndex.getEnd() + ") for target: "
					+ targetWithIndex + " in sentence: " + text);
			return "";
		}
		TokenIndex tokenizedIndex = getTokenizedTokenIndex(
				tokenIndexMap, tokenIndex.getStart(), tokenIndex.getEnd());
		if(tokenizedIndex == null){
			logger.warn("Could not find tokenized TokenIndex ("
					+ tokenIndex.getStart() +", "
					+ tokenIndex.getEnd() + ") for target: "
					+ targetWithIndex + " in sentence: " + text);
			return "";
		}
		if(tokenizedIndex.getStart() == tokenizedIndex.getEnd()){
			return String.valueOf(tokenizedIndex.getStart());
		}else{
			return tokenizedIndex.getStart()
					+ "_" + tokenizedIndex.getEnd();
		}
	}

	private TokenIndex toTokenIndex(String text, int startChar, int endChar){
		List<String> tokens = splitByWhiteSpace(text);
		List<String> sequence = splitByWhiteSpace(
				text.substring(startChar, endChar+1));
		int charIndex = 0;
		for(int i = 0; i<tokens.size(); i++){
			if(tokens.get(i).isEmpty()){
				charIndex += 1;
			}else{
				while(tokens.get(i).charAt(0) != text.charAt(charIndex)){
					charIndex += 1;
				}
			}
			if(tokens.get(i).equals(sequence.get(0))
					&& charIndex == startChar){
				int startTokenIndex = i;
				int endTokenIndex = i;
				if(sequence.size() > 1){
					endTokenIndex = startTokenIndex + sequence.size() - 1;
				}
				return new TokenIndex(startTokenIndex, endTokenIndex);
			}
			charIndex += tokens.get(i).length();
		}
		return null;
	}

	private String getFEsChunk(String text, Element annotationSet,
			Map<TokenIndex, TokenIndex> tokenIndexMap){
		String feChunks = "";
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for(int i=0; i< layers.getLength(); i++){
			Element layer = (Element)layers.item(i);
			if(layer.getAttribute("name").equals("FE")){
				NodeList labels = layer.getElementsByTagName("label");
				for(int j=0; j<labels.getLength(); j++){
					Element label = (Element)labels.item(j);
					if(label.hasAttribute("start")
							&& label.hasAttribute("end")){
						int start = Integer.parseInt(
								label.getAttribute("start"));
						int end = Integer.parseInt(
								label.getAttribute("end"));
						TokenIndex tokenIndex = toTokenIndex(text, start, end);
						if(tokenIndex == null){
							logger.warn("Could not find TokenIndex ("
									+ start + ", "
									+ end + ") for FE in sentence: " + text);
							return "";
						}
						feChunks += label.getAttribute("name");
						feChunks += "\t";
						TokenIndex tokenizedIndex =
								getTokenizedTokenIndex(tokenIndexMap,
										tokenIndex.getStart(),
										tokenIndex.getEnd());
						if(tokenizedIndex == null){
							logger.warn("Could not find tokenized TokenIndex ("
									+ tokenIndex.getStart()
									+ ", " + tokenIndex.getEnd()
									+ ") for FE in sentence: " + text);
							return "";
						}
						if(tokenizedIndex.getStart() == tokenizedIndex.getEnd()){
							feChunks += tokenizedIndex.getStart();
							feChunks += "\t";
						}else{
							feChunks += tokenizedIndex.getStart();
							feChunks += ":";
							feChunks += tokenizedIndex.getEnd();
							feChunks += "\t";
						}
					}
				}
			}
		}
		return feChunks;
	}

	/**
	 * Parser does not handle discontinuous targets
	 * @param text
	 * @param annotationSet
	 * @return
	 */
	private String getTargetWithStartCharIndex(String text, Element annotationSet){
		String targetWithIndex = "";
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for(int i=0; i< layers.getLength(); i++){
			Element layer = (Element)layers.item(i);
			if(layer.getAttribute("name").equals("Target")){
				NodeList labels = layer.getElementsByTagName("label");
				// Handle annotation errors where
				// target layer labels are not specified
				if(labels.getLength() == 0){
					return targetWithIndex;
				}
				int minStart = -1;
				int maxEnd = -1;
				for(int j=0; j<labels.getLength(); j++){
					Element label =
							(Element)layer.getElementsByTagName("label").item(j);
					// Handle annotation errors where
					// the target start/end attributes are not specified
					if(!label.hasAttribute("start")
							|| !label.hasAttribute("end")){
						return targetWithIndex;
					}
					if(minStart == -1 ||
							Integer.parseInt(
									label.getAttribute("start")) < minStart){
						minStart =
								Integer.parseInt(
										label.getAttribute("start"));
					}
					if(maxEnd == -1 ||
							Integer.parseInt(
									label.getAttribute("end")) > maxEnd){
						maxEnd =
								Integer.parseInt(
										label.getAttribute("end"));
					}
				}
				if(minStart != -1 && maxEnd != -1){
					targetWithIndex = text.substring(minStart, maxEnd + 1)
							+ "#" + minStart;
				}
			}
		}
		return targetWithIndex;
	}

	// TODO: test this
	private List<Integer> getWhiteSpaceList(String text){
		List<Integer> whiteSpaces = new ArrayList<>();
		int counter = 0;
		// Start at 1 to avoid cases where text starts with a whitespace
		for(int i=1; i<text.length(); i++){
			if(Character.isWhitespace(text.charAt(i))) {
				counter += 1;
			}else{
				if(counter != 0){
					whiteSpaces.add(counter);
					counter = 0;
				}
			}
		}
		return whiteSpaces;
	}

	private String getTargetIndex(String text, String target, int startChar,
			Map<TokenIndex, TokenIndex> tokenIndexMap){
		String targetIndex = "";
		List<String> tokens = splitByWhiteSpace(text);
		List<String> targetTokens = splitByWhiteSpace(target);
		List<Integer> whiteSpaces = getWhiteSpaceList(text);
		int charIndex = 0;
		int start = -1;
		int end = -1;
		for(int i=0; i<tokens.size(); i++){
			if(startChar == charIndex){
				start = i;
				end = i + targetTokens.size() - 1;
				break;
			}else{
				charIndex += tokens.get(i).length() + whiteSpaces.get(i);
			}
		}
		TokenIndex tokenIndex = getTokenizedTokenIndex(tokenIndexMap, start, end);
		if(tokenIndex == null){
			return targetIndex;
		}
		for(int j=tokenIndex.getStart(); j<= tokenIndex.getEnd(); j++){
			if(targetIndex.isEmpty()){
				targetIndex += j;
			}else{
				targetIndex += "_" + j;
			}
		}
		return targetIndex;
	}

	private void addElementsToMap(String frameName, String luName, String text,
			Element annotationSet, Map<String, Integer> sentenceIndexMap,
			List<String> tokenizedSentences, Map<Integer, Set<String>> feMap){
		String targetWithStartCharIndex = getTargetWithStartCharIndex(text, annotationSet);
		if(targetWithStartCharIndex.isEmpty()){
			return;
		}
		int sentenceIndex = sentenceIndexMap.get(text);
		String tokenizedText = tokenizedSentences.get(sentenceIndex);
		Map<TokenIndex, TokenIndex> tokenIndexMap = SentenceToTokenizedIndexMapping
				.getTokenIndexMap(text, tokenizedText);
		String target = splitBy(targetWithStartCharIndex, "#").get(0);
		int startChar = Integer.parseInt(
				splitBy(targetWithStartCharIndex, "#").get(1));
		String targetIndex = getTargetIndex(text, target, startChar, tokenIndexMap);
		if(targetIndex.isEmpty()){
			logger.warn("Could not retrieve target index for "
					+ targetWithStartCharIndex + " in sentence: " + text);
			return;
		}
		int feFrameNumber = getFrameFENumber(annotationSet);
		String fesChunk = getFEsChunk(text, annotationSet, tokenIndexMap);
		String line = FE_VALUE_0 + "\t"
				+ FE_VALUE_1 + "\t"
				+ feFrameNumber + "\t"
				+ frameName + "\t"
				+ luName + "\t"
				+ targetIndex + "\t"
				+ target + "\t"
				+ sentenceIndex + "\t"
				+ fesChunk;
		if(feMap.containsKey(sentenceIndex)){
			feMap.get(sentenceIndex).add(line.trim());
		}else{
			Set<String> testFESet = new HashSet<>();
			testFESet.add(line.trim());
			feMap.put(sentenceIndex, testFESet);
		}
	}



	private void addFullTextFEData(Map<Integer, Set<String>> trainFEMap, String fullTextDir, Set<String> testSentenceSet, Set<String> testSetDocNameSet, Map<String, Integer> trainSentenceIndexMap, List<String> trainTokenizedSentences)
			throws IOException {
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf(".xml"));
				if(!testSetDocNameSet.contains(docName)){
					NodeList sentences = fullTextDoc.getDocumentElement()
							.getElementsByTagName("sentence");
					for(int i=0; i<sentences.getLength(); i++){
						Element sentence = (Element)sentences.item(i);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence
									.getElementsByTagName("text")
									.item(0)
									.getTextContent()
									.replaceAll("\\s+$", "");
							if(!testSentenceSet.contains(text)){
								NodeList annoSets = sentence
										.getElementsByTagName("annotationSet");
								for(int j=0; j<annoSets.getLength(); j++){
									Element annotationSet =
											(Element)annoSets.item(j);
									if(!annotationSet
											.getAttribute("luName")
											.isEmpty()){
										String frameName = annotationSet
												.getAttribute("frameName");
										String luName = annotationSet
												.getAttribute("luName");
										addElementsToMap(frameName, luName,
												text, annotationSet,
												trainSentenceIndexMap,
												trainTokenizedSentences,
												trainFEMap);
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
			List<String> trainTokenizedSentences)
			throws IOException {
		Files.walk(Paths.get(lexUnitDir)).forEach(filePath -> {
			if(Files.isRegularFile(filePath)
					&& filePath.toString().endsWith(".xml")){
				Element lexUnitElement =
						XmlUtils.parseXmlFile(filePath.toString(),
										false)
								.getDocumentElement();
				String luName = lexUnitElement.getAttribute("name");
				String frameName = lexUnitElement.getAttribute("frame");
				NodeList subCorpora =
						lexUnitElement.getElementsByTagName("subCorpus");
				for(int i=0; i<subCorpora.getLength(); i++){
					Element subCorpus = (Element)subCorpora.item(i);
					NodeList sentences =
							subCorpus.getElementsByTagName("sentence");
					for(int j=0; j<sentences.getLength(); j++){
						Element sentence = (Element)sentences.item(j);
						if(containsFrameNetAnnotation(sentence)){
							String text =
									sentence.getElementsByTagName("text")
											.item(0).getTextContent()
											.replaceAll("\\s+$", "");
							if(!testSentenceSet.contains(text)){
								NodeList annoSets =
										sentence.getElementsByTagName(
												"annotationSet");
								for(int k=0; k<annoSets.getLength(); k++){
									Element annotationSet = (Element)annoSets.item(k);
									addElementsToMap(frameName, luName, text,
											annotationSet, trainSentenceIndexMap,
											trainTokenizedSentences, trainFEMap);
								}
							}
						}
					}
				}
			}
		});
	}

	private Map<Integer, Set<String>> getTestFEMap(
			String fullTextDir,
			Set<String> testSetDocNameSet,
			Map<String, Integer> testSentenceIndexMap,
			List<String> testTokenizedSentences
	) throws IOException {
		Map<Integer, Set<String>> testFEMap = new TreeMap<>();
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath)
					&& filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(
						0, filePath.getFileName().toString().indexOf(".xml")
				);
				if(testSetDocNameSet.contains(docName)){
					NodeList sentences = fullTextDoc
							.getDocumentElement()
							.getElementsByTagName("sentence");
					for(int i=0; i<sentences.getLength(); i++){
						Element sentence = (Element)sentences.item(i);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence
									.getElementsByTagName("text")
									.item(0)
									.getTextContent().replaceAll("\\s+$", "");
							NodeList annoSets = sentence
									.getElementsByTagName("annotationSet");
							for(int j=0; j<annoSets.getLength(); j++){
								Element annotationSet = (Element)annoSets.item(j);
								if(!annotationSet
										.getAttribute("luName")
										.isEmpty()){
									String frameName =
											annotationSet
													.getAttribute("frameName");
									String luName =
											annotationSet
													.getAttribute("luName");
									addElementsToMap(frameName, luName, text,
											annotationSet, testSentenceIndexMap,
											testTokenizedSentences, testFEMap);
								}
							}
						}
					}
				}
			}
		});
		return testFEMap;
	}

	private Map<Integer, Set<String>> getTrainFEMap(
			String lexUnitDir,
			String fullTextDir,
			Set<String> testSentenceSet,
			Set<String> testSetDocNameSet,
			Map<String, Integer> trainSentenceIndexMap,
			List<String> trainTokenizedSentences
	) throws IOException {
		Map<Integer, Set<String>> trainFEMap = new TreeMap<>();
		addFullTextFEData(
				trainFEMap,
				fullTextDir,
				testSentenceSet,
				testSetDocNameSet,
				trainSentenceIndexMap,
				trainTokenizedSentences
		);
		addExemplarFEData(
				trainFEMap,
				lexUnitDir,
				testSentenceSet,
				trainSentenceIndexMap,
				trainTokenizedSentences
		);
		return trainFEMap;
	}

	private void createFESplits(
			String lexUnitDir,
			String fullTextDir,
			Set<String> testSetDocNameSet,
			String testSentenceSplits,
			String testTokenizedSentenceSplits,
			String trainSentenceSplits,
			String trainTokenizedSentenceSplits,
			String outTestFile,
			String outTrainFile
	) throws IOException {
		Map<String, Integer> testSentenceIndexMap =
				getSentenceIndexMap(testSentenceSplits);
		List<String> testTokenizedSentences = Files
				.lines(Paths.get(testTokenizedSentenceSplits))
				.collect(Collectors.toList());
		Map<Integer, Set<String>> testFEMap = getTestFEMap(
				fullTextDir,
				testSetDocNameSet,
				testSentenceIndexMap,
				testTokenizedSentences
		);
		List<String> testFEList = testFEMap
				.values()
				.stream()
				.flatMap(Set::stream)
				.collect(Collectors.toList());
		Map<String, Integer> trainSentenceIndexMap =
				getSentenceIndexMap(trainSentenceSplits);
		List<String> trainTokenizedSentences = Files
				.lines(Paths.get(trainTokenizedSentenceSplits))
				.collect(Collectors.toList());
		Set<String> testSentenceSet = Files
				.lines(Paths.get(testSentenceSplits))
				.collect(Collectors.toSet());
		Map<Integer, Set<String>> trainFEMap = getTrainFEMap(
				lexUnitDir,
				fullTextDir,
				testSentenceSet,
				testSetDocNameSet,
				trainSentenceIndexMap,
				trainTokenizedSentences
		);
		List<String> trainFEList = trainFEMap
				.values()
				.stream()
				.flatMap(Set::stream)
				.collect(Collectors.toList());
		Files.write(
				Paths.get(outTestFile),
				testFEList,
				Charset.defaultCharset()
		);
		Files.write(
				Paths.get(outTrainFile),
				trainFEList,
				Charset.defaultCharset()
		);
	}
}
