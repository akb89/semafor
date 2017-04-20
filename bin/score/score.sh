#!/bin/bash
set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/scoring.sh"

echo "results directory: ${RESULTS_DIR}"
mkdir -p "${RESULTS_DIR}"

#***************** Run SEMAFOR with Gold Targets, Gold Frames, Auto Arg-id ***********************#

scala \
    -classpath ${CLASSPATH} \
    -J-Xmx${max_ram} \
    "${SEMAFOR_HOME}/bin/score/score.scala" \
    "${MODEL_DIR}" \
    "${testing_frame_splits}" \
    "${testing_sentence_splits_with_dependencies}" \
    "${RESULTS_DIR}/test.goldframe.predicted.xml" \
    true # use gold frames

#***************** Create a gold XML file with the same tokenization that SEMAFOR used ***********************#

end=`wc -l "${tokenized_file}"`
end=`expr ${end% *}`

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.evaluation.PrepareFullAnnotationXML \
    testFEPredictionsFile:"${testing_fe_splits}" \
    startIndex:0 \
    endIndex:${end} \
    testParseFile:"${all_lemma_tags_testing_sentence_splits}" \
    testTokenizedFile:"${tokenized_testing_sentence_splits}" \
    outputFile:"${RESULTS_DIR}/test.gold.xml"


#********************************** Evaluation ********************************************#

echo "Exact Arg Results"
${SEMAFOR_HOME}/bin/score/score.pl \
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
