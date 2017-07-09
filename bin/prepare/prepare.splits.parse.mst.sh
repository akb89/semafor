#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

mkdir -p ${MST_PARSER_HOME}/tmp

# Generate cv.***.sentences.mstparsed.conll splits from cv.***.sentences.mst.input.conll splits
echo "Running MSTParser on conll training splits..."
pushd ${MST_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -classpath ".:./lib/trove.jar:./lib/mallet-deps.jar:./lib/mallet.jar" \
	-Xms${min_ram} \
	-Xmx${max_ram} \
	mst.DependencyParser \
	test \
	separate-lab \
	model-name:${mst_parser_model} \
	decode-type:proj \
	order:2 \
	test-file:${training_mst_conll_input_sentence_splits} \
	output-file:${training_sentence_splits_with_dependencies} \
	format:CONLL
echo "Finished MST dependency parsing"
echo
echo "Running MSTParser on conll testing splits..."
pushd ${MST_PARSER_HOME}
time ${JAVA_HOME_BIN}/java \
    -classpath ".:./lib/trove.jar:./lib/mallet-deps.jar:./lib/mallet.jar" \
	-Xms${min_ram} \
	-Xmx${max_ram} \
	mst.DependencyParser \
	test \
	separate-lab \
	model-name:${mst_parser_model} \
	decode-type:proj \
	order:2 \
	test-file:${testing_mst_conll_input_sentence_splits} \
	output-file:${testing_sentence_splits_with_dependencies} \
	format:CONLL
echo "Finished MST dependency parsing"
echo