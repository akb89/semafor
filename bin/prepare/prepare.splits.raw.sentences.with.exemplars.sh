#!/bin/bash

# Generate cv.***.sentences.with.exemplars splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.SentenceSplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_sentence_splits_with_exemplars}" \
    "${testing_sentence_splits_with_exemplars}" \
    "${test_set_documents_names}" \
    "true"