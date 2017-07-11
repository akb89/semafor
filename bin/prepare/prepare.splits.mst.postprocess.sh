#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

# Generate cv.***.sentences.with.dependencies.conll splits from cv.***.sentences.with.dependencies.conll.before.postprocessing
echo "Postprocessing MSTParser CoNLL training output: replacing <num> with original numbers in words and ()[]{} with PTB tokens (-LRB-, -RSB-, ...)"
cut -f 1-2 ${training_mst_conll_input_sentence_splits} > ${training_mst_conll_postprocessed_words}
cut -f 3-10 ${training_sentence_splits_with_dependencies_before_postprocessing} > ${training_mst_conll_postprocessed_rest}
sed -i -e 's/(/-LRB-/g' ${training_mst_conll_postprocessed_words}
sed -i -e 's/)/-RRB-/g' ${training_mst_conll_postprocessed_words}
sed -i -e 's/\[/-LSB-/g' ${training_mst_conll_postprocessed_words}
sed -i -e 's/\]/-RSB-/g' ${training_mst_conll_postprocessed_words}
sed -i -e 's/\{/-LCB-/g' ${training_mst_conll_postprocessed_words}
sed -i -e 's/\}/-RCB-/g' ${training_mst_conll_postprocessed_words}
paste ${training_mst_conll_postprocessed_words} ${training_mst_conll_postprocessed_rest} | perl -pe "s/^\t+$//g" > ${training_sentence_splits_with_dependencies}
echo "Done postprocessing MSTParser CoNLL training ouptut"
echo
echo "Postprocessing MSTParser CoNLL testing output: replacing <num> with original numbers in words and ()[]{} with PTB tokens (-LRB-, -RSB-, ...)"
cut -f 1-2 ${testing_mst_conll_input_sentence_splits} > ${testing_mst_conll_postprocessed_words}
cut -f 3-10 ${testing_sentence_splits_with_dependencies_before_postprocessing} > ${testing_mst_conll_postprocessed_rest}
sed -i -e 's/(/-LRB-/g' ${testing_mst_conll_postprocessed_words}
sed -i -e 's/)/-RRB-/g' ${testing_mst_conll_postprocessed_words}
sed -i -e 's/\[/-LSB-/g' ${testing_mst_conll_postprocessed_words}
sed -i -e 's/\]/-RSB-/g' ${testing_mst_conll_postprocessed_words}
sed -i -e 's/\{/-LCB-/g' ${testing_mst_conll_postprocessed_words}
sed -i -e 's/\}/-RCB-/g' ${testing_mst_conll_postprocessed_words}
paste ${testing_mst_conll_postprocessed_words} ${testing_mst_conll_postprocessed_rest} | perl -pe "s/^\t+$//g" > ${testing_sentence_splits_with_dependencies}
echo "Done postprocessing MSTParser CoNLL testing ouptut"
echo