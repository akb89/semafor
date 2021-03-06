#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname ${0})/../../../config/training.sh"
fi

echo
echo "Frame Identification -- Step 4: Combining alphabet file with learned params for Frame IDing"
# get the last model file created
model_file="$(ls ${frame_id_model}_* | sort -r | head -n1)"
echo "Using model file: ${model_file}"
echo

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    -XX:ParallelGCThreads=${gc_threads} \
    edu.cmu.cs.lti.ark.fn.identification.training.ConvertAlphabetFile \
    ${frame_id_alphabet} \
    ${model_file} \
    ${frame_id_model} \
    ${id_features}