#!/bin/bash

echo
echo "Argument Identification -- Step 1: Creating alphabet"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.CreateAlphabet \
    ${fe_file} \
    ${parsed_file} \
    ${train_events} \
    ${arg_id_alphabet} \
    ${spans_file} \
    true \
    1 \
    null