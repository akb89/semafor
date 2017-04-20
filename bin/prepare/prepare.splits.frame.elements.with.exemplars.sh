#!/bin/bash

# Generate cv.***.sentences.frame.elements from cv.***.sentences splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.FFESplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${testing_sentence_splits_with_exemplars}" \
    "${testing_tokenized_sentence_splits_with_exemplars}" \
    "${training_sentence_splits_with_exemplars}" \
    "${training_tokenized_sentence_splits_with_exemplars}" \
    "${test_set_documents_names}"\
    "${training_fe_splits_with_exemplars}"\
    "${testing_fe_splits_with_exemplars}" \
    "true" \
    "true"