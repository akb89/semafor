#!/bin/bash

set -e # fail fast

echo
echo "step 4iii: Training."
echo "params: 1: lambda 2: batch-size"

source "$(dirname ${0})/../../config/training.sh"


mkdir ${MODEL_DIR}/lambda_$1;
echo model dir: ${MODEL_DIR}/lambda_$1;
echo ${SCAN_DIR}
echo ${CLASSPATH}

${JAVA_HOME_BIN}/java -classpath ${CLASSPATH} -Xms15g -Xmx${max_ram} \
edu.cmu.cs.lti.ark.fn.parsing.TrainArgIdApp \
model:${MODEL_DIR}/lambda_$1/svm.argmodel.dat \
alphabetfile:${SCAN_DIR}/parser.conf.unlabeled \
localfeaturescache:${SCAN_DIR}/featurecache.jobj \
lambda:$1 \
numthreads:${num_threads} \
batch-size:$2 \
save-every-k-batches:400 \
num-models-to-save:60

