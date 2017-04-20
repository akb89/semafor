#!/bin/bash

set -e # fail fast

source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"

# Removing unnecessary temporary files
rm "${all_related_words_file}"
rm "${hv_correspondence_file}"
rm "${wn_related_words_for_words_file}"
rm "${wn_map_file}"
rm "${revised_map_file}"
rm "${lemma_cache_file}"