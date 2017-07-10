#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname ${0})/../../../config/training.sh"
fi

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
    null > ${LOGS_DIR}/train.arg.id.create.alphabet.log # Redirect stout to log file