#!/bin/bash

# Check if script is called from commandline or from within another script
if ps -o stat= -p $PPID | grep -q "s"; then
    source "$(dirname ${0})/../../../config/training.sh"
fi

echo
echo "Argument Identification -- Step 2: Caching feature vectors"
echo
${JAVA_HOME_BIN}/java \
    -classpath ${CLASSPATH} \
    -Xms${min_ram} \
    -Xmx${max_ram} \
    edu.cmu.cs.lti.ark.fn.parsing.CacheFrameFeaturesApp \
    events_file:${train_events} \
    spans_file:${spans_file} \
    train_frame_file:${training_fe_splits} \
    local_features_cache:${feature_cache} > ${LOGS_DIR}/train.arg.id.cache.feature.vectors.log # Redirect stout to semafor/logs