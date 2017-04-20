#!/bin/bash

# Removing unnecessary temporary files
rm "${all_related_words_file}"
rm "${hv_correspondence_file}"
rm "${wn_related_words_for_words_file}"
rm "${wn_map_file}"
rm "${revised_map_file}"
rm "${lemma_cache_file}"

if [ "${with_exemplars}" = false ]; then
    rm "${training_sentence_splits_with_exemplars}"
    rm "${testing_sentence_splits_with_exemplars}"
    rm "${training_fe_splits_with_exemplars}"
    rm "${testing_fe_splits_with_exemplars}"
    rm "${tokenized_training_sentence_splits_with_exemplars}"
    rm "${tokenized_testing_sentence_splits_with_exemplars}"
    rm "${postagged_training_sentence_splits_with_exemplars}"
fi

if [ "${dependency_parser}" = "MST" ]; then
    rm "${mst_conll_input_training_sentence_splits}"
    rm "${mst_conll_input_testing_sentence_splits}"
fi

if [ "${dependency_parser}" = "MALT" ]; then
    rm "${malt_conll_input_training_sentence_splits}"
    rm "${malt_conll_input_testing_sentence_splits}"
fi

if [ "${dependency_parser}" = "TURBO" ]; then
    rm "${turbo_conll_input_training_sentence_splits}"
    rm "${turbo_conll_input_testing_sentence_splits}"
fi