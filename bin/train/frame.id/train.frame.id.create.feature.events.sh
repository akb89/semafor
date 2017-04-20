#!/bin/bash

echo
echo "Frame Identification -- Step 2: Creating feature events for each datapoint"
echo

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.ExtractTrainingFeatures \
    train_fe_file:${training_fe_splits} \
    train_parse_file:${training_all_lemma_tags_sentence_splits} \
    fn_id_req_data_file:${fn_id_req_data_file} \
    log_output_file:${log_file} \
    model:${frame_id_alphabet} \
    id_feature_extractor_type:${id_features} \
    events_file:${EVENT_DIR} \
    start_index:0 \
    end_index:${fe_file_length} \
    num_threads:${num_threads}