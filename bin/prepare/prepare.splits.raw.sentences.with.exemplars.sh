#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.***.sentences.with.exemplars splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
echo "Generating training and testing sentences splits from FrameNet XML data with exemplars TRUE..."
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.SentenceSplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_sentence_splits_with_exemplars}" \
    "${testing_sentence_splits_with_exemplars}" \
    "${test_set_documents_names}" \
    "true" >> ${LOGS_DIR}/prepare.log # Redirect stout to log file
echo "Done generating training and testing sentences splits"
echo