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

# Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.conll splits
echo "**********************************************************************"
echo "Running MaltParser on conll training splits...."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${malt_conll_input_training_splits} \
    -o ${maltparsed_training_splits}
echo "Finished Malt dependency parsing"
echo
echo "**********************************************************************"
echo "Running MaltParser on conll testing splits...."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java -Xmx${max_ram} \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c ${malt_parser_model} \
    -i ${matl_conll_input_testing_splits} \
    -o ${maltparsed_testing_splits}
echo "Finished Malt dependency parsing"
echo
