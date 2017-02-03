#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

export fe_file="${DATA_DIR}/cv.train.sentences.frame.elements"
export parsed_file="${DATA_DIR}/cv.train.sentences.all.lemma.tags"
export fe_file_length=`wc -l ${fe_file}`
export fe_file_length=`expr ${fe_file_length% *}`
export SCAN_DIR="${MODEL_DIR}/scan"

echo num_threads="${num_threads}"
echo gc_threads="${gc_threads}"
echo DATA_DIR="${DATA_DIR}"
echo fe_file="${fe_file}"
echo parsed_file="${parsed_file}"
echo fe_file_length="${fe_file_length}"
echo SCAN_DIR="${SCAN_DIR}"
