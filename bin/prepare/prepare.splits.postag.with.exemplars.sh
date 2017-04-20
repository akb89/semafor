#!/bin/bash

# Generate cv.***.sentences.pos.tagged.with.exemplars splits from cv.***.sentences.with.exemplars splits
echo "Part-of-speech tagging tokenized training splits with exemplars..."
pushd ${POS_TAGGER_HOME}
time ./mxpost tagger.project < ${tokenized_training_sentence_splits_with_exemplars} > ${postagged_training_sentence_splits_with_exemplars}
echo "Finished part-of-speech tagging"
echo