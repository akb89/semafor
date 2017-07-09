#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.TrainingMapsCreation \
    "${training_fe_splits_with_exemplars}" \
    "${training_postagged_sentence_splits_with_exemplars}" \
    "${framenet_lu_map_file}" \
    "${old_framenet_lu_map_file}" \
    "${framenet_fe_map_file}" \
    "${old_framenet_fe_map_file}"