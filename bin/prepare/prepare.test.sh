#!/usr/bin/env bash
#set -x # echo commands
set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

echo
echo "Creating Required Data"
echo

#rm -rf ${MODEL_DIR}
#mkdir -p "${MODEL_DIR}"
#rm -rf ${EXPERIMENT_DATA_DIR}
#mkdir ${EXPERIMENT_DATA_DIR}

# Generate cv.***.sentences splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
echo "**********************************************************************"
echo "Generating training and testing sentences splits from FrameNet XML data..."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.SentenceSplitsCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_sentence_splits}" \
    "${testing_sentence_splits}" \
    "${test_set_documents_names}"
echo "Finished sentences splits generation"
echo

# Generate cv.***.sentences.tokenized splits from cv.***.sentences splits
echo "**********************************************************************"
echo "Tokenizing training splits: ${training_sentence_splits} ..."
time sed -f ${tokenizer_sed} ${training_sentence_splits} > ${tokenized_training_sentence_splits}
echo "Finished tokenization."
echo
echo "**********************************************************************"
echo "Tokenizing testing splits: ${testing_sentence_splits} ..."
time sed -f ${tokenizer_sed} ${testing_sentence_splits} > ${tokenized_testing_sentence_splits}
echo "Finished tokenization"
echo