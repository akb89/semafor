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

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
echo "**********************************************************************"
echo "Creating framenet.original.map and framenet.frame.element.map..."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.TrainingMapsCreation \
    "${training_fe_splits}" \
    "${postagged_training_sentence_splits}" \
    "${framenet_lu_map_file}" \
    "${framenet_fe_map_file}"
echo "Finished maps creation"
echo