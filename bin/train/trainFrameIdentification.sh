#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../config/training.sh"

echo
echo "Training Frame Identification"
echo

mkdir -p "${EVENT_DIR}"

echo
echo "Frame Identification -- Step 1: Creating alphabet"
echo
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


echo
echo "Frame Identification -- Step 2: Creating feature events for each datapoint"
echo
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
    logoutputfile:${log_file} \
    model:${frame_id_alphabet} \
    id-feature-extractor-type:${id_features} \
    eventsfile:${EVENT_DIR} \
    startindex:0 \
    endindex:${fe_file_length} \
    numthreads:${num_threads}

echo
echo "Frame Identification -- Step 3: Training the frame identification model"
echo
if [ -e ${log_file} ]; then
    rm "${log_file}"
fi
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.TrainBatch \
    alphabetfile:${frame_id_alphabet} \
    eventsfile:${EVENT_DIR} \
    model:${frame_id_model} \
    regularization:l1 \
    lambda:1.0 \
    restartfile:null \
    logoutputfile:${log_file} \
    numthreads:${num_threads} \
    use-partial-credit:true \
    cost-multiple:5


echo
echo "Frame Identification -- Step 4: Combining alphabet file with learned params for Frame IDing"
# get the last model file created
model_file="$(ls ${MODEL_DIR}/idmodel.dat_* | sort -r | head -n1)"
echo "Using model file: ${model_file}"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.ConvertAlphabetFile \
    ${frame_id_alphabet} \
    ${model_file} \
    ${frame_id_model} \
    ${id_features}