#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.***.sentences.frame.elements from cv.***.sentences splits
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.FFESplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${testing_sentence_splits}" \
    "${testing_tokenized_sentence_splits}" \
    "${training_sentence_splits}" \
    "${training_tokenized_sentence_splits}" \
    "${test_set_documents_names}"\
    "${training_fe_splits}"\
    "${testing_fe_splits}" \
    "true" \
    "${with_exemplars}"