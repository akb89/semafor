/*******************************************************************************
 * Copyright (c) 2012 Dipanjan Das 
 * Language Technologies Institute, 
 * Carnegie Mellon University, 
 * All Rights Reserved.
 * 
 * CommandLineOptions.java is part of SEMAFOR 2.1.
 * 
 * SEMAFOR 2.1 is free software: you can redistribute it and/or modify  it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or 
 * (at your option) any later version.
 * 
 * SEMAFOR 2.1 is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. 
 * 
 * You should have received a copy of the GNU General Public License along
 * with SEMAFOR 2.1.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package edu.cmu.cs.lti.ark.fn.utils;

import edu.cmu.cs.lti.ark.util.CommandLineOptions;

/**
 * A hodgepodge of command line options for various models used in training/testing the frame structure parser. 
 * Should probably be refactored into several separate classes
 * @author Nathan Schneider (nschneid)
 * @since 2009-09-25
 */
public final class FNModelOptions extends CommandLineOptions {
	public StringOption alphabetFile = new StringOption("alphabet_file");
	public BoolOption train = new BoolOption("train");
	public BoolOption dev = new BoolOption("dev");
	public BoolOption test = new BoolOption("test");
	public StringOption trainParseFile = new StringOption("train_parse_file");
	public StringOption trainFrameFile = new StringOption("train_frame_file");
	public StringOption trainFrameElementFile = new StringOption("train_fe_file");
	public StringOption reg = new StringOption("regularization");
	public DoubleOption lambda = new DoubleOption("lambda");
	public StringOption frameNetMapFile = new StringOption("framenet_lu_map_file");
	public ExistingPathOption wnConfigFile = new ExistingPathOption("wordnet_config_file");
	public ExistingPathOption stopWordsFile = new ExistingPathOption("stopwords_file");
	public StringOption testFrameFile = new StringOption("test_frame_file");
	public StringOption testParseFile = new StringOption("test_parse_file");
	public StringOption modelFile = new StringOption("model");
	public StringOption warmStartModelFile = new StringOption("warm_start_model");
	public IntOption saveEveryKBatches = new IntOption("save_every_k_batches");
	public IntOption numModelsToSave = new IntOption("num_models_to_save");
	public IntOption memory = new IntOption("memory");
	public IntOption startIndex = new IntOption("start_index");
	public IntOption endIndex = new IntOption("end_index");
	public ExistingPathOption testTokenizedFile = new ExistingPathOption("test_tokenized_file");
	public StringOption allRelatedWordsFile = new StringOption("all_related_words_file");
	public StringOption wnRelatedWordsForWordsFile = new StringOption("wn_related_words_for_words_file");
	public StringOption wnMapFile = new StringOption("wn_map_file");
	public StringOption hvCorrespondenceFile = new StringOption("hv_correspondence_file");
	public StringOption fnIdReqDataFile = new StringOption("fn_id_req_data_file");
	public ExistingPathOption idParamFile = new ExistingPathOption("id_model_file");
	public ExistingPathOption luXmlDir = new ExistingPathOption("lexunit_xml_dir");
	public NewFilePathOption frameElementsOutputFile = new NewFilePathOption("frame_elements_output_file");
	public NewFilePathOption logOutputFile = new NewFilePathOption("log_output_file");
	public IntOption minimumCount = new IntOption("minimum_count");
	public IntOption numThreads = new IntOption("num_threads");
	public IntOption batchSize = new PositiveIntOption("batch_size");
	public BoolOption usePartialCredit = new BoolOption("use_partial_credit");
	public DoubleOption costMultiple = new DoubleOption("cost_multiple");
	public ExistingPathOption inputFile = new ExistingPathOption("input_file");
	public NewFilePathOption outputFile = new NewFilePathOption("output_file");
	public StringOption idFeatureExtractorType = new StringOption("id_feature_extractor_type");
	public StringOption eventsFile = new StringOption("events_file");
	public StringOption spansFile = new StringOption("spans_file");
	public StringOption frameFeaturesCacheFile = new StringOption("local_features_cache");
	public StringOption restartFile = new StringOption("restart_file");
	public StringOption lemmaCacheFile = new StringOption("lemma_cache_file");
	public StringOption revisedMapFile = new StringOption("revised_map_file");
	public StringOption useGraph = new StringOption("use_graph");
	public IntOption port = new IntOption("port");
	public StringOption modelDirectory = new StringOption("model_dir");
	public PositiveIntOption kBestOutput = new PositiveIntOption("k_best_output");
	public FNModelOptions(String[] args) {
		this(args, false);
	}
	public FNModelOptions(String[] args, boolean ignoreUnknownOptions) {
		super();
		init(args, ignoreUnknownOptions);
	}
}
