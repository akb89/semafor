#!/bin/bash
set -x # echo commands
set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/scoring.sh"

echo "results directory: ${RESULTS_DIR}"
mkdir -p "${RESULTS_DIR}"

#***************** Run SEMAFOR with Gold Targets, Gold Frames, Auto Arg-id ***********************#

scala -classpath ${CLASSPATH} -J-Xmx${max_ram} "${SEMAFOR_HOME}/bin/score/runWithGoldTargets.scala" \
    "${MODEL_DIR}" \
    "${sentences_frames}" \
    "${sentences_maltparsed}" \
    "${RESULTS_DIR}/test.goldframe.predicted.xml" \
    true # use gold frames

#***************** Create a gold XML file with the same tokenization that SEMAFOR used ***********************#

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

echo "Exact Arg Results"
${SEMAFOR_HOME}/bin/score/fnSemScore_modified.pl \
    -c "${RESULTS_DIR}" \
    -l \
    -n \
    -e \
    -v \
    "${frames_single_file}" \
    "${relation_modified_file}" \
    "${RESULTS_DIR}/test.gold.xml" \
    "${RESULTS_DIR}/test.goldframe.predicted.xml" > "${RESULTS_DIR}/arg_test_exact_verbose"

#********************************** End of Evaluation ********************************************#
