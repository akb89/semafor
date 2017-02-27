package edu.unige.clcl.fn.data.prep.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alex Kabbach
 */
public class SentenceToTokenizedIndexMapping {

	public static void main(String[] args) throws IOException {
		String trainSenSplits15 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences.15";
		String trainTokenizedSenSplits15 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences.tokenized.15";
		String testSenSplits15 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.test.sentences.15";
		String testTokenizedSenSplits15 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.test.sentences.tokenized.15";
		String trainSenSplits16 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences.16";
		String trainTokenizedSenSplits16 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences.tokenized.16";
		String testSenSplits16 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.test.sentences.16";
		String testTokenizedSenSplits16 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.test.sentences.tokenized.16";
		String trainSenSplits17 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences.17";
		String trainTokenizedSenSplits17 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences.tokenized.17";
		String testSenSplits17 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.test.sentences.17";
		String testTokenizedSenSplits17 = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.test.sentences.tokenized.17";

		Map<String, String> data = new HashMap<>();
		data.put(trainSenSplits15, trainTokenizedSenSplits15);
		data.put(testSenSplits15, testTokenizedSenSplits15);
		data.put(trainSenSplits16, trainTokenizedSenSplits16);
		data.put(testSenSplits16, testTokenizedSenSplits16);
		data.put(trainSenSplits17, trainTokenizedSenSplits17);
		data.put(testSenSplits17, testTokenizedSenSplits17);

		data.forEach((senSplits, tokenizedSenSplits) -> {
			System.out.println("Processing files: ");
			System.out.println("	Sentences splits: " + senSplits);
			System.out.println("	Tokenize splits: " + tokenizedSenSplits);
			try {
				List<String> sentences = Files.lines(Paths.get(senSplits)).collect(
						Collectors.toList());
				List<String> tokenizedSentences = Files.lines(Paths.get(tokenizedSenSplits)).collect(
						Collectors.toList());
				int validMaps = 0;
				for(int i=0; i<sentences.size(); i++){
					Map<TokenIndex, TokenIndex> indexMap = getTokenIndexMap(sentences.get(i), tokenizedSentences.get(i));
					checkCollidingTokens(indexMap);
					validMaps += validMap(indexMap, sentences.get(i), tokenizedSentences.get(i), true);
				}
				System.out.println();
				System.out.println("Total valid maps = " + validMaps + " out of " + sentences.size());
				if(validMaps != sentences.size()){
					int invalidMaps = sentences.size() - validMaps;
					System.out.println("Invalid maps = " + invalidMaps);
				}
				System.out.println("--------------------------------------------------------------------------------");
				System.out.println();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static void checkCollidingTokens(Map<TokenIndex, TokenIndex> indexMap){
		Set<Integer> indexSet = new HashSet<>();
		Set<Integer>  tIndexSet = new HashSet<>();
		indexMap.keySet().forEach((key) -> {
			for(int i=key.getStart(); i <= key.getEnd(); i++){
				if(indexSet.contains(i)){
					System.out.println("Colliding token found");
				}else{
					indexSet.add(i);
				}
			}
		});
		indexMap.values().forEach((value) -> {
			for(int j=value.getStart(); j<= value.getEnd(); j++){
				if(tIndexSet.contains(j)){
					System.out.println("Colliding token found");
				}else{
					tIndexSet.add(j);
				}
			}
		});
	}

	private static String getString(TokenIndex tokenIndex, List<String> tokens, Map<String, String> specialCharMap){
		String output = "";
		for(int i = tokenIndex.getStart(); i <= tokenIndex.getEnd(); i++){
			String token = "";
			for(int j=0; j<tokens.get(i).length(); j++){
				if(specialCharMap.containsKey(Character.toString(tokens.get(i).charAt(j)))){
					token += specialCharMap.get(Character.toString(tokens.get(i).charAt(j)));
				}else{
					token += tokens.get(i).charAt(j);
				}
			}
			output += token;
		}
		return output;
	}

	private static boolean containsIndexInKeys(int index, Map<TokenIndex, TokenIndex> indexMap){
		for(Map.Entry<TokenIndex, TokenIndex> entry: indexMap.entrySet()){
			if(entry.getKey().getStart() == index){
				return true;
			}
			if(entry.getKey().getEnd() == index){
				return true;
			}
		}
		return false;
	}

	private static boolean containsIndexInValues(int index, Map<TokenIndex, TokenIndex> indexMap){
		for(Map.Entry<TokenIndex, TokenIndex> entry: indexMap.entrySet()){
			if(entry.getValue().getStart() <= index && index <= entry.getValue().getEnd()){
				return true;
			}
		}
		return false;
	}

	private static List<String> getErrors(Map<TokenIndex, TokenIndex> indexMap, List<String> sentenceTokens, List<String> tSentenceTokens, Map<String, String> specialCharMap){
		List<String> errors = new ArrayList<>();
		if(indexMap.keySet().size() != sentenceTokens.size()){
			for(int i=0; i<sentenceTokens.size(); i++){
				if(!containsIndexInKeys(i, indexMap)){
					errors.add("Failed to process sentence token at index = " + i + " -> " + sentenceTokens.get(i));
				}
			}
		}
		if(indexMap.values().size() != tSentenceTokens.size()){
			for(int i=0; i<tSentenceTokens.size(); i++){
				if(!containsIndexInValues(i, indexMap)){
					errors.add("Failed to process tokenized sentence token at index = " + i + " -> " + tSentenceTokens.get(i));
				}
			}
		}
		indexMap.forEach((key, value) -> {
			String sToken = getString(key, sentenceTokens, specialCharMap);
			String tToken = getString(value, tSentenceTokens, specialCharMap);
			if(!sToken.equals(tToken)){
				if(specialCharMap.containsKey(sToken)){
					if(!specialCharMap.get(sToken).equals(tToken)){
						errors.add("Matching error: " + sToken + " != " + tToken);
					}
				}else{
					errors.add("Matching error: " + sToken + " != " + tToken);
				}
			}
		});
		return errors;
	}

	private static int validMap(Map<TokenIndex, TokenIndex> indexMap, String sentence, String tokenizedSentence, boolean strictTest){
		Map<String, String> specialCharMap = getSpecialCharMap();
		List<String> sentenceTokens = Arrays.asList(sentence.split("\\s+"));
		List<String> tSentenceTokens = Arrays.asList(tokenizedSentence.split("\\s+"));
		//System.out.println("sentenceTokens = " + sentenceTokens.size());
		//System.out.println("tSentenceTokens = " + tSentenceTokens.size());
		List<String> errors = new ArrayList<>();
		if(strictTest || (!strictTest && sentenceTokens.size() != tSentenceTokens.size())){
			errors = getErrors(indexMap, sentenceTokens, tSentenceTokens, specialCharMap);
		}
		if(errors.size() != 0){
			/*System.out.println("Checking sentence: ");
			System.out.println(sentence);
			System.out.println();
			System.out.println("Tokenized version: ");
			System.out.println(tokenizedSentence);
			System.out.println();
			errors.forEach(System.out::println);
			System.out.println("--------------------------------------------------------------------------------");
			System.out.println();*/
			return 0;
		}
		return 1;
	}

	private static Map<String, String> getSpecialCharMap(){
		Map<String, String> map = new HashMap<>();
		map.put("(","-LRB-");
		map.put(")","-RRB-");
		map.put("[","-LSB-");
		map.put("]","-RSB-");
		map.put("{","-LCB-");
		map.put("}","-RCB-");
		map.put("\"", "``");
		map.put("“", "``");
		map.put("”", "\'\'");
		return map;
	}

	private static String[] replaceSpecialChars(String[] tokens, Map<String, String> specialCharMap){
		List<String> output = new ArrayList<>();
		for(int i=0; i<tokens.length; i++){
			String token = "";
			for(int j=0; j<tokens[i].length(); j++){
				if(specialCharMap.containsKey(Character.toString(tokens[i].charAt(j)))){
					token += specialCharMap.get(Character.toString(tokens[i].charAt(j)));
				}else{
					token += tokens[i].charAt(j);
				}
			}
			output.add(token);
		}
		return output.toArray(new String[output.size()]);
	}

	public static Map<TokenIndex, TokenIndex> getTokenIndexMap(String sentence, String tokenizedSentence){
		String[] sentenceTokens = sentence.split("\\s+");
		String[] tokenizedTokens = tokenizedSentence.split("\\s+");
		Map<String,String> specialCharMap = getSpecialCharMap();
		sentenceTokens = replaceSpecialChars(sentenceTokens, specialCharMap);
		Map<TokenIndex, TokenIndex> indexMap = new TreeMap<>();
		int start = 0;
		for(int i=0; i<sentenceTokens.length; i++){
			//System.out.println("Processing token = " + sentenceTokens[i]);
			//System.out.println("i = " + i + " start = " + start);
			for(int j=start; j<tokenizedTokens.length; j++){
				if(sentenceTokens[i].equals(tokenizedTokens[j])){
					//System.out.println("Matching: i = " + sentenceTokens[i] + " j = " + tokenizedTokens[j]);
					//System.out.println("i = " + i);
					//System.out.println("j = " + j);
					indexMap.put(new TokenIndex(i,i), new TokenIndex(j,j));
					start = j + 1;
					break;
				}else {
					if(tokenizedTokens[j].contains(sentenceTokens[i])){
						break;
					} else if(sentenceTokens[i].contains(tokenizedTokens[j])){
						//System.out.println("Containment i -> j");
						//System.out.println("	i = " + i + " -> " + sentenceTokens[i]);
						//System.out.println("	j = " + j + " -> " + tokenizedTokens[j]);
						String agglo = "";
						for(int k = j; k<tokenizedTokens.length; k++){
							agglo += tokenizedTokens[k];
							//System.out.println("agglo = " + agglo);
							if(!sentenceTokens[i].contains(agglo)){
								//System.out.println("Stopping containment at k = " + k);
								indexMap.put(new TokenIndex(i,i), new TokenIndex(j,k-1));
								start = k;
								break;
							}else if(k + 1 == tokenizedTokens.length){
								indexMap.put(new TokenIndex(i,i), new TokenIndex(j,k));
								//System.out.println("Contains !");
								//System.out.println("k = " + k + " -> " + tokenizedTokens[k]);
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
