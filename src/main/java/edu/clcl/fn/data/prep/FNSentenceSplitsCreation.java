package edu.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generate cv.***.sentences splits from FrameNet XML data
 * for training and testing with the Semafor parser
 *
 * Process fulltext and exemplars separately
 * Remove sentences with no annotation
 * @author Alex Kabbach
 */
public class FNSentenceSplitsCreation {

	private static Map<Integer, String> sentenceIdToTrainTestMap;
	private static Map<String, Integer> trainSentenceToNumberMap;
	private static Map<String, Integer> testSentenceToNumberMap;

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

		Set<String> testSetDocNameSet = getTestSetDocNameSet(testSetDocsFile);
		Set<Integer> fullDocIdSet = getFullTextSetDocIdSet(fullTextDir);
		createSentenceSplits(lexUnitDir, fullTextDir, testSetDocNameSet, testSentenceSplits, trainSentenceSplits);

		final String naaclTrainingSplits = "/Users/AKB/Dropbox/GitHub/semafor/experiments/naacl2012/cv.train.sentences";
		final String naaclTestingSplits = "/Users/AKB/Dropbox/GitHub/semafor/experiments/naacl2012/cv.test.sentences";

		final String aclFTTrainingSplits = "/Users/AKB/Dropbox/GitHub/semafor/experiments/acl2015_fn_with_exemplars/data/cv.train.sentences.tokenized";
		final String aclFTTestingSplits = "/Users/AKB/Dropbox/GitHub/semafor/experiments/naacl2012/cv.test.sentences.tokenized";

		final String aclExTestingSplits = "/Users/AKB/Dropbox/GitHub/semafor/experiments/acl2015_exemplars/data/cv.test.sentences.tokenized";

		/*
		System.out.println("NAACL training and testing splits");
		compareSplits(naaclTrainingSplits, naaclTestingSplits);
		System.out.println();
		System.out.println("ACL training splits with NAACL testing splits");
		compareSplits(aclFTTrainingSplits, aclFTTestingSplits);
		System.out.println();
		System.out.println("ACL training splits with ACL testing splits");
		compareSplits(aclFTTrainingSplits, aclExTestingSplits);
		*/

