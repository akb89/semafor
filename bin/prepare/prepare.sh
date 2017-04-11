#!/usr/bin/env bash
set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

echo
echo "Creating Required Data"
echo

rm -rf ${MODEL_DIR}
mkdir -p "${MODEL_DIR}"
rm -rf ${EXPERIMENT_DATA_DIR}
mkdir ${EXPERIMENT_DATA_DIR}

# Generate cv.***.sentences splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.SentenceSplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_sentence_splits}" \
    "${testing_sentence_splits}" \
    "${test_set_documents_names}" \
    "${with_exemplars}" \

# Generate cv.***.sentences.tokenized splits from cv.***.sentences splits
echo "Tokenizing training splits: ${training_sentence_splits} ..."
time sed -f ${tokenizer_sed} ${training_sentence_splits} > ${tokenized_training_sentence_splits}
echo "Finished tokenization."
echo
echo "Tokenizing testing splits: ${testing_sentence_splits} ..."
time sed -f ${tokenizer_sed} ${testing_sentence_splits} > ${tokenized_testing_sentence_splits}
echo "Finished tokenization"
echo

# Generate cv.***.sentences.frame.elements from cv.***.sentences splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.FESplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${testing_sentence_splits}" \
    "${tokenized_testing_sentence_splits}" \
    "${training_sentence_splits}" \
    "${tokenized_training_sentence_splits}" \
    "${test_set_documents_names}"\
    "${training_fe_splits}"\
    "${testing_fe_splits}" \
    "${with_exemplars}"

# Generate cv.***.sentences.pos.tagged splits from cv.***.sentences splits
echo "Part-of-speech tagging tokenized training splits..."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${tokenized_training_sentence_splits} > ${postagged_training_sentence_splits}
echo "Finished part-of-speech tagging"
echo
echo "Part-of-speech tagging tokenized testing splits..."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${tokenized_testing_sentence_splits} > ${postagged_testing_sentence_splits}
echo "Finished part-of-speech tagging"
echo

# Generate cv.***.sentences.malt.input.conll splits from cv.***.sentences.pos.tagged splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_training_sentence_splits} \
    --inputFormat pos \
    --output ${malt_conll_input_training_sentence_splits} \
    --outputFormat conll

time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_testing_sentence_splits} \
    --inputFormat pos \
    --output ${malt_conll_input_testing_sentence_splits} \
    --outputFormat conll

# Generate cv.***.sentences.mst.input.conll splits from cv.***.sentences.pos.tagged splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${postagged_training_sentence_splits} ${mst_conll_input_training_sentence_splits}

time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${postagged_testing_sentence_splits} ${mst_conll_input_testing_sentence_splits}

# Generate cv.***.sentences.mstparsed.conll splits from cv.***.sentences.mst.input.conll splits
echo "Running MSTParser on conll training splits..."
pushd ${MST_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -classpath ".:./lib/trove.jar:./lib/mallet-deps.jar:./lib/mallet.jar" \
	-Xms${min_ram} \
	-Xmx${max_ram} \
	mst.DependencyParser \
	test \
	separate-lab \
	model-name:${mst_parser_model} \
	decode-type:proj \
	order:2 \
	test-file:${mst_conll_input_training_sentence_splits} \
	output-file:${mstparsed_training_sentence_splits} \
	format:CONLL
echo "Finished MST dependency parsing"
echo
echo "Running MSTParser on conll testing splits..."
pushd ${MST_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -classpath ".:./lib/trove.jar:./lib/mallet-deps.jar:./lib/mallet.jar" \
	-Xms${min_ram} \
	-Xmx${max_ram} \
	mst.DependencyParser \
	test \
	separate-lab \
	model-name:${mst_parser_model} \
	decode-type:proj \
	order:2 \
	test-file:${mst_conll_input_testing_sentence_splits} \
	output-file:${mstparsed_testing_sentence_splits} \
	format:CONLL
echo "Finished MST dependency parsing"
echo

# Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.malt.input.conll splits
echo "Running MaltParser on conll training splits..."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${malt_conll_input_training_sentence_splits} \
    -o ${maltparsed_training_sentence_splits}
echo "Finished Malt dependency parsing"
echo
echo "Running MaltParser on conll testing splits..."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${matl_conll_input_testing_sentence_splits} \
    -o ${maltparsed_testing_sentence_splits}
echo "Finished Malt dependency parsing"
echo

# Generate cv.train.sentences.all.lemma.tags
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
    ${tokenized_training_sentence_splits} \
    ${mstparsed_training_sentence_splits} \
    ${tmp_file} \
    ${all_lemma_tags_training_sentence_splits}
rm "${tmp_file}"

# Generate cv.test.sentences.all.lemma.tags
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
    ${tokenized_testing_sentence_splits} \
    ${mstparsed_testing_sentence_splits} \
    ${tmp_file} \
    ${all_lemma_tags_testing_sentence_splits}
rm "${tmp_file}"

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.TrainingMapsCreation \
    "${training_fe_splits}" \
    "${postagged_training_sentence_splits}" \
    "${framenet_lu_map_file}" \
    "${old_framenet_lu_map_file}" \
    "${framenet_fe_map_file}" \
    "${old_framenet_fe_map_file}"

# Create the file reqData.jobj under the MODEL_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.RequiredDataCreation \
    stopwords-file:${stopwords_file} \
    wordnet-configfile:${wordnet_config_file} \
    framenet-mapfile:${framenet_lu_map_file} \
    luxmldir:${LEXUNIT_DIR} \
    allrelatedwordsfile:${all_related_words_file} \
    hvcorrespondencefile:${hv_correspondence_file} \
    wnrelatedwordsforwordsfile:${wn_related_words_for_words_file} \
    wnmapfile:${wn_map_file} \
    revisedmapfile:${revised_map_file} \
    lemmacachefile:${lemma_cache_file} \
    fnidreqdatafile:${fn_id_req_data_file}

# Create files frames.xml and feRelations.xml for use with perl score script under the EXPERIMENT_DATA_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.ScoringRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"