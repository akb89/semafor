#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Generate cv.***.sentences splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.SentenceSplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_sentence_splits}" \
    "${testing_sentence_splits}" \
    "${test_set_documents_names}" \
    "${with_exemplars}"