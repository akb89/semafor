#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Create the file reqData.jobj under the MODEL_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.RequiredDataCreation \
    stopwords_file:${stopwords_file} \
    wordnet_config_file:${wordnet_config_file} \
    framenet_lu_map_file:${framenet_lu_map_file} \
    lexunit_xml_dir:${LEXUNIT_DIR} \
    all_related_words_file:${all_related_words_file} \
    hv_correspondence_file:${hv_correspondence_file} \
    wn_related_words_for_words_file:${wn_related_words_for_words_file} \
    wn_map_file:${wn_map_file} \
    revised_map_file:${revised_map_file} \
    lemma_cache_file:${lemma_cache_file} \
    fn_id_req_data_file:${fn_id_req_data_file}