#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../config/training.sh"

echo
echo "Frame Identification -- Step 1: Creating alphabet"
echo

# a temp directory where training events will be stored
EVENT_DIR="${MODEL_DIR}/events"
mkdir -p "${EVENT_DIR}"

log_file="${MODEL_DIR}/log"
if [ -e ${log_file} ]; then
    rm "${log_file}"
fi

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.AlphabetCreationThreaded \
    train-fefile:${fe_file} \
    train-parsefile:${parsed_file} \
    fnidreqdatafile:${fn_id_req_data_file} \
    logoutputfile:${log_file} \
    model:${MODEL_DIR} \
    id-feature-extractor-type:${id_features} \
    startindex:0 \
    endindex:${fe_file_length} \
    numthreads:${num_threads}
