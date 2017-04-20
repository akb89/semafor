#!/bin/bash

# Generate cv.***.sentences.malt.input.conll splits from cv.***.sentences.pos.tagged splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${training_postagged_sentence_splits} \
    --inputFormat pos \
    --output ${training_malt_conll_input_sentence_splits} \
    --outputFormat conll

time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    edu.cmu.cs.lti.ark.fn.data.prep.formats.ConvertFormat \
    --input ${testing_postagged_sentence_splits} \
    --inputFormat pos \
    --output ${testing_malt_conll_input_sentence_splits} \
    --outputFormat conll