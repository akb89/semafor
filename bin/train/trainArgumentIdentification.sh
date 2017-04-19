#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../config/training.sh"

echo
echo "Training Argument Identification"
echo

mkdir -p ${SCAN_DIR}

echo
echo "Argument Identification -- Step 1: Creating alphabet"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.CreateAlphabet \
    ${fe_file} \
    ${parsed_file} \
    ${train_events} \
    ${arg_id_alphabet} \
    ${spans_file} \
    true \
    1 \
    null

echo
echo "Argument Identification -- Step 2: Caching feature vectors"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.CacheFrameFeaturesApp \
    eventsfile:${train_events} \
    spansfile:${spans_file} \
    train-framefile:${fe_file} \
    localfeaturescache:${feature_cache}

echo
echo "Argument identification -- Step 3: Training argument identification model"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.TrainArgIdApp \
    model:${arg_id_model} \
    alphabetfile:${arg_id_alphabet} \
    localfeaturescache:${feature_cache} \
    lambda:${lambda} \
    numthreads:${num_threads} \
    batch-size:${batch_size} \
    save-every-k-batches:400 \
    num-models-to-save:60