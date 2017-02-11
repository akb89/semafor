#!/bin/bash

set -e # fail fast

source "$(dirname ${BASH_SOURCE[0]})/config.sh"

echo
echo "Creating Required Data"
echo
mkdir -p "${MODEL_DIR}"


${JAVA_HOME_BIN}/java -classpath ${classpath} -Xms2g -Xmx2g -XX:ParallelGCThreads=2 \
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
