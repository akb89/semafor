#!/bin/bash

set -e # fail fast

echo
echo "step 4ii: Caching Feature Vectors"
echo

source "$(dirname ${0})/../../config/training.sh"


${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xms15g -Xmx${max_ram} \
edu.cmu.cs.lti.ark.fn.parsing.CacheFrameFeaturesApp \
eventsfile:${SCAN_DIR}/cv.train.events.bin \
spansfile:${SCAN_DIR}/cv.train.sentences.frame.elements.spans \
train-framefile:${fe_file} \
localfeaturescache:${SCAN_DIR}/featurecache.jobj
