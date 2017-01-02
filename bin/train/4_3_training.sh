#!/bin/bash

set -e # fail fast

# step 4iii: Training.

source "$(dirname ${0})/config.sh"

${JAVA_HOME_BIN}/java -classpath ${classpath} -Xms4g -Xmx${max_ram} \
  edu.cmu.cs.lti.ark.fn.parsing.TrainingBatchMain \
  model:${datadir}/argmodel.dat \
  alphabetfile:${SCAN_DIR}/parser.conf.unlabeled \
  localfeaturescache:${SCAN_DIR}/featurecache.jobj \
  train-framefile:${fe_file} \
  regularization:reg \
  lambda:0.1 \
  numthreads:${num_threads} \
  binaryoverlapfactor:false
