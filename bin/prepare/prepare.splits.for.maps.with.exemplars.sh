#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname "${BASH_SOURCE[0]}")/../../config/preprocessing.sh"
fi

prepare_bin="$(dirname ${0})"

bash ${prepare_bin}/prepare.splits.raw.sentences.with.exemplars.sh
bash ${prepare_bin}/prepare.splits.tokenize.with.exemplars.sh
bash ${prepare_bin}/prepare.splits.postag.with.exemplars.sh
bash ${prepare_bin}/prepare.splits.frame.elements.with.exemplars.sh