#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../../config/training.sh"

echo
echo "Training Argument Identification"
echo

mkdir -p ${SCAN_DIR}

train_arg_id_bin="$(dirname ${0})"

# Argument Identification -- Step 1: Creating alphabet
bash ${train_arg_id_bin}/train.arg.id.create.alphabet.sh

# Argument Identification -- Step 2: Caching feature vectors
bash ${train_arg_id_bin}/train.arg.id.cache.feature.vectors.sh

# Argument identification -- Step 3: Training argument identification model
bash ${train_arg_id_bin}/train.arg.id.train.model.sh