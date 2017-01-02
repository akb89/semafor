#!/bin/bash

######################## ENVIRONMENT VARIABLES ###############################
######### change the following according to your own local setup #############

# assumes this script (all.sh) lives in "${BASE_DIR}/semafor/config/"
export BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." > /dev/null && pwd )"

# path to the absolute path where you decompressed SEMAFOR.
export SEMAFOR_HOME="${BASE_DIR}/semafor"

export CLASSPATH=".:${SEMAFOR_HOME}/target/Semafor-3.0-alpha-04.jar"

# Change the following to the bin directory of your $JAVA_HOME
export JAVA_HOME_BIN="/Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home/bin"

# Change the following to the directory where you decompressed the models for SEMAFOR 2.0.
#export MALT_MODEL_DIR="${SEMAFOR_HOME}/models/semafor_malt_model_20121129"
#export TURBO_MODEL_DIR="${SEMAFOR_HOME}/models/turbo_20130606"

# should set to roughly the number of cores available
export num_threads=8
export gc_threads=2
export max_ram=6g #max RAM allocated to the JVM in GB. Corresponds to the -Xmx argument

# choose a name for the model to train
export model_name="acl2015_fn_with_exemplars"

export EXPERIMENTS_DIR="${SEMAFOR_HOME}/experiments/${model_name}"

# the directory the resulting model will end up in
export MODEL_DIR="${EXPERIMENTS_DIR}/model"

# the directory that contains all sets of training data
# framenet.frame.element.map and framenet.original.map
export DATA_DIR="${EXPERIMENTS_DIR}/data"

######################## END ENVIRONMENT VARIABLES #########################

echo "Environment variables:"
echo "SEMAFOR_HOME=${SEMAFOR_HOME}"
echo "CLASSPATH=${CLASSPATH}"
echo "JAVA_HOME_BIN=${JAVA_HOME_BIN}"
echo "model_name=${model_name}"
echo "MODEL_DIR=${MODEL_DIR}"
echo "EXPERIMENTS_DIR=${EXPERIMENTS_DIR}"
echo "DATA_DIR=${DATA_DIR}"