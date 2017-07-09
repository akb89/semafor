#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.***.sentences.pos.tagged.with.exemplars splits from cv.***.sentences.with.exemplars splits
echo "Part-of-speech tagging tokenized training splits with exemplars..."
pushd ${POS_TAGGER_HOME}
./mxpost tagger.project < ${training_tokenized_sentence_splits_with_exemplars} > ${training_postagged_sentence_splits_with_exemplars}
echo "Finished part-of-speech tagging"
echo