package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate cv.***.sentences splits from FrameNet XML data
 * for training and testing with Semafor
 *
 * Process fulltext and exemplars separately
 * Remove sentences with no FrameNet annotation
 * @author Alex Kabbach
 */
public class SentenceSplitsCreation {

	public static void main(String[] args) throws IOException {
		final String frameNetDataDir = args[0];
		final String trainSentenceSplits = args[1];
		final String testSentenceSplits = args[2];
		final String testSetDocsFile = args[3];

		/*final String frameNetDataDir = "/Users/AKB/Dropbox/FrameNetData/fndata-1.5";
		final String resourcesDir = "/Users/AKB/Dropbox/GitHub/semafor/data";
		final String testSentenceSplits = resourcesDir + "/cv.test.sentences"; // TODO: use global variable?
		final String trainSentenceSplits = resourcesDir + "/cv.train.sentences"; // TODO: use global variable?
		final String testSetDocsFile = "/Users/AKB/Dropbox/GitHub/semafor/resources/fn.fulltext.test.set.documents";*/

		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";

		Set<String> testSetDocNameSet = getTestSetDocNameSet(testSetDocsFile);
		createSentenceSplits(lexUnitDir, fullTextDir, testSetDocNameSet, testSentenceSplits, trainSentenceSplits);
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
					if(layer.getAttribute("name").equals("FE")){
						return true;
					}
				}
			}
		}
		return false;
	}

	private static void addFullTextSentences(String fullTextDir, Set<String> testSetDocNameSet, Set<String> testSentenceSet, Set<String> trainSentenceSet)
			throws IOException {
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf(".xml"));
				if(!testSetDocNameSet.contains(docName)){
					NodeList sentences = fullTextDoc.getDocumentElement().getElementsByTagName("sentence");
					for(int i=0; i<sentences.getLength(); i++){
						Element sentence = (Element)sentences.item(i);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().replaceAll("\\s+$", "");;
							if(!testSentenceSet.contains(text)){
								trainSentenceSet.add(text);
							}
						}
					}
				}
			}
		});
	}

	private static void addExemplarSentences(String lexUnitDir, Set<String> testSentenceSet, Set<String> trainSentenceSet)
			throws IOException {
		Files.walk(Paths.get(lexUnitDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document lexUnitDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				NodeList subCorpora = lexUnitDoc.getDocumentElement().getElementsByTagName("subCorpus");
				for(int i=0; i<subCorpora.getLength(); i++){
					Element subCorpus = (Element)subCorpora.item(i);
					NodeList sentences = subCorpus.getElementsByTagName("sentence");
					for(int j=0; j<sentences.getLength(); j++){
						Element sentence = (Element)sentences.item(j);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().replaceAll("\\s+$", "");;
							if(!testSentenceSet.contains(text)){
								trainSentenceSet.add(text);
							}
						}
					}
				}
			}
		});
	}

	private static Set<String> getTrainSentenceSet(String fullTextDir, String lexUnitDir, Set<String> testSetDocNameSet, Set<String> testSentenceSet)
			throws IOException {
		Set<String> trainSentenceSet = new HashSet<>();
		addFullTextSentences(fullTextDir, testSetDocNameSet, testSentenceSet, trainSentenceSet);
		addExemplarSentences(lexUnitDir, testSentenceSet, trainSentenceSet);
		return trainSentenceSet;
	}

	private static Set<String> getTestSentenceSet(String fullTextDir, Set<String> testSetDocNameSet)
			throws IOException {
		Set<String> testSentenceSet = new HashSet<>();
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf(".xml"));
				if(testSetDocNameSet.contains(docName)){
					NodeList sentences = fullTextDoc.getDocumentElement().getElementsByTagName("sentence");
					for(int i=0; i<sentences.getLength(); i++){
						Element sentence = (Element)sentences.item(i);
						if(containsFrameNetAnnotation(sentence)){
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().replaceAll("\\s+$", "");
							testSentenceSet.add(text);
						}
					}
				}
			}
		});
		return testSentenceSet;
	}

	/**
	 * Create cv.train.sentences and cv.test.sentences
	 */
	private static void createSentenceSplits(String lexUnitDir, String fullTextDir, Set<String> testSetDocNameSet, String outTestFile, String outTrainFile)
			throws IOException {
		Set<String> testSentenceSet = getTestSentenceSet(fullTextDir, testSetDocNameSet);
		System.out.println("Test sentences = " + testSentenceSet.size());
		Set<String> trainSentenceSet = getTrainSentenceSet(fullTextDir, lexUnitDir, testSetDocNameSet, testSentenceSet);
		System.out.println("Train sentences = " + trainSentenceSet.size());
		testSentenceSet.forEach(sentence -> System.out.println(sentence));
		Files.write(Paths.get(outTestFile), testSentenceSet, Charset.forName("UTF-8"));
		Files.write(Paths.get(outTrainFile), trainSentenceSet, Charset.forName("UTF-8"));
	}

	private static Map<String, Integer> getTrainSentenceToNumberMap(String trainSentenceSplits)
			throws IOException {
		Map<String, Integer> map = new HashMap<>();
		int sentenceIterator = 0;
		List<String> sentences = Files.lines(Paths.get(trainSentenceSplits)).collect(
				Collectors.toList());
		for (String sentence : sentences) {
			map.put(sentence, sentenceIterator);
			sentenceIterator += 1;
		}
		return map;
	}

	private static Map<String, Integer> getTestSentenceToNumberMap(){
		Map<String, Integer> map = new HashMap<>();

		return map;
	}

	// Generate .frames files
	private static void createFrameSentenceSplits(){
		//trainSentenceToNumberMap = getTrainSentenceToNumberMap();
		//testSentenceToNumberMap = getTestSentenceToNumberMap();
	}


	// Get .frame.elements
	/**
	 * Lists frame targets and corresponding frames from annotated examples in the FrameNet corpus

	 */
	/**
	 * For each frame instance in annotated examples from the FrameNet corpus, lists information about the target and frame elements.
	 * Line format (tab-separated; ## indicates either a single token number or a multi-token span, like 9_10):
	 * #spans	target_LU	target_token##	LU_token	sentence#	FE_name1	token1##	FE_name2	token2## ...
	 * @param directory
	 * @param outFile
	 * @throws Exception
	 * @author Nathan Schneider (nschneid)
	 * @see Adapted from originalFNUnits()
	 * @see getSemevalFrameElements()
	 */

}
