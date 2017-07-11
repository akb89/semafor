#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.***.sentences.mst.input.conll splits from cv.***.sentences.pos.tagged splits
echo "Converting postagged training splits to MST conll input..."
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${training_postagged_sentence_splits} ${training_mst_conll_input_sentence_splits} > ${LOGS_DIR}/mst.input.log
echo "Done converting postagged training splits to MST conll input"
echo
echo "Converting postagged training splits to MST conll input..."
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
	edu.cmu.cs.lti.ark.fn.data.prep.CoNLLInputPreparation \
	${testing_postagged_sentence_splits} ${testing_mst_conll_input_sentence_splits} >> ${LOGS_DIR}/mst.input.log
echo "Done converting postagged testing splits to MST conll input"
echo