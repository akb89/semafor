#!/usr/bin/env bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

# You should not need to change anything here

export RESOURCES_DIR="${SEMAFOR_HOME}/resources/data"
export LEXUNIT_DIR="${FRAMENET_DATA_DIR}/lu"

export tokenizer_sed="${SEMAFOR_HOME}/scripts/tokenizer.sed"

export POS_TAGGER_HOME="${SEMAFOR_HOME}/scripts/jmx"
export MST_PARSER_HOME="${SEMAFOR_HOME}/scripts/mstparser"
export MALT_PARSER_HOME="${SEMAFOR_HOME}/scripts/maltparser-1.7.2"

export mst_parser_model="${RESOURCES_DIR}/wsj.model"
export malt_parser_model="engmalt.linear-1.7"

export test_set_documents_names="${RESOURCES_DIR}/fn.fulltext.test.set.documents"

export tmp_file="${EXPERIMENT_DATA_DIR}/tmp"

export training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences"
export testing_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences"

export training_fe_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.frame.elements"
export testing_fe_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.frame.elements"

export tokenized_training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.tokenized"
export tokenized_testing_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.tokenized"

export postagged_training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.pos.tagged"
export postagged_testing_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.pos.tagged"

export malt_conll_input_training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.malt.input.conll"
export matl_conll_input_testing_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.malt.input.conll"

export maltparsed_training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.maltparsed.conll"
export maltparsed_testing_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.maltparsed.conll"

export mst_conll_input_training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.mst.input.conll"
export mst_conll_input_testing_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.mst.input.conll"

export mstparsed_training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.mstparsed.conll"
export mstparsed_testing_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.mstparsed.conll"

export all_lemma_tags_training_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.all.lemma.tags"
export all_lemma_tags_testing_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.all.lemma.tags"

export framenet_lu_map_file="${MODEL_DIR}/framenet.original.map"
export framenet_fe_map_file="${MODEL_DIR}/framenet.frame.element.map"

export wordnet_config_file="${RESOURCES_DIR}/file_properties.xml"
export stopwords_file="${RESOURCES_DIR}/stopwords.txt"
export all_related_words_file="${RESOURCES_DIR}/allrelatedwords.ser"
export hv_correspondence_file="${RESOURCES_DIR}/hvmap.ser"
export wn_related_words_for_words_file="${RESOURCES_DIR}/wnallrelwords.ser"
export wn_map_file="${RESOURCES_DIR}/wnMap.ser"
export revised_map_file="${RESOURCES_DIR}/revisedrelmap.ser"
export lemma_cache_file="${RESOURCES_DIR}/hvlemmas.ser"

export fn_id_req_data_file="${MODEL_DIR}/reqData.jobj"