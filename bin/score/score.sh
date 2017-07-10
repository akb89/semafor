#!/bin/bash
set -e # fail fast

echo
echo "Evaluating Semafor parser..."

source "$(dirname "${BASH_SOURCE[0]}")/../../config/scoring.sh"

score_bin="$(dirname ${0})"

# Validate input parameters
bash ${score_bin}/score.validate.input.parameters.sh

echo "Results directory: ${RESULTS_DIR}"
mkdir -p "${RESULTS_DIR}"

#***************** Run SEMAFOR with Gold Targets and Gold Frames ***********************#

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.unige.clcl.fn.score.ScoreWithGoldFrames \
    "${testing_sentence_splits_with_dependencies}" \
    "${testing_frame_splits}" \
    "${arg_id_alphabet}" \
    "${framenet_fe_map_file}" \
    "${arg_id_model}" \
    1 \
    "${scoring_predicted_goldframe_xml_file}" > ${LOGS_DIR}/score.log # Redirect stout to log file

#***************** Create a gold XML file with the same tokenization that SEMAFOR used ***********************#

${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.evaluation.PrepareFullAnnotationXML \
    testFEPredictionsFile:"${testing_fe_splits}" \
    startIndex:0 \
    endIndex:${end_index_prepare_fullanno_xml} \
    testParseFile:"${testing_all_lemma_tags_sentence_splits}" \
    testTokenizedFile:"${testing_tokenized_sentence_splits}" \
    outputFile:"${scoring_gold_xml_file}" >> ${LOGS_DIR}/score.log # Redirect stout to log file


#********************************** Evaluation ********************************************#

echo "Scoring with SemEval perl script..."
${SEMAFOR_HOME}/bin/score/score.pl \
    -c "${RESULTS_DIR}" \
    -l \
    -n \
    -e \
    -v \
    "${frames_single_file}" \
    "${relation_modified_file}" \
    "${scoring_gold_xml_file}" \
    "${scoring_predicted_goldframe_xml_file}" > "${scoring_output_text_file}" 2>> ${LOGS_DIR}/score.log # Redirect stout to log file

echo "Scoring completed"

#********************************** End of Evaluation ********************************************#

# Removing unnecessary temporary files
if [ "${clean_after_scoring}" = true ]; then
    echo "cleaning score tmp"
    rm "${scoring_gold_xml_file}"
    rm "${scoring_predicted_goldframe_xml_file}"
fi
