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
    edu.clcl.fn.data.prep.FNSplitsDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${training_splits}" \
    "${testing_splits}" \
    "${test_set_documents_names}"
echo "Finished sentences splits generation"
echo