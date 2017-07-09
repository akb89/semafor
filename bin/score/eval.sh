#!/bin/bash
set -e # fail fast

echo
echo "Evaluating Semafor parser..."

source "$(dirname "${BASH_SOURCE[0]}")/../../config/scoring.sh"

score_bin="$(dirname ${0})"

# Validate input parameters
bash ${score_bin}/score.validate.input.parameters.sh
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
    "${scoring_predicted_goldframe_xml_file}" > "${scoring_output_text_file}"

echo "Scoring completed"