		/*final String aclSplits = "/Users/AKB/Dropbox/GitHub/semafor/experiments/acl2015_fn_with_exemplars/data/cv.train.sentences.tokenized";
		final String mySplits = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences";
		compareSplits(aclSplits, mySplits);*/
	}

	private static void compareSplits(String trainingSplits, String testingSplits)
			throws IOException {
		List<String> trainingList = new ArrayList<>();
		List<String> testingList = new ArrayList<>();
		Files.lines(Paths.get(trainingSplits)).forEach(trainingList::add);
		Files.lines(Paths.get(testingSplits)).forEach(testingList::add);
		System.out.println("TrainingList size = " + trainingList.size());
		System.out.println("TestingList size = " + testingList.size());
		Set<String> trainingSet = new HashSet<>(trainingList);
		Set<String> testingSet = new HashSet<>(testingList);
		System.out.println("TrainingSet size = " + trainingSet.size());
		System.out.println("TestingSet size = " + testingSet.size());
		trainingSet.retainAll(testingSet);
		System.out.println("Intersection = " + trainingSet.size());
		System.out.println();
		trainingSet.forEach(System.out::println);
	}

	private static Set<String> getTestSetDocNameSet(String testSetDocsFile)
			throws IOException {
		Set<String> testSetDocNameSet = new HashSet<>();
		Files.lines(Paths.get(testSetDocsFile)).forEach(testSetDocNameSet::add);
		return testSetDocNameSet;
	}

	private static Set<Integer> getFullTextSetDocIdSet(String fullTextDir)
			throws IOException {
		Set<Integer> docIdSet = new HashSet<>();
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				Element documentElement = XmlUtils.getUniqueChildNodeFromXPath(fullTextDoc, "/fullTextAnnotation/header/corpus/document");
				Integer docID = Integer.parseInt(documentElement.getAttribute("ID"));
				docIdSet.add(docID);
			}
		});
		return docIdSet;
	}

	// TODO: remove sentences with no annotation?
	private static void processExemplars(String lexUnitDir, List<String> trainSentenceSplits, Set<Integer> fullDocIdSet)
			throws IOException {
		Files.walk(Paths.get(lexUnitDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document lexUnitDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				Element lexUnitElement = lexUnitDoc.getDocumentElement();
				NodeList lexUnitChildren = lexUnitElement.getChildNodes();
				for(int i=0; i<lexUnitChildren.getLength(); i++){
					if(lexUnitChildren.item(i).getNodeName().equals("subCorpus")){
						Element subCorpusElement = (Element)lexUnitChildren.item(i);
						NodeList subCorpusChildren = subCorpusElement.getChildNodes();
						for(int j=0; j<subCorpusChildren.getLength(); j++){
							if(subCorpusChildren.item(j).getNodeName().equals("sentence")){
								Element sentenceElement = (Element)subCorpusChildren.item(j);
								if(containsFrameNetAnnotation(sentenceElement)){
									Integer sentenceId = Integer.parseInt(sentenceElement.getAttribute("ID"));
									// If exemplar sentence not in fulltext (process separately)
									boolean isInFullTextSet = false;
									if(sentenceElement.hasAttribute("docID")){
										Integer docID = Integer.parseInt(sentenceElement.getAttribute("docID"));
										if(fullDocIdSet.contains(docID)){
											isInFullTextSet = true;
										}
									}
									if(!isInFullTextSet){
										NodeList sentenceChildren = sentenceElement.getChildNodes();
										for(int k=0; k<sentenceChildren.getLength(); k++){
											if(sentenceChildren.item(k).getNodeName().equals("text")){
												trainSentenceSplits.add(sentenceChildren.item(k).getFirstChild().getNodeValue().trim());
												if(sentenceIdToTrainTestMap.containsKey(sentenceId) && sentenceIdToTrainTestMap
														.get(sentenceId).equals("test")){
													System.err.println("Incompatible status for sentence with ID = " + sentenceId);
												}else{
													sentenceIdToTrainTestMap
															.put(sentenceId, "train");
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		});
	}

	// TODO: remove sentences with no annotation?
	private static void processFullTexts(String fullTextDir, List<String> testSentenceSplits, List<String> trainSentenceSplits, Set<String> testSetDocNameSet)
			throws IOException {
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				boolean isInTestSet = false;
				String docName = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf(".xml"));
				if(!testSetDocNameSet.contains(docName)){

				}
				Element fullTextElement = fullTextDoc.getDocumentElement();
				NodeList fullTextChildren = fullTextElement.getChildNodes();
				for(int i=0; i<fullTextChildren.getLength(); i++){
					if(fullTextChildren.item(i).getNodeName().equals("sentence")){
						Element sentenceElement = (Element)fullTextChildren.item(i);
						if(containsFrameNetAnnotation(sentenceElement)){
							Integer sentenceId = Integer.parseInt(sentenceElement.getAttribute("ID"));
							NodeList sentenceChildren = sentenceElement.getChildNodes();
							for(int k=0; k<sentenceChildren.getLength(); k++){
								if(sentenceChildren.item(k).getNodeName().equals("text")){
									if(isInTestSet){
										testSentenceSplits.add(sentenceChildren.item(k).getFirstChild().getNodeValue().trim());
										if(sentenceIdToTrainTestMap.containsKey(sentenceId) && sentenceIdToTrainTestMap
												.get(sentenceId).equals("train")){
											System.err.println("Incompatible status for sentence with ID = " + sentenceId);
										}else{
											sentenceIdToTrainTestMap
													.put(sentenceId, "test");
										}
									}else{
										trainSentenceSplits.add(sentenceChildren.item(k).getFirstChild().getNodeValue().trim());
										if(sentenceIdToTrainTestMap.containsKey(sentenceId) && sentenceIdToTrainTestMap
												.get(sentenceId).equals("test")){
											System.err.println("Incompatible status for sentence with ID = " + sentenceId);
										}else{
											sentenceIdToTrainTestMap
													.put(sentenceId, "train");
										}
									}
								}
							}
						}
					}
				}
			}
		});
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
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().trim();
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
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().trim();
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
							String text = sentence.getElementsByTagName("text").item(0).getTextContent().trim();
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
		sentenceIdToTrainTestMap = new HashMap<>();
		Set<String> testSentenceSet = getTestSentenceSet(fullTextDir, testSetDocNameSet);
		System.out.println("Test sentences = " + testSentenceSet.size());
		Set<String> trainSentenceSet = getTrainSentenceSet(fullTextDir, lexUnitDir, testSetDocNameSet, testSentenceSet);
		System.out.println("Train sentences = " + trainSentenceSet.size());
		Files.write(Paths.get(outTestFile), testSentenceSet, Charset.defaultCharset());
		Files.write(Paths.get(outTrainFile), trainSentenceSet, Charset.defaultCharset());
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
