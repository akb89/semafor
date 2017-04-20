#!/bin/bash

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