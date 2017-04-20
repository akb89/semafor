#!/bin/bash

echo
echo "Argument Identification -- Step 1: Creating alphabet"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.CreateAlphabet \
    ${training_fe_splits} \
    ${training_all_lemma_tags_sentence_splits} \
    ${train_events} \
    ${arg_id_alphabet} \
    ${spans_file} \
    true \
    1 \
    null