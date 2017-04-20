#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Generate cv.***.sentences.malt.input.conll splits from cv.***.sentences.pos.tagged splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_training_sentence_splits} \
    --inputFormat pos \
    --output ${malt_conll_input_training_sentence_splits} \
    --outputFormat conll

time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${postagged_testing_sentence_splits} \
    --inputFormat pos \
    --output ${malt_conll_input_testing_sentence_splits} \
    --outputFormat conll