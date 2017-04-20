#!/bin/bash

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.TrainingMapsCreation \
    "${training_fe_splits_with_exemplars}" \
    "${postagged_training_sentence_splits_with_exemplars}" \
    "${framenet_lu_map_file}" \
    "${old_framenet_lu_map_file}" \
    "${framenet_fe_map_file}" \
    "${old_framenet_fe_map_file}"