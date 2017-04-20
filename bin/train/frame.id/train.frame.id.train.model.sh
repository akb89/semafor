#!/bin/bash

echo
echo "Frame Identification -- Step 3: Training the frame identification model"
echo

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.TrainBatch \
    alphabet_file:${frame_id_alphabet} \
    events_file:${EVENT_DIR} \
    model:${frame_id_model} \
    regularization:l1 \
    lambda:1.0 \
    restart_file:null \
    log_output_file:${log_file} \
    num_threads:${num_threads} \
    use_partial_credit:true \
    cost_multiple:5