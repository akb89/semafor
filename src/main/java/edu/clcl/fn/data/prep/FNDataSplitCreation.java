package edu.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generate cv.***.sentences.*** splits from FrameNet XML data
 * for training and testing with the Semafor parser
 *
 * Process fulltext and exemplars separately
 * Do not keep fulltext sentences when processing exemplars
 * Remove sentences with no annotation
 * @author Alex Kabbach
 */
public class FNDataSplitCreation {

	public static void main(String[] args) throws IOException {
		//final String frameNetDataDir = args[0];
		//final String preprocessedDataDir = args[1];
		final String frameNetDataDir = "/Users/AKB/Dropbox/FrameNetData/fndata-1.5";
		final String resourcesDir = "/Users/AKB/Dropbox/GitHub/semafor/data";
		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";
		final String testSetDocsFile = "/Users/AKB/Dropbox/GitHub/semafor/resources/fn.fulltext.test.set.documents";
		final String outTestFile = resourcesDir + "/cv.test.sentences";
		final String outTrainFile = resourcesDir + "/cv.train.sentences";
		Set<String> testSetDocNameSet = getTestSetDocNameSet(testSetDocsFile);
		Set<Integer> fullDocIdSet = getFullTextSetDocIdSet(fullTextDir);
		//createSentenceSplits(lexUnitDir, fullTextDir, testSetDocNameSet, fullDocIdSet, outTestFile, outTrainFile);
		final String aclSplits = "/Users/AKB/Dropbox/GitHub/semafor/experiments/acl2015_fn_with_exemplars/data/cv.train.sentences.tokenized";
		final String mySplits = "/Users/AKB/Dropbox/GitHub/semafor/data/cv.train.sentences";
		compareSplits(aclSplits, mySplits);
	}

	private static void compareSplits(String aclSplits, String mySplits)
			throws IOException {
		List<String> aclSet = new ArrayList<>();
		List<String> mySet = new ArrayList<>();
		Files.lines(Paths.get(aclSplits)).forEach(aclSet::add);
		Files.lines(Paths.get(mySplits)).forEach(mySet::add);
		aclSet.forEach(entry -> {
			if(!mySet.contains(entry)){
				System.out.println("Sentence in aclSet but not in mySet = ");
				System.out.println(entry);
				System.out.println("");
			}
		});
		mySet.forEach(entry -> {
			if(!aclSet.contains(entry)){
				System.out.println("Sentence in mySet but not in aclSet = ");
				System.out.println(entry);
				System.out.println("");
			}
		});
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
			if (Files.isRegularFile(filePath) && filePath.getFileName().toString().endsWith(".xml")) {
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
			if (Files.isRegularFile(filePath) && filePath.getFileName().toString().endsWith(".xml")) {
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
			if (Files.isRegularFile(filePath) && filePath.getFileName().toString().endsWith(".xml")) {
				Document fullTextDoc = XmlUtils.parseXmlFile(filePath.toString(), false);
				boolean isInTestSet = false;
				String docName = filePath.getFileName().toString().substring(0, filePath.getFileName().toString().indexOf(".xml"));
				if(testSetDocNameSet.contains(docName)){
					isInTestSet = true;
				}
				Element fullTextElement = fullTextDoc.getDocumentElement();
				NodeList fullTextChildren = fullTextElement.getChildNodes();
				for(int i=0; i<fullTextChildren.getLength(); i++){
					if(fullTextChildren.item(i).getNodeName().equals("sentence")){
						Element sentenceElement = (Element)fullTextChildren.item(i);
						NodeList sentenceChildren = sentenceElement.getChildNodes();
						for(int k=0; k<sentenceChildren.getLength(); k++){
							if(sentenceChildren.item(k).getNodeName().equals("text")){
								if(isInTestSet){
									testSentenceSplits.add(sentenceChildren.item(k).getFirstChild().getNodeValue().trim());
								}else{
									trainSentenceSplits.add(sentenceChildren.item(k).getFirstChild().getNodeValue().trim());
								}
							}
						}
					}
				}
			}
		});
	}

	/**
	 * Create cv.train.sentences and cv.test.sentences
	 */
	private static void createSentenceSplits(String lexUnitDir, String fullTextDir, Set<String> testSetDocNameSet, Set<Integer> fullDocIdSet, String outTestFile, String outTrainFile)
			throws IOException {
		List<String> trainSentenceSplits = new ArrayList<>();
		List<String> testSentenceSplits = new ArrayList<>();
		processExemplars(lexUnitDir, trainSentenceSplits, fullDocIdSet);
		processFullTexts(fullTextDir, testSentenceSplits, trainSentenceSplits, testSetDocNameSet);
		Files.write(Paths.get(outTestFile), testSentenceSplits, Charset.defaultCharset());
		Files.write(Paths.get(outTrainFile), trainSentenceSplits, Charset.defaultCharset());
	}

	/**
	 * Create cv.train.sentences.tokenized and cv.test.sentences.tokenized
	 */
	private static void createTokenizedSplits(){

	}

	/**
	 * Create cv.train.sentences.pos.tagged and cv.test.sentences.pos.tagged
	 */
	private static void createPOSTaggedSplits(){

	}

	// Generate all.sentences.tokenized

	// Generate all.sentences.frames

	// Generate all.sentences.frame.elements

	// Generate all.sentences.mstparsed.conll

	// Generate all.sentences.all.lemma.tags

	// Split between test and train

	// Generate cv.train.sentences and cv.test.sentences

	// Generate cv.***.

	// Generate .frames files
	/**
	 * Lists frame targets and corresponding frames from annotated examples in the FrameNet corpus
	 * @param directory
	 * @param outFile
	 * @throws Exception
	 * @author dipanjan
	 * @see getSemEvalFrames()
	 */


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
