#!/bin/bash

# You should not need to change anything below

source "$(dirname "${BASH_SOURCE[0]}")/training.sh"

export RESULTS_DIR="${EXPERIMENTS_DIR}/results"

export frames_single_file="${EXPERIMENT_DATA_DIR}/frames.xml"
export relation_modified_file="${EXPERIMENT_DATA_DIR}/frRelations.xml"

export end_index_prepare_fullanno_xml=`wc -l < "${testing_tokenized_sentence_splits}"`

export scoring_predicted_goldframe_xml_file="${RESULTS_DIR}/test.predicted.goldframe.xml"
export scoring_gold_xml_file="${RESULTS_DIR}/test.gold.xml"
export scoring_output_text_file="${RESULTS_DIR}/arg_test_exact_verbose_${lambda}"