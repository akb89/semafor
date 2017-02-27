package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import edu.unige.clcl.fn.data.prep.utils.SentenceToTokenizedIndexMapping;
import edu.unige.clcl.fn.data.prep.utils.TokenIndex;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
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

	public static void main(String[] args) throws IOException {
		/*final String frameNetDataDir = args[0];
		final String trainSentenceSplits = args[1];
		final String testSentenceSplits = args[2];
		final String testSetDocsFile = args[3];
		final String trainFESplits = args[4];
		final String testFESplits = args[5];*/

		/*
		List<String> sentences = Files.lines(Paths.get(sentenceSplits)).collect(
				Collectors.toList());
				*/

		final String frameNetDataDir = "/Users/AKB/Dropbox/FrameNetData/fndata-1.5";
		final String resourcesDir = "/Users/AKB/Dropbox/GitHub/semafor/data";
		final String testSentenceSplits = resourcesDir + "/cv.test.sentences"; // TODO: use global variable?
		final String trainSentenceSplits = resourcesDir + "/cv.train.sentences"; // TODO: use global variable?
		final String testTokenizedSentenceSplits = resourcesDir + "/cv.test.sentences.tokenized";
		final String trainTokenizedSentenceSplits = resourcesDir + "/cv.train.sentences.tokenized";
		final String testSetDocsFile = "/Users/AKB/Dropbox/GitHub/semafor/resources/fn.fulltext.test.set.documents";
		final String testFESplits = resourcesDir + "/cv.test.sentences.frame.elements";
		final String trainFESplits = resourcesDir + "/cv.train.sentences.frame.elements";

		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";

		Set<String> testSetDocNameSet = getTestSetDocNameSet(testSetDocsFile);
		createFESplits(lexUnitDir, fullTextDir, testSetDocNameSet, testSentenceSplits, testTokenizedSentenceSplits, trainSentenceSplits, trainTokenizedSentenceSplits, testFESplits, trainFESplits);
		//test(trainSentenceSplits, trainTokenizedSentenceSplits);
	}

	private static void test(String sentenceSplits, String tokenizedSentenceSplits)
			throws IOException {
		String text = "As husband of Louis VII's sister , Constance , Raymond of Toulouse could count on help from his brother-in-law .";
		String targetWithIndex = "count#73";
		Map<String, Integer> sentenceMap = getSentenceIndexMap(sentenceSplits);
		List<String> tokenizedSentences = Files.lines(Paths.get(tokenizedSentenceSplits)).collect(
				Collectors.toList());
		Map<TokenIndex, TokenIndex> indexMap = SentenceToTokenizedIndexMapping.getTokenIndexMap(text, tokenizedSentences.get(sentenceMap.get(text)));
		String targetIndex = getTargetIndex(text, targetWithIndex, indexMap);
		System.out.println(targetIndex);
		if(targetIndex.isEmpty()){
			System.out.println("isEmpty");
		}
	}

	private static Set<String> getTestSetDocNameSet(String testSetDocsFile)
			throws IOException {
		Set<String> testSetDocNameSet = new HashSet<>();
		Files.lines(Paths.get(testSetDocsFile)).forEach(testSetDocNameSet::add);
		return testSetDocNameSet;
	}

	private static boolean containsFrameNetAnnotation(Element sentenceElement){
		NodeList annotationSets = sentenceElement.getElementsByTagName("annotationSet");
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

	private static Map<String, Integer> getSentenceIndexMap(String sentenceSplits)
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

	private static int getFrameFENumber(Element annotationSet){
		int frameFENumber = 1; // At least one for the frame
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for(int i=0; i< layers.getLength(); i++){
			Element layer = (Element)layers.item(i);
			if(layer.getAttribute("name").equals("FE")){
				NodeList labels = layer.getElementsByTagName("label");
				for(int j=0; j<labels.getLength(); j++){
					Element label = (Element)labels.item(j);
					// Only count FEs visible in the sentence (no CNI, DNI etc.)
					if(label.hasAttribute("start") && label.hasAttribute("end")){
						frameFENumber += 1;
					}
				}
			}
		}
		return frameFENumber;
	}

	/**
	 * A target with its startchar, separated by a #
	 * string#startchar
	 * @param text
	 * @param annotationSet
	 * @return
	 */
	private static String getTarget(String text, Element annotationSet){
		String target = "";
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for(int i=0; i< layers.getLength(); i++){
			Element layer = (Element)layers.item(i);
			if(layer.getAttribute("name").equals("Target")){
				Element label = (Element)layer.getElementsByTagName("label").item(0);
				// Handle annotation errors where the target start/end is not specified
				if(label == null){
					return target;
				}
				target = text.substring(Integer.parseInt(label.getAttribute("start")), Integer.parseInt(label.getAttribute("end"))+1) + "#" + label.getAttribute("start");
			}
		}
		return target;
	}

	// TODO: test this and maybe optimize a bit
	private static TokenIndex getTokenizedTokenIndex(Map<TokenIndex, TokenIndex> tokenIndexMap, int startTokenIndex, int endTokenIndex){
		int startIndex = -1;
		int endIndex = -1;
		for(TokenIndex key: tokenIndexMap.keySet()){
			if(key.getStart() <= startTokenIndex && startTokenIndex <= key.getEnd()){
				startIndex = tokenIndexMap.get(key).getStart();
			}
			if(key.getStart() <= endTokenIndex && endTokenIndex <= key.getEnd()){
				endIndex = tokenIndexMap.get(key).getEnd();
			}
		}
		if(startIndex == -1 || endIndex == -1){
			return null;
		}
		return new TokenIndex(startIndex, endIndex);
	}

	private static String getTargetIndex(String text, String targetWithIndex, Map<TokenIndex, TokenIndex> tokenIndexMap){
		String[] targetTokens = targetWithIndex.split(Pattern.quote("#"));
		String[] targetSplit = targetTokens[0].split("\\s+");
		String[] tokens = text.split("\\s+");
		int targetCharIndex = Integer.parseInt(targetTokens[1]);
		int charIndex = 0;
		for(int i=0; i<tokens.length; i++){
			if(tokens[i].isEmpty()){
				charIndex += 1;
			}else{
				while(tokens[i].charAt(0) != text.charAt(charIndex)){
					charIndex += 1;
				}
			}
			if(tokens[i].equals(targetSplit[0]) && charIndex == targetCharIndex){
				int startTokenIndex = i;
				int endTokenIndex = i;
				if(targetSplit.length > 1){
					endTokenIndex = targetSplit.length -1;
				}
				TokenIndex tokenizedIndex = getTokenizedTokenIndex(tokenIndexMap, startTokenIndex, endTokenIndex);
				if(tokenizedIndex == null){
					System.err.println("Could not find TokenIndex (" + startTokenIndex +", " + endTokenIndex + ") for target: " + targetWithIndex + " in sentence: " + text);
					return "";
				}
				if(tokenizedIndex.getStart() == tokenizedIndex.getEnd()){
					return String.valueOf(tokenizedIndex.getStart());
				}else{
					return tokenizedIndex.getStart() + "_" + tokenizedIndex.getEnd();
				}
			}
			charIndex += tokens[i].length();
		}
		return "";
	}

	private static TokenIndex toTokenIndex(String text, int start, int end){
		String[] tokens = text.split("\\s+");
		List<String> tokenList = Arrays.asList(tokens);
		String sub = text.substring(start, end+1);
		String[] subTokens = sub.split("\\s+");
		List<String> subList = Arrays.asList(subTokens);
		Collections.indexOfSubList(tokenList, subList);
		Collections.lastIndexOfSubList(tokenList, subList);
	}

	private static String getFEsChunk(String text, Element annotationSet, Map<TokenIndex, TokenIndex> tokenIndexMap){
		String feChunks = "";
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for(int i=0; i< layers.getLength(); i++){
			Element layer = (Element)layers.item(i);
			if(layer.getAttribute("name").equals("FE")){
				NodeList labels = layer.getElementsByTagName("label");
				for(int j=0; j<labels.getLength(); j++){
					Element label = (Element)labels.item(j);
					if(label.hasAttribute("start") && label.hasAttribute("end")){
						int start = Integer.parseInt(label.getAttribute("start"));
						int end = Integer.parseInt(label.getAttribute("end"));
						TokenIndex toTokenIndex = toTokenIndex(text, start, end);
						int tokenIterator = 0;
						int startTokenIndex = 0;
						int endTokenIndex = 0;
						for(int k=0; k<text.length(); k++){
							if(k == start){
								startTokenIndex = tokenIterator;
							}
							if(k == end){
								endTokenIndex = tokenIterator;
							}
							if(Character.isWhitespace(text.charAt(k))){
								tokenIterator += 1;
							}
						}
						feChunks += label.getAttribute("name");
						feChunks += "\t";
						TokenIndex tokenizedIndex = getTokenizedTokenIndex(tokenIndexMap, startTokenIndex, endTokenIndex);
						if(tokenizedIndex == null){
							System.err.println("Could not find TokenIndex (" + startTokenIndex +", " + endTokenIndex + ") for FE in sentence: " + text);
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

	private static void addElementsToMap(String frameName, String luName, String text, Element annotationSet, Map<String, Integer> sentenceIndexMap, List<String> tokenizedSentences, Map<Integer, Set<String>> feMap){
		int feFrameNumber = getFrameFENumber(annotationSet);
		String targetWithIndex = getTarget(text, annotationSet);
		if(!targetWithIndex.isEmpty()){
			int sentenceIndex = sentenceIndexMap.get(text);
			String tokenizedText = tokenizedSentences.get(sentenceIndex);
			Map<TokenIndex, TokenIndex> tokenIndexMap = SentenceToTokenizedIndexMapping
					.getTokenIndexMap(text, tokenizedText);
			String targetIndex = getTargetIndex(text, targetWithIndex, tokenIndexMap);
			if(targetIndex.isEmpty()){
				System.err.println("Could not retrieve target index for " + targetWithIndex + " in sentence: " + text);
			}
			String target = targetWithIndex.split(Pattern.quote("#"))[0];
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
			//System.out.println(line);
		}
	}

	private static Map<Integer, Set<String>> getTestFEMap(String fullTextDir, Set<String> testSetDocNameSet, Map<String, Integer> testSentenceIndexMap, List<String> testTokenizedSentences)
			throws IOException {
		Map<Integer, Set<String>> testFEMap = new TreeMap<>();
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf(".xml"));
				if(testSetDocNameSet.contains(docName)){
					NodeList sentences = fullTextDoc.getDocumentElement().getElementsByTagName("sentence");
					for(int i=0; i<sentences.getLength(); i++){
						Element sentence = (Element)sentences.item(i);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().replaceAll("\\s+$", "");
							NodeList annoSets = sentence.getElementsByTagName("annotationSet");
							for(int j=0; j<annoSets.getLength(); j++){
								Element annotationSet = (Element)annoSets.item(j);
								if(!annotationSet.getAttribute("luName").isEmpty()){
									String frameName = annotationSet.getAttribute("frameName");
									String luName = annotationSet.getAttribute("luName");
									addElementsToMap(frameName, luName, text, annotationSet, testSentenceIndexMap, testTokenizedSentences, testFEMap);
								}
							}
						}
					}
				}
			}
		});
		return testFEMap;
	}

	private static void addFullTextFEData(Map<Integer, Set<String>> trainFEMap, String fullTextDir, Set<String> testSentenceSet, Set<String> testSetDocNameSet, Map<String, Integer> trainSentenceIndexMap, List<String> trainTokenizedSentences)
			throws IOException {
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf(".xml"));
				if(!testSetDocNameSet.contains(docName)){
					NodeList sentences = fullTextDoc.getDocumentElement().getElementsByTagName("sentence");
					for(int i=0; i<sentences.getLength(); i++){
						Element sentence = (Element)sentences.item(i);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().replaceAll("\\s+$", "");
							if(!testSentenceSet.contains(text)){
								NodeList annoSets = sentence.getElementsByTagName("annotationSet");
								for(int j=0; j<annoSets.getLength(); j++){
									Element annotationSet = (Element)annoSets.item(j);
									if(!annotationSet.getAttribute("luName").isEmpty()){
										String frameName = annotationSet.getAttribute("frameName");
										String luName = annotationSet.getAttribute("luName");
										addElementsToMap(frameName, luName, text, annotationSet, trainSentenceIndexMap, trainTokenizedSentences, trainFEMap);
									}
								}
							}
						}
					}
				}
			}
		});
	}

	private static void addExemplarFEData(Map<Integer, Set<String>> trainFEMap, String lexUnitDir, Set<String> testSentenceSet, Map<String, Integer> trainSentenceIndexMap, List<String> trainTokenizedSentences)
			throws IOException {
		Files.walk(Paths.get(lexUnitDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Element lexUnitElement = XmlUtils.parseXmlFile(filePath.toString(), false).getDocumentElement();
				String luName = lexUnitElement.getAttribute("name");
				String frameName = lexUnitElement.getAttribute("frame");
				NodeList subCorpora = lexUnitElement.getElementsByTagName("subCorpus");
				for(int i=0; i<subCorpora.getLength(); i++){
					Element subCorpus = (Element)subCorpora.item(i);
					NodeList sentences = subCorpus.getElementsByTagName("sentence");
					for(int j=0; j<sentences.getLength(); j++){
						Element sentence = (Element)sentences.item(j);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().replaceAll("\\s+$", "");
							if(!testSentenceSet.contains(text)){
								NodeList annoSets = sentence.getElementsByTagName("annotationSet");
								for(int k=0; k<annoSets.getLength(); k++){
									Element annotationSet = (Element)annoSets.item(k);
									addElementsToMap(frameName, luName, text, annotationSet, trainSentenceIndexMap, trainTokenizedSentences, trainFEMap);
								}
							}
						}
					}
				}
			}
		});
	}

	private static Map<Integer, Set<String>> getTrainFEMap(String lexUnitDir, String fullTextDir, Set<String> testSentenceSet, Set<String> testSetDocNameSet, Map<String, Integer> trainSentenceIndexMap, List<String> trainTokenizedSentences)
			throws IOException {
		Map<Integer, Set<String>> trainFEMap = new TreeMap<>();
		addFullTextFEData(trainFEMap, fullTextDir, testSentenceSet, testSetDocNameSet, trainSentenceIndexMap, trainTokenizedSentences);
		addExemplarFEData(trainFEMap, lexUnitDir, testSentenceSet, trainSentenceIndexMap, trainTokenizedSentences);
		return trainFEMap;
	}

	private static Set<String> getSentenceSet(String sentenceSplits)
			throws IOException {
		Set<String> sentenceSet = new HashSet<>();
		Files.lines(Paths.get(sentenceSplits)).forEach(sentenceSet::add);
		return sentenceSet;
	}

	private static void createFESplits(String lexUnitDir, String fullTextDir, Set<String> testSetDocNameSet, String testSentenceSplits, String testTokenizedSentenceSplits, String trainSentenceSplits, String trainTokenizedSentenceSplits, String outTestFile, String outTrainFile)
			throws IOException {
		Map<String, Integer> testSentenceIndexMap = getSentenceIndexMap(testSentenceSplits);
		List<String> testTokenizedSentences = Files.lines(Paths.get(testTokenizedSentenceSplits)).collect(
				Collectors.toList());
		Map<Integer, Set<String>> testFEMap = getTestFEMap(fullTextDir, testSetDocNameSet, testSentenceIndexMap, testTokenizedSentences);
		List<String> testFEList = new ArrayList<>();
		testFEMap.forEach((key, value) -> {
			testFEList.addAll(value);
		});
		Map<String, Integer> trainSentenceIndexMap = getSentenceIndexMap(trainSentenceSplits);
		List<String> trainTokenizedSentences = Files.lines(Paths.get(trainTokenizedSentenceSplits)).collect(
				Collectors.toList());
		Set<String> testSentenceSet = getSentenceSet(testSentenceSplits);
		Map<Integer, Set<String>> trainFEMap = getTrainFEMap(lexUnitDir, fullTextDir, testSentenceSet, testSetDocNameSet, trainSentenceIndexMap, trainTokenizedSentences);
		List<String> trainFEList = new ArrayList<>();
		trainFEMap.forEach((key, value) -> {
			trainFEList.addAll(value);
		});
		Files.write(Paths.get(outTestFile), testFEList, Charset.defaultCharset());
		Files.write(Paths.get(outTrainFile), trainFEList, Charset.defaultCharset());
	}
}
