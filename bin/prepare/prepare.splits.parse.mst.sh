#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

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
	test-file:${mst_conll_input_training_sentence_splits} \
	output-file:${mstparsed_training_sentence_splits} \
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
	test-file:${mst_conll_input_testing_sentence_splits} \
	output-file:${mstparsed_testing_sentence_splits} \
	format:CONLL
echo "Finished MST dependency parsing"
echo