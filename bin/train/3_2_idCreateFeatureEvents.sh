#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../config/training.sh"

echo
echo "Frame Identification -- Step 2: Creating feature events for each datapoint"
echo

# clobber the log file
log_file="${MODEL_DIR}/log"
if [ -e ${log_file} ]; then
    rm "${log_file}"
fi

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.ExtractTrainingFeatures \
    train-fefile:${fe_file} \
    train-parsefile:${parsed_file} \
    fnidreqdatafile:${fn_id_req_data_file} \
    logoutputfile:${MODEL_DIR}/log \
    model:${MODEL_DIR}/alphabet.dat \
    id-feature-extractor-type:${id_features} \
    eventsfile:${MODEL_DIR}/events \
    startindex:0 \
    endindex:${fe_file_length} \
    numthreads:${num_threads}
