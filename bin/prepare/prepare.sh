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

# Generate cv.***.sentences.*** splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.FNDataSplitCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.TrainingRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"

# Create files frames.xml and feRelations.xml for use with perl score script under the EXPERIMENT_DATA_DIR directory
${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.clcl.fn.data.prep.ScoringRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"