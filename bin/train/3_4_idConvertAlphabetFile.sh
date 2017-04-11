#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../config/training.sh"

# gets the last model file created
model_file="$(ls ${MODEL_DIR}/idmodel.dat_* | sort -r | head -n1)"

echo
echo "Frame Identification -- Step 4: Combining alphabet file with learned params for Frame IDing"
echo "Using model file: ${model_file}"
echo

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.ConvertAlphabetFile \
    ${alphabet_file} \
    ${model_file} \
    ${MODEL_DIR}/idmodel.dat \
    ${id_features}
