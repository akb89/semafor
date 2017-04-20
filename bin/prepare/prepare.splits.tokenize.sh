#!/bin/bash

# Generate cv.***.sentences.tokenized splits from cv.***.sentences splits
echo "Tokenizing training splits: ${training_sentence_splits} ..."
time sed -f ${tokenizer_sed} ${training_sentence_splits} > ${training_tokenized_sentence_splits}
echo "Finished tokenization."
echo
echo "Tokenizing testing splits: ${testing_sentence_splits} ..."
time sed -f ${tokenizer_sed} ${testing_sentence_splits} > ${testing_tokenized_sentence_splits}
echo "Finished tokenization"
echo