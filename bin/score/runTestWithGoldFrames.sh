#!/bin/bash
set -x # echo commands
set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/scoring.sh"

cv="test"

echo "results directory: ${RESULTS_DIR}"
mkdir -p "${RESULTS_DIR}"


#***************** Run SEMAFOR with Gold Targets, Gold Frames, Auto Arg-id ***********************#

scala -classpath ${CLASSPATH} -J-Xmx${max_ram} "${SEMAFOR_HOME}/bin/score/runWithGoldTargets.scala" \
    "${MODEL_DIR}" \
    "${DATA_DIR}/cv.${cv}.sentences.frames" \
    "${DATA_DIR}/cv.${cv}.sentences.maltparsed.conll" \
    "${RESULTS_DIR}/${cv}.goldframe.predicted.xml" \
    true # use gold frames


#***************** Create a gold XML file with the same tokenization that SEMAFOR used ***********************#

processed_file="${DATA_DIR}/cv.${cv}.sentences.all.lemma.tags"
tokenized_file="${DATA_DIR}/cv.${cv}.sentences.tokenized"
fe_file="${DATA_DIR}/cv.${cv}.sentences.frame.elements"

end=`wc -l "${tokenized_file}"`
end=`expr ${end% *}`


${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.evaluation.PrepareFullAnnotationXML \
    testFEPredictionsFile:"${fe_file}" \
    startIndex:0 \
    endIndex:${end} \
    testParseFile:"${processed_file}" \
    testTokenizedFile:"${tokenized_file}" \
    outputFile:"${RESULTS_DIR}/${cv}.gold.xml"


#********************************** Evaluation ********************************************#

frames_single_file="${DATA_DIR}/framesSingleFile.xml"
relation_modified_file="${DATA_DIR}/frRelationModified.xml"

echo "Exact Arg Results"
${SEMAFOR_HOME}/bin/score/fnSemScore_modified.pl \
    -c "${RESULTS_DIR}" \
    -l \
    -n \
    -e \
    -v \
    "${frames_single_file}" \
    "${relation_modified_file}" \
    "${RESULTS_DIR}/${cv}.gold.xml" \
    "${RESULTS_DIR}/${cv}.goldframe.predicted.xml" > "${RESULTS_DIR}/arg_${cv}_exact_verbose"


#********************************** End of Evaluation ********************************************#
