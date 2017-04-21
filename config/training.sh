#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/preprocessing.sh"

# You should not need to change anything here
export EVENT_DIR="${MODEL_DIR}/events" # a temp directory where training events will be stored
export log_file="${MODEL_DIR}/log"
export frame_id_alphabet=${MODEL_DIR}/alphabet.dat
export frame_id_model=${MODEL_DIR}/idmodel.dat
export fe_file_length=`wc -l ${training_fe_splits}`
export fe_file_length=`expr ${fe_file_length% *}`
export SCAN_DIR="${MODEL_DIR}/scan"
export id_features="ancestor"
export train_events=${SCAN_DIR}/cv.train.events.bin
export arg_id_alphabet="${SCAN_DIR}/parser.conf.unlabeled"
export spans_file="${SCAN_DIR}/cv.train.sentences.frame.elements.spans "
export feature_cache="${SCAN_DIR}/featurecache.jobj"
export arg_id_model="${MODEL_DIR}/svm.argmodel.dat"

