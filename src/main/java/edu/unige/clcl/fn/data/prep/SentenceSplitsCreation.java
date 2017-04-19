package edu.unige.clcl.fn.data.prep;

import edu.cmu.cs.lti.ark.util.XmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Generate cv.***.sentences splits from FrameNet XML data
 * for training and testing with Semafor
 * <p>
 * Process fulltext and exemplars separately
 * Remove sentences with no FrameNet annotation
 *
 * @author Alex Kabbach
 */
public class SentenceSplitsCreation {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) throws IOException {
		final String frameNetDataDir = args[0];
		final String trainSentenceSplits = args[1];
		final String testSentenceSplits = args[2];
		final String testSetDocsFile = args[3];
		final boolean withExemplars = Boolean.parseBoolean(args[4]);

		final String fullTextDir = frameNetDataDir + "/fulltext";
		final String lexUnitDir = frameNetDataDir + "/lu";

		SentenceSplitsCreation spCreation = new SentenceSplitsCreation();
		spCreation.logger.info("Generating training and testing sentences splits from FrameNet XML data...");

		Set<String> testSetDocNameSet = spCreation.getTestSetDocNameSet(testSetDocsFile);
		if (withExemplars) {
			spCreation.createSentenceSplits(lexUnitDir, fullTextDir, testSetDocNameSet,
					testSentenceSplits, trainSentenceSplits);
		} else {
			spCreation.createSentenceSplits(fullTextDir, testSetDocNameSet,
					testSentenceSplits, trainSentenceSplits);
		}
		spCreation.logger.info("Done generating training and testing sentences splits from FrameNet XML data");
	}

	private Set<String> getTestSetDocNameSet(String testSetDocsFile)
			throws IOException {
		Set<String> testSetDocNameSet = new HashSet<>();
		Files.lines(Paths.get(testSetDocsFile)).forEach(testSetDocNameSet::add);
		return testSetDocNameSet;
	}

	private boolean containsFrameNetAnnotation(Element sentenceElement) {
		NodeList annotationSets = sentenceElement
				.getElementsByTagName("annotationSet");
		for (int i = 0; i < annotationSets.getLength(); i++) {
			NodeList layers = annotationSets.item(i).getChildNodes();
			for (int j = 0; j < layers.getLength(); j++) {
				if (!layers.item(j).getNodeName().equals("#text")) {
					Element layer = (Element) layers.item(j);
					if (layer.getAttribute("name").equals("FE")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void addSentencesTextToSet(NodeList sentences,
			Set<String> trainSentenceSet, Set<String> testSentenceSet){
		for (int i = 0; i < sentences.getLength(); i++) {
			Element sentence = (Element) sentences.item(i);
			if (containsFrameNetAnnotation(sentence)) {
				String text = sentence.getElementsByTagName("text")
						.item(0).getTextContent()
						.replaceAll("\\s+$", "");
				if (!testSentenceSet.contains(text)) {
					trainSentenceSet.add(text);
				}
			}
		}
	}

	private void addFullTextSentences(String fullTextDir,
			Set<String> testSetDocNameSet, Set<String> testSentenceSet,
			Set<String> trainSentenceSet) throws IOException {
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString()
					.endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0,
						filePath.getFileName().toString().indexOf(".xml"));
				if (!testSetDocNameSet.contains(docName)) {
					NodeList sentences = fullTextDoc.getDocumentElement()
							.getElementsByTagName("sentence");
					addSentencesTextToSet(sentences, trainSentenceSet, testSentenceSet);
				}
			}
		});
	}

	private void addExemplarSentences(String lexUnitDir,
			Set<String> testSentenceSet, Set<String> trainSentenceSet)
			throws IOException {
		Files.walk(Paths.get(lexUnitDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString()
					.endsWith(".xml")) {
				Document lexUnitDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				NodeList subCorpora = lexUnitDoc.getDocumentElement()
						.getElementsByTagName("subCorpus");
				for (int i = 0; i < subCorpora.getLength(); i++) {
					Element subCorpus = (Element) subCorpora.item(i);
					NodeList sentences = subCorpus
							.getElementsByTagName("sentence");
					addSentencesTextToSet(sentences, trainSentenceSet, testSentenceSet);
				}
			}
		});
	}

	private Set<String> getTrainSentenceSet(String fullTextDir,
			String lexUnitDir, Set<String> testSetDocNameSet,
			Set<String> testSentenceSet) throws IOException {
		Set<String> trainSentenceSet = new HashSet<>();
		addFullTextSentences(fullTextDir, testSetDocNameSet, testSentenceSet,
				trainSentenceSet);
		addExemplarSentences(lexUnitDir, testSentenceSet, trainSentenceSet);
		return trainSentenceSet;
	}

	private Set<String> getTrainSentenceSet(String fullTextDir,
			Set<String> testSetDocNameSet, Set<String> testSentenceSet)
			throws IOException {
		Set<String> trainSentenceSet = new HashSet<>();
		addFullTextSentences(fullTextDir, testSetDocNameSet, testSentenceSet,
				trainSentenceSet);
		return trainSentenceSet;
	}

	private Set<String> getTestSentenceSet(String fullTextDir,
			Set<String> testSetDocNameSet) throws IOException {
		Set<String> testSentenceSet = new HashSet<>();
		Files.walk(Paths.get(fullTextDir)).forEach(filePath -> {
			if (Files.isRegularFile(filePath) && filePath.toString()
					.endsWith(".xml")) {
				Document fullTextDoc = XmlUtils
						.parseXmlFile(filePath.toString(), false);
				String docName = filePath.getFileName().toString().substring(0,
						filePath.getFileName().toString().indexOf(".xml"));
				if (testSetDocNameSet.contains(docName)) {
					NodeList sentences = fullTextDoc.getDocumentElement()
							.getElementsByTagName("sentence");
					for (int i = 0; i < sentences.getLength(); i++) {
						Element sentence = (Element) sentences.item(i);
						if (containsFrameNetAnnotation(sentence)) {
							String text = sentence.getElementsByTagName("text")
									.item(0).getTextContent()
									.replaceAll("\\s+$", "");
							testSentenceSet.add(text);
						}
					}
				}
			}
		});
		return testSentenceSet;
	}

	/**
	 * Create cv.train.sentences and cv.test.sentences with fulltext data only
	 */
	private void createSentenceSplits(String fullTextDir,
			Set<String> testSetDocNameSet, String outTestFile,
			String outTrainFile) throws IOException {
		Set<String> testSentenceSet = getTestSentenceSet(fullTextDir,
				testSetDocNameSet);
		logger.info("Test sentences = " + testSentenceSet.size());
		Set<String> trainSentenceSet = getTrainSentenceSet(fullTextDir,
				testSetDocNameSet, testSentenceSet);
		logger.info("Train sentences = " + trainSentenceSet.size());
		Files.write(Paths.get(outTestFile), testSentenceSet,
				StandardCharsets.UTF_8);
		Files.write(Paths.get(outTrainFile), trainSentenceSet,
				StandardCharsets.UTF_8);
	}

	/**
	 * Create cv.train.sentences and cv.test.sentences with fulltext and exemplars data
	 */
	private void createSentenceSplits(String lexUnitDir,
			String fullTextDir, Set<String> testSetDocNameSet,
			String outTestFile, String outTrainFile) throws IOException {
		Set<String> testSentenceSet = getTestSentenceSet(fullTextDir,
				testSetDocNameSet);
		logger.info("Test sentences = " + testSentenceSet.size());
		Set<String> trainSentenceSet = getTrainSentenceSet(fullTextDir,
				lexUnitDir, testSetDocNameSet, testSentenceSet);
		logger.info("Train sentences = " + trainSentenceSet.size());
		Files.write(Paths.get(outTestFile), testSentenceSet,
				StandardCharsets.UTF_8);
		Files.write(Paths.get(outTrainFile), trainSentenceSet,
				StandardCharsets.UTF_8);
	}

}
