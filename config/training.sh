#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/preprocessing.sh"

# You should not need to change anything here
export EVENT_DIR="${MODEL_DIR}/events" # a temp directory where training events will be stored
export log_file="${MODEL_DIR}/log"
export frame_id_alphabet="${MODEL_DIR}/alphabet.dat"
export frame_id_model="${MODEL_DIR}/idmodel.dat"
export id_features="ancestor"
export train_events="cv.train.events.bin"
export arg_id_alphabet="parser.conf"
export spans_file="cv.train.sentences.frame.elements.spans "
export feature_cache="featurecache.jobj"
export arg_id_model="${MODEL_DIR}/argmodel.dat"

fe_file_length=`wc -l ${training_fe_splits}`
fe_file_length=`expr ${fe_file_length% *}`
export fe_file_length

