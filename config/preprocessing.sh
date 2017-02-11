#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

# You should not need to change anything here
export RESOURCES_DIR="${SEMAFOR_HOME}/resources"

export LEXUNIT_DIR="${FRAMENET_DATA_DIR}/lu"

export framenet_map_file="${MODEL_DIR}/framenet.original.map"

export wordnet_config_file="${RESOURCES_DIR}/file_properties.xml"
export stopwords_file="${RESOURCES_DIR}/stopwords.txt"
export all_related_words_file="${RESOURCES_DIR}/allrelatedwords.ser"
export hv_correspondence_file="${RESOURCES_DIR}/hvmap.ser"
export wn_related_words_for_words_file="${RESOURCES_DIR}/wnallrelwords.ser"
export wn_map_file="${RESOURCES_DIR}/wnMap.ser"
export revised_map_file="${RESOURCES_DIR}/revisedrelmap.ser"
export lemma_cache_file="${RESOURCES_DIR}/hvlemmas.ser"

export fn_id_req_data_file="${MODEL_DIR}/reqData.jobj"