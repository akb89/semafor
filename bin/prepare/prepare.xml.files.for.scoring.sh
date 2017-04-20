#!/bin/bash

# Create files frames.xml and feRelations.xml for use with perl score script under the EXPERIMENT_DATA_DIR directory
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.data.prep.ScoringRequiredDataCreation \
    "${FRAMENET_DATA_DIR}" \
    "${EXPERIMENT_DATA_DIR}"