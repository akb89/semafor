package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.SerializedObjects;
import gnu.trove.THashMap;
import gnu.trove.THashSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alex Kabbach
 */
public class TrainingRequiredDataCreation {

	private static final int FRAME_ELEMENT_SPLITS_FRAME_INDEX = 3;
	private static final int FRAME_ELEMENT_SPLITS_FE_START_INDEX = 8;

	public static void main(String[] args) throws IOException {
		//final String frameNetDataDir = args[0];
		//final String preprocessedDataDir = args[1];
		final String frameNetDataDir = "/Users/AKB/Dropbox/FrameNetData/fndata-1.5";
		final String preprocessedDataDir = "/Users/AKB/Dropbox/GitHub/semafor/data";
		final String outputFramesFile = preprocessedDataDir + "/frames.xml";
		String frameElementSplits = preprocessedDataDir + "/cv.train.sentences.frame.elements";
		//THashMap<String,THashSet<String>> newMap = _createFrameFEMap(outputFramesFile);
		THashMap<String,THashSet<String>> newMap = createFrameFEMap(frameElementSplits);
		THashMap<String,THashSet<String>> oldMap = (THashMap<String,THashSet<String>>) SerializedObjects
				.readSerializedObject("/Users/AKB/Dropbox/GitHub/semafor/data/framenet.frame.element.map.old");
		THashMap<String,THashSet<String>> originalMap = (THashMap<String,THashSet<String>>)SerializedObjects
				.readSerializedObject("/Users/AKB/Dropbox/GitHub/semafor/data/framenet.original.map.old");
		//System.out.println(oldMap);
		//System.out.println(originalMap);
		//compareMaps(oldMap, newMap);
		//compareFEs(oldMap, newMap);

		String tokenizedSentenceFile = preprocessedDataDir + "/cv.test.sentences.tokenized";
		String sentenceFile = preprocessedDataDir + "/cv.test.sentences";

		test(tokenizedSentenceFile, sentenceFile);
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

	private static Map<Integer, String> getIndexSentenceMap(String sentenceSplits)
			throws IOException {
		Map<Integer, String> map = new HashMap<>();
		int sentenceIterator = 0;
		List<String> sentences = Files.lines(Paths.get(sentenceSplits)).collect(
				Collectors.toList());
		for (String sentence : sentences) {
			map.put(sentenceIterator, sentence);
			sentenceIterator += 1;
		}
		return map;
	}

	private static void test(String tokenizedSentenceFile, String sentenceFile)
			throws IOException {
		Map<String, Integer> sentenceMap = getSentenceIndexMap(sentenceFile);
		Map<Integer, String> tokenizedMap = getIndexSentenceMap(tokenizedSentenceFile);
		sentenceMap.entrySet().forEach((entry) -> {
			int slength = entry.getKey().split("\\s+").length;
			int tlength = tokenizedMap.get(entry.getValue()).split("\\s+").length;
			if(slength != tlength){
				System.out.println("Length is not equal for sentence #" + entry.getValue());
			}
		});
	}

	/**
	 * Populates the given map object with frames (as keys) and sets of target words that evoke
	 * those frames in the given corresponding sentences (as values)
	 * @author dipanjan
	 */
	private static void compareMaps(THashMap<String,THashSet<String>> originalMap, THashMap<String,THashSet<String>> newMap){
		System.out.println("New Map Size = "+newMap.size());
		System.out.println("Old Map Size = "+originalMap.size());
		int counter = 0;
		for(Map.Entry<String, THashSet<String>> entry : newMap.entrySet()){
			if(!originalMap.containsKey(entry.getKey())){
				System.out.println("Original map does not contain frame: "+entry.getKey());
				counter+=1;
			}
		}
		for(Map.Entry<String, THashSet<String>> entry : originalMap.entrySet()){
			if(!newMap.containsKey(entry.getKey())){
				System.out.println("New map does not contain frame: "+entry.getKey());
			}
		}
		System.out.println("Counter = "+counter);
	}

	private static void compareFEs(THashMap<String,THashSet<String>> originalMap, THashMap<String,THashSet<String>> newMap){
		int counter = 0;
		for(Map.Entry<String, THashSet<String>> entry : newMap.entrySet()){
			if(originalMap.containsKey(entry.getKey())){
				THashSet<String> originalFEs = originalMap.get(entry.getKey());
				THashSet<String> newFEs = entry.getValue();
				if(originalFEs.size() != newFEs.size()){
					counter += 1;
					System.out.println("Different FE set size for frame: " + entry.getKey());
					if(originalFEs.size() < newFEs.size()){
						System.out.println(originalFEs.size());
						System.out.println(newFEs.size());
						newFEs.forEach((fe) -> {
							if(!originalFEs.contains(fe)){
								System.out.println("FE = " + fe + " is not in the original map");
							}
						});
					}
				}
			}
		}
		System.out.println("New Map Size = " + newMap.size());
		System.out.println("Old Map Size = " + originalMap.size());
		System.out.println("Different Frames Size = " + counter);
	}

	private static String getFrame(String line){
		String[] tokens = line.split("\\t");
		return tokens[FRAME_ELEMENT_SPLITS_FRAME_INDEX];
	}

	private static THashSet<String> getFEs(String line){
		THashSet<String> fes = new THashSet<>();
		String[] tokens = line.split("\\t");
		for(int i = FRAME_ELEMENT_SPLITS_FE_START_INDEX; i < tokens.length; i += 2){
			fes.add(tokens[i]);
		}
		return fes;
	}

	private static THashMap<String,THashSet<String>> createFrameFEMap(String frameElementSplits)
			throws IOException {
		THashMap<String,THashSet<String>> frameFEMap = new THashMap<>();
		Files.lines(Paths.get(frameElementSplits)).forEach((line) -> {
			String frame = getFrame(line);
			THashSet<String> fes = getFEs(line);
			if(frameFEMap.containsKey(frame)){
				frameFEMap.get(frame).addAll(fes);
			}else{
				frameFEMap.put(frame, fes);
			}
		});
		return frameFEMap;
	}
}
