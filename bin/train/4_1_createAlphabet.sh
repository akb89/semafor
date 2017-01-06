#!/bin/bash

set -e # fail fast

echo
echo "step 4i: create the alphabet file for the argument identification model."
echo

source "$(dirname ${0})/../../config/training.sh"

mkdir -p ${SCAN_DIR}


${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xms4g -Xmx${max_ram} \
   edu.cmu.cs.lti.ark.fn.parsing.CreateAlphabet \
   ${fe_file} \
   ${parsed_file} \
   ${SCAN_DIR}/cv.train.events.bin \
   ${SCAN_DIR}/parser.conf.unlabeled \
   ${SCAN_DIR}/cv.train.sentences.frame.elements.spans \
   true \
   1 \
   null