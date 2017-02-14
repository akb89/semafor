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

# TODO: fix pos_tagged.conll
# Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.conll splits
echo "**********************************************************************"
echo "Running MaltParser on conll training splits...."
pushd ${SEMAFOR_HOME}/scripts/maltparser-1.7.2
time ${JAVA_HOME_BIN}/java -Xmx2g \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c engmalt.linear-1.7 \
    -i ${postagged_training_splits}.conll \
    -o ${maltparsed_training_splits}
echo "Finished running MaltParser."
echo
echo "**********************************************************************"
echo "Running MaltParser on conll testing splits...."
pushd ${SEMAFOR_HOME}/scripts/maltparser-1.7.2
time ${JAVA_HOME_BIN}/java -Xmx2g \
    -jar maltparser-1.7.2.jar \
    -w ${RESOURCES_DIR} \
    -c engmalt.linear-1.7 \
    -i ${postagged_testing_splits}.conll \
    -o ${maltparsed_testing_splits}
echo "Finished running MaltParser."
echo