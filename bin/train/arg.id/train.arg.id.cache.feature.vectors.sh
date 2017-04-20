#!/bin/bash

echo
echo "Argument Identification -- Step 2: Caching feature vectors"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.CacheFrameFeaturesApp \
    eventsfile:${train_events} \
    spansfile:${spans_file} \
    train-framefile:${training_fe_splits} \
    localfeaturescache:${feature_cache}