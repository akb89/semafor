#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

# You should not need to change anything here
export RESOURCES_DIR="${SEMAFOR_HOME}/resources"

export LEXUNIT_DIR="${FRAMENET_DATA_DIR}/lu"

export test_set_documents_names="${RESOURCES_DIR}/fn.fulltext.test.set.documents"

export training_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences"
export testing_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences"

export tokenized_training_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.tokenized"
export tokenized_testing_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.tokenized"

export postagged_training_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.pos.tagged"
export postagged_testing_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.pos.tagged"

export maltparsed_training_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.maltparsed.conll"
export maltparsed_testing_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.maltparsed.conll"

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