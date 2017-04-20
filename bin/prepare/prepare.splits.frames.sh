#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Generate cv.***.sentences.frames from cv.***.sentences splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.FFESplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${testing_sentence_splits}" \
    "${tokenized_testing_sentence_splits}" \
    "${training_sentence_splits}" \
    "${tokenized_training_sentence_splits}" \
    "${test_set_documents_names}"\
    "${training_frame_splits}"\
    "${testing_frame_splits}" \
    "false" \
    "${with_exemplars}"