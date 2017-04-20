#!/bin/bash

# Generate cv.***.sentences.mst.input.conll splits from cv.***.sentences.pos.tagged splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${postagged_training_sentence_splits} ${mst_conll_input_training_sentence_splits}

time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${postagged_testing_sentence_splits} ${mst_conll_input_testing_sentence_splits}