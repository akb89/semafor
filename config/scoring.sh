#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/training.sh"

# You should not need to change anything here
export RESULTS_DIR="${EXPERIMENTS_DIR}/results"

export frames_single_file="${EXPERIMENT_DATA_DIR}/frames.xml"
export relation_modified_file="${EXPERIMENT_DATA_DIR}/frRelations.xml"

end_index_prepare_fullanno_xml=`wc -l "${testing_tokenized_sentence_splits}"`
end_index_prepare_fullanno_xml=`expr ${end% *}`
export end_index_prepare_fullanno_xml

export scoring_predicted_goldframe_xml_file="${RESULTS_DIR}/test.predicted.goldframe.xml"
export scoring_gold_xml_file="${RESULTS_DIR}/test.gold.xml"
export scoring_output_text_file="${RESULTS_DIR}/arg_test_exact_verbose_${lambda}"