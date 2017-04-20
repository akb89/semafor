#!/bin/bash

source "$(dirname "${BASH_SOURCE[0]}")/../config/preprocessing.sh"

# You should not need to change anything here
export RESULTS_DIR="${EXPERIMENTS_DIR}/results"

export frames_single_file="${EXPERIMENT_DATA_DIR}/frames.xml"
export relation_modified_file="${EXPERIMENT_DATA_DIR}/frRelations.xml"