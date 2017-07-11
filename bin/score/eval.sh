#!/bin/bash
set -e # fail fast

echo
echo "Evaluating Semafor parser with Semeval 2007 script..."

source "$(dirname "${BASH_SOURCE[0]}")/../../config/scoring.sh"

score_bin="$(dirname ${0})"

echo "Scoring with Kshirsagar et al. (2015) perl script..."
${SEMAFOR_HOME}/bin/score/score_acl_2015.pl \
    -c "${RESULTS_DIR}" \
    -l \
    -n \
    -e \
    -v \
    "${frames_single_file}" \
    "${relation_modified_file}" \
    "${scoring_gold_xml_file}" \
    "${scoring_predicted_goldframe_xml_file}" > "${scoring_output_text_file_acl_2015}" 2>> ${LOGS_DIR}/score.log # Redirect stout to log file

echo "Scoring completed"