#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Generate cv.train.sentences.all.lemma.tags
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
    ${tokenized_training_sentence_splits} \
    ${training_sentence_splits_with_dependencies} \
    ${tmp_file} \
    ${all_lemma_tags_training_sentence_splits}
rm "${tmp_file}"

# Generate cv.test.sentences.all.lemma.tags
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
    ${tokenized_testing_sentence_splits} \
    ${testing_sentence_splits_with_dependencies} \
    ${tmp_file} \
    ${all_lemma_tags_testing_sentence_splits}
rm "${tmp_file}"