#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.malt.input.conll splits
echo "Running MaltParser on conll training splits..."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${malt_conll_input_training_sentence_splits} \
    -o ${maltparsed_training_sentence_splits}
echo "Finished Malt dependency parsing"
echo
echo "Running MaltParser on conll testing splits..."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${malt_conll_input_testing_sentence_splits} \
    -o ${maltparsed_testing_sentence_splits}
echo "Finished Malt dependency parsing"
echo