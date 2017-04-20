#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Generate cv.***.sentences.frame.elements from cv.***.sentences splits
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
    "${training_fe_splits}"\
    "${testing_fe_splits}" \
    "true" \
    "${with_exemplars}"