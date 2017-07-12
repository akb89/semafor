#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Removing unnecessary temporary files
echo "Cleaning up directories by removing unnecessary tmp files..."
rm "${training_sentence_splits}"
rm "${testing_sentence_splits}"
rm "${training_frame_splits}"
rm "${training_tokenized_sentence_splits}"
rm "${training_postagged_sentence_splits}"
rm "${testing_postagged_sentence_splits}"
rm "${training_sentence_splits_with_dependencies}"

if [ "${with_exemplars}" = FALSE ]; then
    rm "${training_sentence_splits_with_exemplars}"
    rm "${testing_sentence_splits_with_exemplars}"
    rm "${training_fe_splits_with_exemplars}"
    rm "${testing_fe_splits_with_exemplars}"
    rm "${training_tokenized_sentence_splits_with_exemplars}"
    rm "${testing_tokenized_sentence_splits_with_exemplars}"
    rm "${training_postagged_sentence_splits_with_exemplars}"
fi

if [ "${dependency_parser}" = "MST" ]; then
    rm "${training_mst_conll_input_sentence_splits}"
    rm "${testing_mst_conll_input_sentence_splits}"
    rm "${training_mst_conll_postprocessed_words}"
    rm "${testing_mst_conll_postprocessed_words}"
    rm "${training_mst_conll_postprocessed_rest}"
    rm "${testing_mst_conll_postprocessed_rest}"
    rm ${training_sentence_splits_with_dependencies_before_postprocessing}
    rm ${testing_sentence_splits_with_dependencies_before_postprocessing}
fi

if [ "${dependency_parser}" = "MALT" ]; then
    rm "${training_malt_conll_input_sentence_splits}"
    rm "${testing_malt_conll_input_sentence_splits}"
fi

if [ "${dependency_parser}" = "TURBO" ]; then
    rm "${turbo_conll_input_training_sentence_splits}"
    rm "${turbo_conll_input_testing_sentence_splits}"
fi

rm "${framenet_lu_map_file}"
rm "${all_related_words_file}"
rm "${hv_correspondence_file}"
rm "${wn_related_words_for_words_file}"
rm "${wn_map_file}"
rm "${revised_map_file}"
rm "${lemma_cache_file}"
echo "Done cleaning up directories"