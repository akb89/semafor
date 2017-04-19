#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

# You should not need to change anything here
export fe_file="${EXPERIMENT_DATA_DIR}/cv.train.sentences.frame.elements"
export parsed_file="${EXPERIMENT_DATA_DIR}/cv.train.sentences.all.lemma.tags"
export fe_file_length=`wc -l ${fe_file}`
export fe_file_length=`expr ${fe_file_length% *}`
export SCAN_DIR="${MODEL_DIR}/scan"
export id_features="ancestor"
export EVENT_DIR="${MODEL_DIR}/events" # a temp directory where training events will be stored
export log_file="${MODEL_DIR}/log"

echo num_threads="${num_threads}"
echo gc_threads="${gc_threads}"
echo EXPERIMENT_DATA_DIR="${EXPERIMENT_DATA_DIR}"
echo fe_file="${fe_file}"
echo parsed_file="${parsed_file}"
echo fe_file_length="${fe_file_length}"
echo SCAN_DIR="${SCAN_DIR}"

