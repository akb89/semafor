package edu.unige.clcl.fn.score;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import edu.cmu.cs.lti.ark.fn.data.prep.formats.Sentence;
import edu.cmu.cs.lti.ark.fn.parsing.*;
import edu.cmu.cs.lti.ark.fn.utils.DataPointWithFrameElements;
import edu.cmu.cs.lti.ark.util.ds.Range0Based;
import edu.cmu.cs.lti.ark.util.nlp.parse.DependencyParse;
import edu.cmu.cs.lti.ark.util.nlp.parse.DependencyParses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Alex Kabbach
 */
public class StaticSemafor {

	private static final Logger logger = LoggerFactory.getLogger(
			StaticSemafor.class);

	private static int[] convertToIdxs(Map<String, Integer> argIdFeatureIndex,
									   Iterable<String> featureSet) {
		// convert feature names to feature indexes
		final List<Integer> featureList = Lists.newArrayList();
		for (String feature : featureSet) {
			final Integer idx = argIdFeatureIndex.get(feature);
			if (idx != null) {
				featureList.add(idx);
			}
		}
		return Ints.toArray(featureList);
	}

	private static List<String> predictArgumentLines(Sentence sentence,
													 List<String> idResults,
													 Map<String, Integer> argIdFeatureIndex,
													 FEDict feDict,
													 Decoding decoder,
													 int kBest)
			throws IOException {
		System.out.println("Processing sentence: " + sentence.getTokens().toString());
		final List<FrameFeatures> frameFeaturesList = Lists.newArrayList();
		final FeatureExtractor featureExtractor = new FeatureExtractor();
		for (String feLine : idResults) {
			System.out.println("feLine = " + feLine);
			final DataPointWithFrameElements dataPoint = new DataPointWithFrameElements(
					sentence, feLine);
			System.out.println("dataPoint = " + dataPoint.getFrameElementsAndSpans().toString());
			final String frame = dataPoint.getFrameName();
			final DependencyParses parses = dataPoint.getParses();
			final int targetStartTokenIdx = dataPoint.getTargetTokenIdxs()[0];
			final int targetEndTokenIdx = dataPoint.getTargetTokenIdxs()[
					dataPoint.getTargetTokenIdxs().length - 1];
			final List<DataPrep.SpanAndParseIdx> spans = DataPrep.findSpans(
					dataPoint, 1);
			final List<String> frameElements = Lists.newArrayList(
					feDict.lookupFrameElements(frame));
			final List<SpanAndCorrespondingFeatures[]> featuresAndSpanByArgument = Lists
					.newArrayList();
			for (String frameElement : frameElements) {
				final List<SpanAndCorrespondingFeatures> spansAndFeatures = Lists
						.newArrayList();
				for (DataPrep.SpanAndParseIdx candidateSpanAndParseIdx : spans) {
					final Range0Based span = candidateSpanAndParseIdx.span;
					final DependencyParse parse = parses.get(
							candidateSpanAndParseIdx.parseIdx);
					final Set<String> featureSet = featureExtractor
							.extractFeatures(dataPoint, frame, frameElement,
											 span, parse).elementSet();
					final int[] featArray = convertToIdxs(argIdFeatureIndex,
														  featureSet);
					spansAndFeatures.add(new SpanAndCorrespondingFeatures(
							new int[] { span.start, span.end }, featArray));
				}
				featuresAndSpanByArgument.add(spansAndFeatures.toArray(
						new SpanAndCorrespondingFeatures[spansAndFeatures
								.size()]));
			}
			frameFeaturesList.add(new FrameFeatures(frame, targetStartTokenIdx,
													targetEndTokenIdx,
													frameElements,
													featuresAndSpanByArgument));
		}
		return decoder.decodeAll(frameFeaturesList, idResults, 0, kBest);
	}

	private static List<String> predictArgsForSentence(Sentence sentence,
													   List<String> frameSplits,
													   Map<String, Integer> argIdFeatureIndex,
													   FEDict feDict,
													   Decoding decoder,
													   int kBest)
			throws IOException {
		return predictArgumentLines(sentence, frameSplits, argIdFeatureIndex,
									feDict, decoder, kBest);
	}

	public static List<String> predictAllArguments(List<Sentence> sentences,
												   Map<Integer, List<String>> frameSplitsMap,
												   Map<String, Integer> argIdFeatureIndex,
												   FEDict feDict,
												   Decoding decoder, int kBest)
			throws IOException {
		List<String> allPredictedArgs = new ArrayList<>();
		logger.info("sentences.size = " + sentences.size());
		logger.info("frameSplitsMap.size = " + frameSplitsMap.size());
		int counter = 0;
		for (int i = 0; i < sentences.size(); i++) {
			System.out.println("Sentence at i = " + i);
			Sentence sentence = sentences.get(i);
			if (frameSplitsMap.containsKey(i)) {
				List<String> frameSplits = frameSplitsMap.get(i);
				List<String> predictedArgs = predictArgsForSentence(sentence,
																	frameSplits,
																	argIdFeatureIndex,
																	feDict,
																	decoder,
																	kBest);
				allPredictedArgs.addAll(predictedArgs);
			} else {
				counter += 1;
			}
		}
		logger.info(
				"There are " + counter + " sentences without frame annotation");
		return allPredictedArgs;
	}
}
