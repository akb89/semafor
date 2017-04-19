package edu.unige.clcl.fn.data.prep.utils;

import edu.unige.clcl.fn.data.prep.models.TokenIndex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex Kabbach
 */
public class SentenceToTokenizedIndexMapping {

	private static Map<String, String> getSpecialCharMap() {
		Map<String, String> map = new HashMap<>();
		map.put("(", "-LRB-");
		map.put(")", "-RRB-");
		map.put("[", "-LSB-");
		map.put("]", "-RSB-");
		map.put("{", "-LCB-");
		map.put("}", "-RCB-");
		map.put("\"", "``");
		map.put("“", "``");
		map.put("”", "\'\'");
		return map;
	}

	private static String[] replaceSpecialChars(String[] tokens,
			Map<String, String> specialCharMap) {
		List<String> output = new ArrayList<>();
		for (int i = 0; i < tokens.length; i++) {
			String token = "";
			for (int j = 0; j < tokens[i].length(); j++) {
				if (specialCharMap
						.containsKey(Character.toString(tokens[i].charAt(j)))) {
					token += specialCharMap
							.get(Character.toString(tokens[i].charAt(j)));
				} else {
					token += tokens[i].charAt(j);
				}
			}
			output.add(token);
		}
		return output.toArray(new String[output.size()]);
	}

	public static Map<TokenIndex, TokenIndex> getTokenIndexMap(String sentence,
			String tokenizedSentence) {
		String[] sentenceTokens = sentence.split("\\s+");
		String[] tokenizedTokens = tokenizedSentence.split("\\s+");
		Map<String, String> specialCharMap = getSpecialCharMap();
		sentenceTokens = replaceSpecialChars(sentenceTokens, specialCharMap);
		Map<TokenIndex, TokenIndex> indexMap = new TreeMap<>();
		int start = 0;
		for (int i = 0; i < sentenceTokens.length; i++) {
			for (int j = start; j < tokenizedTokens.length; j++) {
				if (sentenceTokens[i].equals(tokenizedTokens[j])) {
					indexMap.put(new TokenIndex(i, i), new TokenIndex(j, j));
					start = j + 1;
					break;
				} else {
					if (tokenizedTokens[j].contains(sentenceTokens[i])) {
						break;
					} else if (sentenceTokens[i].contains(tokenizedTokens[j])) {
						String agglo = "";
						for (int k = j; k < tokenizedTokens.length; k++) {
							agglo += tokenizedTokens[k];
							if (!sentenceTokens[i].contains(agglo)) {
								indexMap.put(new TokenIndex(i, i),
										new TokenIndex(j, k - 1));
								start = k;
								break;
							} else if (k + 1 == tokenizedTokens.length) {
								indexMap.put(new TokenIndex(i, i),
										new TokenIndex(j, k));
							}
						}
						break;
					}
				}
			}
		}
		return indexMap;
	}
}
