#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.***.sentences.tokenized.with.exemplars splits from cv.***.sentences.with.exemplars splits
echo "Tokenizing training splits with exemplars: ${training_sentence_splits_with_exemplars} ..."
time sed -f ${tokenizer_sed} ${training_sentence_splits_with_exemplars} > ${training_tokenized_sentence_splits_with_exemplars}
echo "Finished tokenization."
echo
echo "Tokenizing testing splits with exemplars: ${testing_sentence_splits_with_exemplars} ..."
time sed -f ${tokenizer_sed} ${testing_sentence_splits_with_exemplars} > ${testing_tokenized_sentence_splits_with_exemplars}
echo "Finished tokenization"
echo