#!/bin/bash

set -e # fail fast

source "$(dirname ${0})/../../../config/training.sh"

echo
echo "Training Frame Identification"

mkdir -p "${EVENT_DIR}"

train_frame_id_bin="$(dirname ${0})"

# Frame Identification -- Step 1: Creating alphabet
bash ${train_frame_id_bin}/train.frame.id.create.alphabet.sh

# Frame Identification -- Step 2: Creating feature events for each datapoint
bash ${train_frame_id_bin}/train.frame.id.create.feature.events.sh

# Frame Identification -- Step 3: Training the frame identification model
bash ${train_frame_id_bin}/train.frame.id.train.model.sh

# Frame Identification -- Step 4: Combining alphabet file with learned params for Frame IDing
bash ${train_frame_id_bin}/train.frame.id.combine.params.sh