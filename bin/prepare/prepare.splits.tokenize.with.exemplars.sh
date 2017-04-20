#!/bin/bash

# Generate cv.***.sentences.tokenized.with.exemplars splits from cv.***.sentences.with.exemplars splits
echo "Tokenizing training splits with exemplars: ${training_sentence_splits_with_exemplars} ..."
time sed -f ${tokenizer_sed} ${training_sentence_splits_with_exemplars} > ${tokenized_training_sentence_splits_with_exemplars}
echo "Finished tokenization."
echo
echo "Tokenizing testing splits with exemplars: ${testing_sentence_splits_with_exemplars} ..."
time sed -f ${tokenizer_sed} ${testing_sentence_splits_with_exemplars} > ${tokenized_testing_sentence_splits_with_exemplars}
echo "Finished tokenization"
echo