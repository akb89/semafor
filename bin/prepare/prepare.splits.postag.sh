#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.***.sentences.pos.tagged splits from cv.***.sentences splits
echo "Part-of-speech tagging tokenized training splits..."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${training_tokenized_sentence_splits} > ${training_postagged_sentence_splits}
echo "Finished part-of-speech tagging"
echo
echo "Part-of-speech tagging tokenized testing splits..."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${testing_tokenized_sentence_splits} > ${testing_postagged_sentence_splits}
echo "Finished part-of-speech tagging"
echo