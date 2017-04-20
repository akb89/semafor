#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Generate cv.***.sentences.pos.tagged splits from cv.***.sentences splits
echo "Part-of-speech tagging tokenized training splits..."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${tokenized_training_sentence_splits} > ${postagged_training_sentence_splits}
echo "Finished part-of-speech tagging"
echo
echo "Part-of-speech tagging tokenized testing splits..."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${tokenized_testing_sentence_splits} > ${postagged_testing_sentence_splits}
echo "Finished part-of-speech tagging"
echo