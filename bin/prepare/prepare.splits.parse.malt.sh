#!/bin/bash

# Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.malt.input.conll splits
echo "Running MaltParser on conll training splits..."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${training_malt_conll_input_sentence_splits} \
    -o ${training_sentence_splits_with_dependencies}
echo "Finished Malt dependency parsing"
echo
echo "Running MaltParser on conll testing splits..."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${testing_malt_conll_input_sentence_splits} \
    -o ${testing_sentence_splits_with_dependencies}
echo "Finished Malt dependency parsing"
echo