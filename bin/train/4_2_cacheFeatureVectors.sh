#!/bin/bash

set -e # fail fast

echo
echo "Argument Identification -- Step 2: Caching feature vectors"
echo

source "$(dirname ${0})/../../config/training.sh"


${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.CacheFrameFeaturesApp \
    eventsfile:${SCAN_DIR}/cv.train.events.bin \
    spansfile:${SCAN_DIR}/cv.train.sentences.frame.elements.spans \
    train-framefile:${fe_file} \
    localfeaturescache:${SCAN_DIR}/featurecache.jobj
