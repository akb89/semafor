#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.train.sentences.all.lemma.tags
echo "Merging POS tags, dependency parses, and lemmatized version of each training sentence into one line..."
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
    ${training_tokenized_sentence_splits} \
    ${training_sentence_splits_with_dependencies} \
    ${tmp_file} \
    ${training_all_lemma_tags_sentence_splits}
rm "${tmp_file}"
echo "Done merging POS tags, dependency parses, and lemmatized version of each training sentence into one line"

# Generate cv.test.sentences.all.lemma.tags
echo "Merging POS tags, dependency parses, and lemmatized version of each testing sentence into one line..."
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
    ${testing_tokenized_sentence_splits} \
    ${testing_sentence_splits_with_dependencies} \
    ${tmp_file} \
    ${testing_all_lemma_tags_sentence_splits}
rm "${tmp_file}"
echo "Done merging POS tags, dependency parses, and lemmatized version of each testing sentence into one line"