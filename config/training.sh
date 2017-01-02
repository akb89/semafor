#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

#export id_features="ancestor"

#export OLD_MODEL_DIR="${MALT_MODEL_DIR}"

#export fn_id_req_data_file="${MODEL_DIR}/reqData.jobj"

# paths to the training data
# TODO: remove this line paths to the gold-standard annotated sentences, and dependency-parsed version of it
#export TRAINING_DIR="${DATA_DIR}/training"
export fe_file="${DATA_DIR}/cv.train.sentences.frame.elements"
export parsed_file="${DATA_DIR}/cv.train.sentences.all.lemma.tags"
export fe_file_length=`wc -l ${fe_file}`
export fe_file_length=`expr ${fe_file_length% *}`

# path to store the alphabet we create:
#export alphabet_file="${MODEL_DIR}/alphabet.dat"

export SCAN_DIR="${MODEL_DIR}/scan"

echo num_threads="${num_threads}"
echo gc_threads="${gc_threads}"
echo DATA_DIR="${DATA_DIR}"
#echo id_features="${id_features}"
#echo fn_id_req_data_file="${fn_id_req_data_file}"
#echo TRAINING_DIR="${TRAINING_DIR}"
echo fe_file="${fe_file}"
echo parsed_file="${parsed_file}"
echo fe_file_length="${fe_file_length}"
#echo alphabet_file="${alphabet_file}"
echo SCAN_DIR="${SCAN_DIR}"
