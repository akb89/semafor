#!/bin/bash

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
    train_fe_file:${training_fe_splits} \
    train_parse_file:${training_all_lemma_tags_sentence_splits} \
    fn_id_req_data_file:${fn_id_req_data_file} \
    log_output_file:${log_file} \
    model:${MODEL_DIR} \
    id_feature_extractor_type:${id_features} \
    start_index:0 \
    end_index:${fe_file_length} \
    num_threads:${num_threads}