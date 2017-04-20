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
    train-fefile:${training_fe_splits} \
    train-parsefile:${training_all_lemma_tags_sentence_splits} \
    fnidreqdatafile:${fn_id_req_data_file} \
    logoutputfile:${log_file} \
    model:${MODEL_DIR} \
    id-feature-extractor-type:${id_features} \
    startindex:0 \
    endindex:${fe_file_length} \
    numthreads:${num_threads}