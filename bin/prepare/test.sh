#!/bin/bash

set -e # fail fast

echo
echo "Creating Required Data..."

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

rm -rf ${MODEL_DIR}
mkdir -p "${MODEL_DIR}"
rm -rf ${EXPERIMENT_DATA_DIR}
mkdir -p ${EXPERIMENT_DATA_DIR}

prepare_bin="$(dirname ${0})"

# Validate input parameters
bash ${prepare_bin}/prepare.validate.input.parameters.sh

# Generate cv.***.sentences splits from FrameNet XML data under the EXPERIMENT_DATA_DIR directory
bash ${prepare_bin}/prepare.splits.raw.sentences.sh

# Generate cv.***.sentences.tokenized splits from cv.***.sentences splits
bash ${prepare_bin}/prepare.splits.tokenize.sh

# Generate cv.***.sentences.frames from cv.***.sentences splits
bash ${prepare_bin}/prepare.splits.frames.sh

# Generate cv.***.sentences.frame.elements from cv.***.sentences splits
bash ${prepare_bin}/prepare.splits.frame.elements.sh

# Generate cv.***.sentences.pos.tagged splits from cv.***.sentences splits
bash ${prepare_bin}/prepare.splits.postag.sh 2> ${LOGS_DIR}/mxpost.log # Redirect stderr to semafor/logs/mxpost.log