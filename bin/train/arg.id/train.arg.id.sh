#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../../config/training.sh"

echo
echo "Training Argument Identification"
echo

train_arg_id_bin="$(dirname ${0})"

# Argument Identification -- Step 1: Creating alphabet
bash ${train_arg_id_bin}/train.arg.id.create.alphabet.sh

# Argument Identification -- Step 2: Caching feature vectors
bash ${train_arg_id_bin}/train.arg.id.cache.feature.vectors.sh

# Argument identification -- Step 3: Training argument identification model
bash ${train_arg_id_bin}/train.arg.id.train.model.sh

# get the last model file created
model_file="$(ls ${arg_id_model}_* | sort -r | head -n1)"
echo "Using model file: ${model_file}"
echo
cp ${model_file} ${arg_id_model}

# Removing unnecessary temporary files
if [ "${clean_after_training}" = TRUE ]; then
    rm ${arg_id_model}_*
    rm ${train_events}
    rm ${spans_file}
    rm ${feature_cache}
fi