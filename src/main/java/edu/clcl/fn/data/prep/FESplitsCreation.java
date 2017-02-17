package edu.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
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
 * Indexes correspond to the tokenized splits
 * @author Alex Kabbach
 */
public class FESplitsCreation {

	public static final String FE_VALUE_0 = "1";
	public static final String FE_VALUE_1 = "0.0";

	public static void main(String[] args) throws IOException {
		/*final String frameNetDataDir = args[0];
		final String trainSentenceSplits = args[1];
		final String testSentenceSplits = args[2];
		final String testSetDocsFile = args[3];*/

		final String frameNetDataDir = "/Users/AKB/Dropbox/FrameNetData/fndata-1.5";
		final String resourcesDir = "/Users/AKB/Dropbox/GitHub/semafor/data";
		final String testSentenceSplits = resourcesDir + "/cv.test.sentences"; // TODO: use global variable?
		final String trainSentenceSplits = resourcesDir + "/cv.train.sentences"; // TODO: use global variable?
		final String testSetDocsFile = "/Users/AKB/Dropbox/GitHub/semafor/resources/fn.fulltext.test.set.documents";

		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";

		final String outTestFile = resourcesDir + "/cv.test.sentences.frame.elements";
		final String outTrainFile = resourcesDir + "/cv.train.sentences.frame.elements";

		final String naaclSentences = "/Users/AKB/Dropbox/GitHub/semafor/experiments/naacl2012/cv.test.sentences";
		final String naaclTokenizedSentences = "/Users/AKB/Dropbox/GitHub/semafor/experiments/naacl2012/cv.test.sentences.tokenized";

		Set<String> testSetDocNameSet = getTestSetDocNameSet(testSetDocsFile);
		createFESplits(lexUnitDir, fullTextDir, testSetDocNameSet, testSentenceSplits, trainSentenceSplits, outTestFile, outTrainFile);
		//test(naaclSentences, naaclTokenizedSentences);
	}

	private static void test(String sentencesSplits, String tokenizedSplits)
			throws IOException {
		List<String> sentences = new ArrayList<>();
		List<String> tokenized = new ArrayList<>();
		Files.lines(Paths.get(sentencesSplits)).forEach(sentences::add);
		Files.lines(Paths.get(tokenizedSplits)).forEach(tokenized::add);
		for(int i=0; i<sentences.size(); i++){
			Map<Integer, Integer> map = getTokenIndexMap(sentences.get(i), tokenized.get(i));
			map.entrySet().forEach(entry -> {
				System.out.println("i = " + entry.getKey() + "\t j = " + entry.getValue());
			});
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
				frameFENumber += layer.getElementsByTagName("label").getLength();
			}
		}
		return frameFENumber;
	}

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
				target = text.substring(Integer.parseInt(label.getAttribute("start")), Integer.parseInt(label.getAttribute("end"))+1);
			}
		}
		return target;
	}

	private static int getTargetIndex(String text, String target){
		String[] tokens = text.split("\\s");
		for(int i=0; i<tokens.length; i++){
			if(tokens[i].equals(target)){
				return i;
			}
		}
		return 0;
	}

	private static Map<Integer, Integer> getTokenIndexMap(String text, String tokenizedText){
		Map<Integer, Integer> indexMap = new HashMap<>();
		String[] tokens = text.split("\\s");
		String[] tokenizedTokens = tokenizedText.split("\\s");
		if(tokens.length != tokenizedTokens.length){
			System.err.println("ERROR");
		}
		for(int i=0; i<tokens.length; i++){
			for(int j=i; j<tokenizedTokens.length; j++){
				if(tokens[i].equals(tokens[j])){
					indexMap.put(i,j);
					break;
				}
			}
		}
		return indexMap;
	}

	private static String getFEsChunk(String text, Element annotationSet){
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
						if (startTokenIndex == endTokenIndex){
							feChunks += startTokenIndex;
							feChunks += "\t";
						}else{
							feChunks += startTokenIndex;
							feChunks += ":";
							feChunks += endTokenIndex;
							feChunks += "\t";
						}
					}
				}
			}
		}
		return feChunks;
	}

	private static Map<Integer, Set<String>> getTestFEMap(String fullTextDir, Set<String> testSetDocNameSet, Map<String, Integer> testSentenceIndexMap)
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
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().trim();
							NodeList annoSets = sentence.getElementsByTagName("annotationSet");
							for(int j=0; j<annoSets.getLength(); j++){
								Element annotationSet = (Element)annoSets.item(j);
								if(!annotationSet.getAttribute("luName").isEmpty()){
									int feFrameNumber = getFrameFENumber(annotationSet);
									String frameName = annotationSet.getAttribute("frameName");
									String luName = annotationSet.getAttribute("luName");
									String target = getTarget(text, annotationSet);
									int targetIndex = getTargetIndex(text, target); // FIXME: multiword pull out.v	13_14	pull out
									int sentenceIndex = testSentenceIndexMap.get(text);
									String fesChunk = getFEsChunk(text, annotationSet);
									// Handle annotation errors where target start/end is not specified
									if(!target.isEmpty()){
										String line = FE_VALUE_0 + "\t"
												+ FE_VALUE_1 + "\t"
												+ feFrameNumber + "\t"
												+ frameName + "\t"
												+ luName + "\t"
												+ targetIndex + "\t"
												+ target + "\t"
												+ sentenceIndex + "\t"
												+ fesChunk;
										if(testFEMap.containsKey(sentenceIndex)){
											testFEMap.get(sentenceIndex).add(line.trim());
										}else{
											Set<String> testFESet = new HashSet<>();
											testFESet.add(line.trim());
											testFEMap.put(sentenceIndex, testFESet);
										}
										System.out.println(line);
									}
								}
							}
						}
					}
				}
			}
		});
		return testFEMap;
	}

	private static Set<String> getTrainFESet(){
		Set<String> trainFESet = new HashSet<>();

		return trainFESet;
	}

	private static void createFESplits(String lexUnitDir, String fullTextDir, Set<String> testSetDocNameSet, String testSentenceSplits, String trainSentenceSplits, String outTestFile, String outTrainFile)
			throws IOException {
		Map<String, Integer> testSentenceIndexMap = getSentenceIndexMap(testSentenceSplits);
		Map<Integer, Set<String>> testFEMap = getTestFEMap(fullTextDir, testSetDocNameSet, testSentenceIndexMap);
		List<String> testFEList = new ArrayList<>();
		testFEMap.forEach((key, value) -> {
			testFEList.addAll(value);
		});
		Map<String, Integer> trainSentenceIndexMap = getSentenceIndexMap(trainSentenceSplits);
		//Set<String> trainFESet = getTrainFESet();
		Files.write(Paths.get(outTestFile), testFEList, Charset.defaultCharset());
		//Files.write(Paths.get(outTrainFile), trainFESet, Charset.defaultCharset());
	}
}
