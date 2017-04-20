#!/bin/bash

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