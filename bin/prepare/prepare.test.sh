#!/usr/bin/env bash
#set -x # echo commands
set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

echo
echo "Creating Required Data"
echo

#rm -rf ${MODEL_DIR}
#mkdir -p "${MODEL_DIR}"
#rm -rf ${EXPERIMENT_DATA_DIR}
#mkdir ${EXPERIMENT_DATA_DIR}

# Generate cv.***.sentences.all.lemma.tags
echo "**********************************************************************"
echo "Merging POS tags, dependency parses, and lemmatized version of each training sentence into one line...."
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xms1g -Xmx${max_ram} \
        edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
          ${tokenized_training_splits} \
          ${maltparsed_training_splits} \
          ${tmp_file} \
          ${all_lemma_tags_training_splits}
rm "${tmp_file}"
echo "Finished merging"
echo
echo "**********************************************************************"
echo "Merging POS tags, dependency parses, and lemmatized version of each testing sentence into one line...."
pushd ${MALT_PARSER_HOME}
time ${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xms1g -Xmx${max_ram} \
        edu.cmu.cs.lti.ark.fn.data.prep.AllAnnotationsMergingWithoutNE \
          ${tokenized_testing_splits} \
          ${maltparsed_testing_splits} \
          ${tmp_file} \
          ${all_lemma_tags_testing_splits}
rm "${tmp_file}"
echo "Finished merging"
echo