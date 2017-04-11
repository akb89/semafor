#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../config/training.sh"

echo
echo "Frame Identification -- Step 3: Training the frame identification model"
echo

mkdir -p ${MODEL_DIR}

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
    edu.cmu.cs.lti.ark.fn.identification.training.TrainBatch \
    alphabetfile:${MODEL_DIR}/alphabet.dat \
    eventsfile:${MODEL_DIR}/events \
    model:${MODEL_DIR}/idmodel.dat \
    regularization:l1 \
    lambda:1.0 \
    restartfile:null \
    logoutputfile:${MODEL_DIR}/log \
    numthreads:${num_threads} \
    use-partial-credit:true \
    cost-multiple:5
