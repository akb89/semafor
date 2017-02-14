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
${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.FNSplitsDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_splits}" \
    "${testing_splits}" \
    "${test_set_documents_names}"

# Generate cv.***.sentences.tokenized splits from cv.***.sentences splits
echo "**********************************************************************"
echo "Tokenizing training splits: ${training_splits}"
time sed -f ${SEMAFOR_HOME}/scripts/tokenizer.sed ${training_splits} > ${tokenized_training_splits}
echo "Finished tokenization."
echo
echo "**********************************************************************"
echo "Tokenizing testing splits: ${testing_splits}"
time sed -f ${SEMAFOR_HOME}/scripts/tokenizer.sed ${testing_splits} > ${tokenized_testing_splits}
echo "Finished tokenization."
echo

# Generate cv.***.sentences.pos.tagged splits from cv.***.sentences splits
echo "**********************************************************************"
echo "Part-of-speech tagging tokenized training splits...."
pushd ${SEMAFOR_HOME}/scripts/jmx
time ./mxpost tagger.project < ${tokenized_training_splits} > ${postagged_training_splits}
echo "Finished part-of-speech tagging."
echo
echo "**********************************************************************"
echo "Part-of-speech tagging tokenized testing splits...."
pushd ${SEMAFOR_HOME}/scripts/jmx
time ./mxpost tagger.project < ${tokenized_testing_splits} > ${postagged_testing_splits}
echo "Finished part-of-speech tagging."
echo

# TODO: fix output format
# Generate cv.***.sentences.conll splits from cv.***.sentences.pos.tagged splits
echo "**********************************************************************"
echo "Converting postagged training splits to conll."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_training_splits} \
    --inputFormat pos \
    --output ${postagged_training_splits}.conll \
    --outputFormat conll
echo "Done converting postagged input to conll."
echo
echo "**********************************************************************"
echo "Converting postagged testing splits to conll."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_testing_splits} \
    --inputFormat pos \
    --output ${postagged_testing_splits}.conll \
    --outputFormat conll
echo "Done converting postagged input to conll."
echo

# TODO: fix pos_tagged.conll
# Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.conll splits
echo "**********************************************************************"
echo "Running MaltParser on conll training splits...."
pushd ${SEMAFOR_HOME}/scripts/maltparser-1.7.2
time ${JAVA_HOME_BIN}/java -Xmx2g \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c engmalt.linear-1.7 \
    -i ${postagged_training_splits}.conll \
    -o ${maltparsed_training_splits}
echo "Finished running MaltParser."
echo
echo "**********************************************************************"
echo "Running MaltParser on conll testing splits...."
pushd ${SEMAFOR_HOME}/scripts/maltparser-1.7.2
time ${JAVA_HOME_BIN}/java -Xmx2g \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c engmalt.linear-1.7 \
    -i ${postagged_testing_splits}.conll \
    -o ${maltparsed_testing_splits}
echo "Finished running MaltParser."
echo

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.TrainingRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"

# Create the file reqData.jobj under the MODEL_DIR directory
${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} -XX:ParallelGCThreads=${gc_threads} \
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
${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.ScoringRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"