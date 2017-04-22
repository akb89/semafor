package edu.unige.clcl.fn.score;

import com.google.common.collect.Lists;
import edu.cmu.cs.lti.ark.fn.data.prep.formats.AllLemmaTags;
import edu.cmu.cs.lti.ark.fn.data.prep.formats.Sentence;
import edu.cmu.cs.lti.ark.fn.data.prep.formats.SentenceCodec;
import edu.cmu.cs.lti.ark.fn.evaluation.PrepareFullAnnotationXML;
import edu.cmu.cs.lti.ark.fn.parsing.DataPrep;
import edu.cmu.cs.lti.ark.fn.parsing.Decoding;
import edu.cmu.cs.lti.ark.fn.parsing.FEDict;
import edu.cmu.cs.lti.ark.util.XmlUtils;
import edu.cmu.cs.lti.ark.util.ds.Range;
import edu.cmu.cs.lti.ark.util.ds.Range0Based;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alex Kabbach
 */
public class ScoreWithGoldFrames {

	private static final Logger logger = LoggerFactory
			.getLogger(ScoreWithGoldFrames.class);

	private static final String TAB = "\t";
	private static final int SENTENCE_FIELD = 7;

	public static void main(String[] args) throws IOException {
		final String depParsedSplitsPath = args[0];
		final String frameSplitsPath = args[1];
		final String alphabetFilename = args[2];
		final String frameElementMapFilename = args[3];
		final String argModelFilename = args[4];
		final int kBest = Integer.parseInt(args[5]);
		final String outputXmlFilePath = args[6];

		ScoreWithGoldFrames score = new ScoreWithGoldFrames();

		List<Sentence> sentences = score.getSentences(depParsedSplitsPath);
		List<String> frameSplits = score.getFrameSplits(frameSplitsPath);
		Map<Integer, List<String>> frameSplitsMap = score
				.getFrameSplitsMap(frameSplits);
		final Map<String, Integer> argIdFeatureIndex = DataPrep
				.readFeatureIndex(new File(alphabetFilename));
		final FEDict feDict = FEDict.fromFile(frameElementMapFilename);
		final Decoding decoder = Decoding
				.fromFile(argModelFilename, alphabetFilename);
		List<String> tokenizedSplits = score.getTokenizedSplits(sentences);
		List<String> depParsedSplits = score.getDepParsedSplits(sentences);
		Range0Based range =
				new Range0Based(0, sentences.size(), false);

		score.runWithGoldFrames(sentences, frameSplitsMap, argIdFeatureIndex,
				feDict, decoder, kBest, depParsedSplits,
				tokenizedSplits, range, outputXmlFilePath);
	}

	private void runWithGoldFrames(List<Sentence> sentences,
			Map<Integer, List<String>> frameSplitsMap,
			Map<String, Integer> argIdFeatureIndex, FEDict feDict,
			Decoding decoder, int kBest, List<String> depParsedSplits,
			List<String> tokenizedSplits, Range range,
			String outputXmlFilePath) throws IOException {
		List<String> predictedFESplits = StaticSemafor
				.predictAllArguments(sentences, frameSplitsMap,
						argIdFeatureIndex, feDict, decoder, kBest);
		Document outputDoc = PrepareFullAnnotationXML
				.createXMLDoc(predictedFESplits, range, depParsedSplits,
						tokenizedSplits);
		XmlUtils.writeXML(outputXmlFilePath, outputDoc);
	}

	private List<Sentence> getSentences(String depParsedSplitsPath)
			throws FileNotFoundException {
		File depParsedSplitsFile = new File(depParsedSplitsPath);
		SentenceCodec.SentenceIterator sentenceIterator =
				new SentenceCodec.SentenceIterator(
				SentenceCodec.ConllCodec, depParsedSplitsFile);
		return Lists.newArrayList(sentenceIterator);
	}

	private List<String> getTokenizedSplits(List<Sentence> sentences) {
		return sentences.stream().map(sentence -> String.join(" ",
				sentence.getTokens().stream().map(token -> token.getForm())
						.collect(Collectors.toList())))
				.collect(Collectors.toList());
	}

	private List<String> getDepParsedSplits(List<Sentence> sentences) {
		return sentences.stream().map(sentence -> AllLemmaTags
				.makeLine(sentence.toAllLemmaTagsArray()))
				.collect(Collectors.toList());
	}

	private Map<Integer, List<String>> getFrameSplitsMap(List<String> frameSplits) {
		return frameSplits
				.stream()
				.collect(Collectors
						.groupingBy(frameSplit ->
								Integer.parseInt(frameSplit
										.split(TAB)[SENTENCE_FIELD])));
	}

	private List<String> getFrameSplits(String frameSplitsPath)
			throws IOException {
		return Files.lines(Paths.get(frameSplitsPath))
				.collect(Collectors.toList());
	}
}
