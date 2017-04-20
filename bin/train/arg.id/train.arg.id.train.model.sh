#!/bin/bash

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