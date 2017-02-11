#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

# You should not need to change anything here
export RESULTS_DIR="${EXPERIMENTS_DIR}/results"

export sentences_frames="${EXPERIMENT_DATA_DIR}/cv.test.sentences.frames"
export sentences_maltparsed="${EXPERIMENT_DATA_DIR}/cv.test.sentences.maltparsed.conll"
export processed_file="${EXPERIMENT_DATA_DIR}/cv.test.sentences.all.lemma.tags"
export tokenized_file="${EXPERIMENT_DATA_DIR}/cv.test.sentences.tokenized"
export fe_file="${EXPERIMENT_DATA_DIR}/cv.test.sentences.frame.elements"
export frames_single_file="${EXPERIMENT_DATA_DIR}/frames.xml"
export relation_modified_file="${EXPERIMENT_DATA_DIR}/frRelations.xml"