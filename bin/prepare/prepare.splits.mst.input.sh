#!/bin/bash

# Generate cv.***.sentences.mst.input.conll splits from cv.***.sentences.pos.tagged splits
time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${training_postagged_sentence_splits} ${training_mst_conll_input_sentence_splits}

time ${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${testing_postagged_sentence_splits} ${testing_mst_conll_input_sentence_splits}