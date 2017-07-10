#!/bin/bash

set -e # fail fast

echo
echo "Creating Required Data..."

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

#rm -rf ${MODEL_DIR}
mkdir -p "${MODEL_DIR}"
#rm -rf ${EXPERIMENT_DATA_DIR}
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
bash ${prepare_bin}/prepare.splits.postag.sh

echo "${dependency_parser}"

if [ "${dependency_parser}" = "MST" ]; then
    # Generate cv.***.sentences.mst.input.conll splits from cv.***.sentences.pos.tagged splits
    bash ${prepare_bin}/prepare.splits.mst.input.sh
    # Generate cv.***.sentences.mstparsed.conll splits from cv.***.sentences.mst.input.conll splits
    bash ${prepare_bin}/prepare.splits.parse.mst.sh
fi

if [ "${dependency_parser}" = "MALT" ]; then
    # Generate cv.***.sentences.malt.input.conll splits from cv.***.sentences.pos.tagged splits
    bash ${prepare_bin}/prepare.splits.malt.input.sh
    # Generate cv.***.sentences.maltparsed.conll splits from cv.***.sentences.malt.input.conll splits
    bash ${prepare_bin}/prepare.splits.parse.malt.sh
fi

if [ "${dependency_parser}" = "TURBO" ]; then
    # Generate cv.***.sentences.turbo.input.conll splits from cv.***.sentences.pos.tagged splits
    bash ${prepare_bin}/prepare.splits.turbo.input.sh
    # Generate cv.***.sentences.turboparsed.conll splits from cv.***.sentences.turbo.input.conll splits
    bash ${prepare_bin}/prepare.splits.parse.turbo.sh
fi

# Generate cv.train.sentences.all.lemma.tags
bash ${prepare_bin}/prepare.splits.merge.all.tags.sh

# framenet.original.map and framenet.frame.element.map are based on fulltext AND exemplar data
# When processing fulltext data only, it is necessary to add exemplar data for generating the maps
if [ "${with_exemplars}" = false ]; then
    bash ${prepare_bin}/prepare.splits.for.maps.with.exemplars.sh
fi

if [ "${with_exemplars}" = true ]; then
    training_fe_splits_with_exemplars=${training_fe_splits}
    training_postagged_sentence_splits_with_exemplars=${training_postagged_sentence_splits}
fi

# Create files framenet.original.map and framenet.frame.element.map under the MODEL_DIR directory
bash ${prepare_bin}/prepare.splits.framenet.maps.sh

# Create the file reqData.jobj under the MODEL_DIR directory
bash ${prepare_bin}/prepare.splits.wordnet.maps.sh

# Create files frames.xml and feRelations.xml for use with perl score script under the EXPERIMENT_DATA_DIR directory
bash ${prepare_bin}/prepare.xml.files.for.scoring.sh

# Removing unnecessary temporary files
if [ "${clean_after_preprocessing}" = true ]; then
    bash ${prepare_bin}/prepare.splits.clean.sh
fi