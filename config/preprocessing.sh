#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/all.sh"

# You should not need to change anything here

export RESOURCES_DIR="${SEMAFOR_HOME}/resources"
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

export training_sentence_splits_with_exemplars="${EXPERIMENT_DATA_DIR}/cv.train.sentences.with.exemplars"
export testing_sentence_splits_with_exemplars="${EXPERIMENT_DATA_DIR}/cv.test.sentences.with.exemplars"

export training_frame_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.frames"
export testing_frame_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.frames"

export training_fe_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.frame.elements"
export testing_fe_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.frame.elements"

export training_fe_splits_with_exemplars="${EXPERIMENT_DATA_DIR}/cv.train.sentences.with.exemplars.frame.elements"
export testing_fe_splits_with_exemplars="${EXPERIMENT_DATA_DIR}/cv.test.sentences.with.exemplars.frame.elements"

export training_tokenized_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.tokenized"
export testing_tokenized_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.tokenized"

export training_tokenized_sentence_splits_with_exemplars="${EXPERIMENT_DATA_DIR}/cv.train.sentences.with.exemplars.tokenized"
export testing_tokenized_sentence_splits_with_exemplars="${EXPERIMENT_DATA_DIR}/cv.test.sentences.with.exemplars.tokenized"

export training_postagged_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.pos.tagged"
export testing_postagged_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.pos.tagged"

export training_postagged_sentence_splits_with_exemplars="${EXPERIMENT_DATA_DIR}/cv.train.sentences.with.exemplars.pos.tagged"

export training_malt_conll_input_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.malt.input.conll"
export testing_malt_conll_input_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.malt.input.conll"

export training_mst_conll_input_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.mst.input.conll"
export testing_mst_conll_input_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.mst.input.conll"

export training_sentence_splits_with_dependencies="${EXPERIMENT_DATA_DIR}/cv.train.sentences.with.dependencies.conll"
export testing_sentence_splits_with_dependencies="${EXPERIMENT_DATA_DIR}/cv.test.sentences.with.dependencies.conll"

export training_all_lemma_tags_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.train.sentences.all.lemma.tags"
export testing_all_lemma_tags_sentence_splits="${EXPERIMENT_DATA_DIR}/cv.test.sentences.all.lemma.tags"

export framenet_lu_map_file="${MODEL_DIR}/framenet.original.map"
export old_framenet_lu_map_file="${RESOURCES_DIR}/framenet.original.map.old"

export framenet_fe_map_file="${MODEL_DIR}/framenet.frame.element.map"
export old_framenet_fe_map_file="${RESOURCES_DIR}/framenet.frame.element.map.old"

export wordnet_config_file="${RESOURCES_DIR}/file_properties.xml"
export stopwords_file="${RESOURCES_DIR}/stopwords.txt"
export all_related_words_file="${MODEL_DIR}/allrelatedwords.ser"
export hv_correspondence_file="${MODEL_DIR}/hvmap.ser"
export wn_related_words_for_words_file="${MODEL_DIR}/wnallrelwords.ser"
export wn_map_file="${MODEL_DIR}/wnMap.ser"
export revised_map_file="${MODEL_DIR}/revisedrelmap.ser"
export lemma_cache_file="${MODEL_DIR}/hvlemmas.ser"