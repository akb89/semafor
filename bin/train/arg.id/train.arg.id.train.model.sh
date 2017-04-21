#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname ${0})/../../../config/training.sh"
fi

echo
echo "Argument identification -- Step 3: Training argument identification model"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.TrainArgIdApp \
    model:${arg_id_model} \
    alphabet_file:${arg_id_alphabet} \
    local_features_cache:${feature_cache} \
    lambda:${lambda} \
    num_threads:${num_threads} \
    batch_size:${batch_size} \
    save_every_k_batches:400 \
    num_models_to_save:60