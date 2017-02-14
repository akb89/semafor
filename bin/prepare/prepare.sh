#!/usr/bin/env bash
set -x # echo commands
set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

echo
echo "Creating Required Data"
echo

rm -rf ${MODEL_DIR}
mkdir -p "${MODEL_DIR}"
rm -rf ${EXPERIMENT_DATA_DIR}
mkdir ${EXPERIMENT_DATA_DIR}

# TODO: Generate also cv.***.frames splits and cv.***.frame.elements splits
# Generate cv.***.sentences splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
echo "**********************************************************************"
echo "Generating training and testing sentences splits from FrameNet XML data..."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.FNSplitsDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_splits}" \
    "${testing_splits}" \
    "${test_set_documents_names}"
echo "Finished sentences splits generation"
echo

# Generate cv.***.sentences.tokenized splits from cv.***.sentences splits
echo "**********************************************************************"
echo "Tokenizing training splits: ${training_splits} ..."
time sed -f ${tokenizer_sed} ${training_splits} > ${tokenized_training_splits}
echo "Finished tokenization."
echo
echo "**********************************************************************"
echo "Tokenizing testing splits: ${testing_splits} ..."
time sed -f ${tokenizer_sed} ${testing_splits} > ${tokenized_testing_splits}
echo "Finished tokenization"
echo

# Generate cv.***.sentences.pos.tagged splits from cv.***.sentences splits
echo "**********************************************************************"
echo "Part-of-speech tagging tokenized training splits...."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${tokenized_training_splits} > ${postagged_training_splits}
echo "Finished part-of-speech tagging"
echo
echo "**********************************************************************"
echo "Part-of-speech tagging tokenized testing splits...."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${tokenized_testing_splits} > ${postagged_testing_splits}
echo "Finished part-of-speech tagging"
echo

# Generate cv.***.sentences.malt.input.conll splits from cv.***.sentences.pos.tagged splits
echo "**********************************************************************"
echo "Converting postagged training splits to Malt conll input..."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_training_splits} \
    --inputFormat pos \
    --output ${malt_conll_input_training_splits} \
    --outputFormat conll
echo "Done converting postagged input to conll"
echo
echo "**********************************************************************"
echo "Converting postagged testing splits to Malt conll input..."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_testing_splits} \
    --inputFormat pos \
    --output ${matl_conll_input_testing_splits} \
    --outputFormat conll
echo "Done converting postagged input to conll"
echo

# Generate cv.***.sentences.mst.input.conll splits from cv.***.sentences.pos.tagged splits
echo "**********************************************************************"
echo "Converting postagged training splits to MST conll input..."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${postagged_training_splits} ${mst_conll_input_training_splits}
echo
echo "**********************************************************************"
echo "Converting postagged testing splits to MST conll input..."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${postagged_testing_splits} ${mst_conll_input_testing_splits}

# Generate cv.***.sentences.mstparsed.conll splits from cv.***.sentences.mst.input.conll splits
echo "**********************************************************************"
echo "Running MSTParser on conll training splits...."
pushd ${MST_PARSER_HOME}
time ${JAVA_HOME_BIN}/java -classpath ".:./lib/trove.jar:./lib/mallet-deps.jar:./lib/mallet.jar" \
	-Xms8g -Xmx${max_ram} mst.DependencyParser \
	test separate-lab \
	model-name:${mst_parser_model} \
	decode-type:proj order:2 \
	test-file:${mst_conll_input_training_splits} \
	output-file:${mstparsed_training_splits} \
	format:CONLL
echo "Finished MST dependency parsing"
echo
echo "**********************************************************************"
echo "Running MSTParser on conll testing splits...."
pushd ${MST_PARSER_HOME}
time ${JAVA_HOME_BIN}/java -classpath ".:./lib/trove.jar:./lib/mallet-deps.jar:./lib/mallet.jar" \
	-Xms8g -Xmx${max_ram} mst.DependencyParser \
	test separate-lab \
	model-name:${mst_parser_model} \
	decode-type:proj order:2 \
	test-file:${mst_conll_input_testing_splits} \
	output-file:${mstparsed_testing_splits} \
	format:CONLL
echo "Finished MST dependency parsing"
echo

# Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.malt.input.conll splits
echo "**********************************************************************"
echo "Running MaltParser on conll training splits...."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${malt_conll_input_training_splits} \
    -o ${maltparsed_training_splits}
echo "Finished Malt dependency parsing"
echo
echo "**********************************************************************"
echo "Running MaltParser on conll testing splits...."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${matl_conll_input_testing_splits} \
    -o ${maltparsed_testing_splits}
echo "Finished Malt dependency parsing"
echo

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.TrainingRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"

# Create the file reqData.jobj under the MODEL_DIR directory
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.RequiredDataCreation \
    stopwords-file:${stopwords_file} \
    wordnet-configfile:${wordnet_config_file} \
    framenet-mapfile:${framenet_map_file} \
    luxmldir:${LEXUNIT_DIR} \
    allrelatedwordsfile:${all_related_words_file} \
    hvcorrespondencefile:${hv_correspondence_file} \
    wnrelatedwordsforwordsfile:${wn_related_words_for_words_file} \
    wnmapfile:${wn_map_file} \
    revisedmapfile:${revised_map_file} \
    lemmacachefile:${lemma_cache_file} \
    fnidreqdatafile:${fn_id_req_data_file}

# Create files frames.xml and feRelations.xml for use with perl score script under the EXPERIMENT_DATA_DIR directory
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.ScoringRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"