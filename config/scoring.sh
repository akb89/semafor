#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/all.sh"

# the directory the evaluation results will end up in
export RESULTS_DIR="${EXPERIMENTS_DIR}/results"

export sentences_frames="${DATA_DIR}/cv.test.sentences.frames"
export sentences_maltparsed="${DATA_DIR}/cv.test.sentences.maltparsed.conll"
export processed_file="${DATA_DIR}/cv.test.sentences.all.lemma.tags"
export tokenized_file="${DATA_DIR}/cv.test.sentences.tokenized"
export fe_file="${DATA_DIR}/cv.test.sentences.frame.elements"
export frames_single_file="${DATA_DIR}/frames.xml"
export relation_modified_file="${DATA_DIR}/frRelations.xml"