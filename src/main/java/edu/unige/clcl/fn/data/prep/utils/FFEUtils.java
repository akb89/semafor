package edu.unige.clcl.fn.data.prep.utils;

import edu.unige.clcl.fn.data.prep.models.TokenIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex Kabbach
 */
public class FFEUtils {

	private static final Logger logger = LoggerFactory.getLogger(FFEUtils.class);

	public static List<String> splitBy(String text, String regex) {
		return Arrays.asList(text.split(regex));
	}

	public static List<String> splitByWhiteSpace(String text) {
		return splitBy(text, "\\s+");
	}

	public static Set<String> getTestSetDocNameSet(String testSetDocsFile)
			throws IOException {
		return Files.lines(Paths.get(testSetDocsFile))
				.collect(Collectors.toSet());
	}

	public static boolean containsFrameNetAnnotation(Element sentenceElement) {
		NodeList annotationSets = sentenceElement
				.getElementsByTagName("annotationSet");
		for (int i = 0; i < annotationSets.getLength(); i++) {
			NodeList layers = annotationSets.item(i).getChildNodes();
			for (int j = 0; j < layers.getLength(); j++) {
				if (!layers.item(j).getNodeName().equals("#text")) {
					Element layer = (Element) layers.item(j);
					if (!layer.getAttribute("name").equals("FE")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static Map<String, Integer> getSentenceIndexMap(String sentenceSplits)
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

	public static int getFrameFENumber(Element annotationSet) {
		int frameFENumber = 1; // At least one for the frame
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for (int i = 0; i < layers.getLength(); i++) {
			Element layer = (Element) layers.item(i);
			if (layer.getAttribute("name").equals("FE")) {
				NodeList labels = layer.getElementsByTagName("label");
				for (int j = 0; j < labels.getLength(); j++) {
					Element label = (Element) labels.item(j);
					// Only count FEs visible in the sentence (no CNI, DNI etc.)
					if (label.hasAttribute("start") && label
							.hasAttribute("end")) {
						frameFENumber += 1;
					}
				}
			}
		}
		return frameFENumber;
	}

	// TODO: test this and maybe optimize a bit
	public static TokenIndex getTokenizedTokenIndex(
			Map<TokenIndex, TokenIndex> tokenIndexMap, int startTokenIndex,
			int endTokenIndex) {
		int startIndex = -1;
		int endIndex = -1;
		for (TokenIndex key : tokenIndexMap.keySet()) {
			if (key.getStart() <= startTokenIndex && startTokenIndex <= key
					.getEnd()) {
				startIndex = tokenIndexMap.get(key).getStart();
			}
			if (key.getStart() <= endTokenIndex && endTokenIndex <= key
					.getEnd()) {
				endIndex = tokenIndexMap.get(key).getEnd();
			}
		}
		if (startIndex == -1 || endIndex == -1) {
			return null;
		}
		return new TokenIndex(startIndex, endIndex);
	}

	public static String getTargetIndex(String text, String targetWithIndex,
			Map<TokenIndex, TokenIndex> tokenIndexMap) {
		int targetStartChar = Integer
				.parseInt(splitBy(targetWithIndex, "#").get(1));
		int targetEndChar =
				targetStartChar + splitBy(targetWithIndex, "#").get(0).length()
						- 1;
		TokenIndex tokenIndex = toTokenIndex(text, targetStartChar,
				targetEndChar);
		if (tokenIndex == null) {
			logger.warn(
					"Could not find TokenIndex (" + tokenIndex.getStart() + ", "
							+ tokenIndex.getEnd() + ") for target: "
							+ targetWithIndex + " in sentence: " + text);
			return "";
		}
		TokenIndex tokenizedIndex = getTokenizedTokenIndex(tokenIndexMap,
				tokenIndex.getStart(), tokenIndex.getEnd());
		if (tokenizedIndex == null) {
			logger.warn("Could not find tokenized TokenIndex (" + tokenIndex
					.getStart() + ", " + tokenIndex.getEnd() + ") for target: "
					+ targetWithIndex + " in sentence: " + text);
			return "";
		}
		if (tokenizedIndex.getStart() == tokenizedIndex.getEnd()) {
			return String.valueOf(tokenizedIndex.getStart());
		} else {
			return tokenizedIndex.getStart() + "_" + tokenizedIndex.getEnd();
		}
	}

	public static TokenIndex toTokenIndex(String text, int startChar, int endChar) {
		List<String> tokens = splitByWhiteSpace(text);
		List<String> sequence = splitByWhiteSpace(
				text.substring(startChar, endChar + 1));
		int charIndex = 0;
		for (int i = 0; i < tokens.size(); i++) {
			if (tokens.get(i).isEmpty()) {
				charIndex += 1;
			} else {
				while (tokens.get(i).charAt(0) != text.charAt(charIndex)) {
					charIndex += 1;
				}
			}
			if (tokens.get(i).equals(sequence.get(0))
					&& charIndex == startChar) {
				int startTokenIndex = i;
				int endTokenIndex = i;
				if (sequence.size() > 1) {
					endTokenIndex = startTokenIndex + sequence.size() - 1;
				}
				return new TokenIndex(startTokenIndex, endTokenIndex);
			}
			charIndex += tokens.get(i).length();
		}
		return null;
	}

	public static String getFEsChunk(String text, Element annotationSet,
			Map<TokenIndex, TokenIndex> tokenIndexMap) {
		String feChunks = "";
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for (int i = 0; i < layers.getLength(); i++) {
			Element layer = (Element) layers.item(i);
			if (layer.getAttribute("name").equals("FE")) {
				NodeList labels = layer.getElementsByTagName("label");
				for (int j = 0; j < labels.getLength(); j++) {
					Element label = (Element) labels.item(j);
					if (label.hasAttribute("start") && label
							.hasAttribute("end")) {
						int start = Integer
								.parseInt(label.getAttribute("start"));
						int end = Integer.parseInt(label.getAttribute("end"));
						TokenIndex tokenIndex = toTokenIndex(text, start, end);
						if (tokenIndex == null) {
							logger.warn(
									"Could not find TokenIndex (" + start + ", "
											+ end + ") for FE in sentence: "
											+ text);
							return "";
						}
						feChunks += label.getAttribute("name");
						feChunks += "\t";
						TokenIndex tokenizedIndex = getTokenizedTokenIndex(
								tokenIndexMap, tokenIndex.getStart(),
								tokenIndex.getEnd());
						if (tokenizedIndex == null) {
							logger.warn("Could not find tokenized TokenIndex ("
									+ tokenIndex.getStart() + ", " + tokenIndex
									.getEnd() + ") for FE in sentence: "
									+ text);
							return "";
						}
						if (tokenizedIndex.getStart() == tokenizedIndex
								.getEnd()) {
							feChunks += tokenizedIndex.getStart();
							feChunks += "\t";
						} else {
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
	 */
	public static String getTargetWithStartCharIndex(String text,
			Element annotationSet) {
		String targetWithIndex = "";
		NodeList layers = annotationSet.getElementsByTagName("layer");
		for (int i = 0; i < layers.getLength(); i++) {
			Element layer = (Element) layers.item(i);
			if (layer.getAttribute("name").equals("Target")) {
				NodeList labels = layer.getElementsByTagName("label");
				// Handle annotation errors where
				// target layer labels are not specified
				if (labels.getLength() == 0) {
					return targetWithIndex;
				}
				int minStart = -1;
				int maxEnd = -1;
				for (int j = 0; j < labels.getLength(); j++) {
					Element label = (Element) layer
							.getElementsByTagName("label").item(j);
					// Handle annotation errors where
					// the target start/end attributes are not specified
					if (!label.hasAttribute("start") || !label
							.hasAttribute("end")) {
						return targetWithIndex;
					}
					if (minStart == -1
							|| Integer.parseInt(label.getAttribute("start"))
							< minStart) {
						minStart = Integer
								.parseInt(label.getAttribute("start"));
					}
					if (maxEnd == -1
							|| Integer.parseInt(label.getAttribute("end"))
							> maxEnd) {
						maxEnd = Integer.parseInt(label.getAttribute("end"));
					}
				}
				if (minStart != -1 && maxEnd != -1) {
					targetWithIndex = text.substring(minStart, maxEnd + 1) + "#"
							+ minStart;
				}
			}
		}
		return targetWithIndex;
	}

	// TODO: test this
	public static List<Integer> getWhiteSpaceList(String text) {
		List<Integer> whiteSpaces = new ArrayList<>();
		int counter = 0;
		// Start at 1 to avoid cases where text starts with a whitespace
		for (int i = 1; i < text.length(); i++) {
			if (Character.isWhitespace(text.charAt(i))) {
				counter += 1;
			} else {
				if (counter != 0) {
					whiteSpaces.add(counter);
					counter = 0;
				}
			}
		}
		return whiteSpaces;
	}

	public static String getTargetIndex(String text, String target, int startChar,
			Map<TokenIndex, TokenIndex> tokenIndexMap) {
		String targetIndex = "";
		List<String> tokens = splitByWhiteSpace(text);
		List<String> targetTokens = splitByWhiteSpace(target);
		List<Integer> whiteSpaces = getWhiteSpaceList(text);
		int charIndex = 0;
		int start = -1;
		int end = -1;
		for (int i = 0; i < tokens.size(); i++) {
			if (startChar == charIndex) {
				start = i;
				end = i + targetTokens.size() - 1;
				break;
			} else {
				charIndex += tokens.get(i).length() + whiteSpaces.get(i);
			}
		}
		TokenIndex tokenIndex = getTokenizedTokenIndex(tokenIndexMap, start,
				end);
		if (tokenIndex == null) {
			return targetIndex;
		}
		for (int j = tokenIndex.getStart(); j <= tokenIndex.getEnd(); j++) {
			if (targetIndex.isEmpty()) {
				targetIndex += j;
			} else {
				targetIndex += "_" + j;
			}
		}
		return targetIndex;
	}
}
