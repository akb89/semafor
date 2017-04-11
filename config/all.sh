#!/bin/bash

export BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." > /dev/null && pwd )"

# Change the following settings according to your environment
export FRAMENET_DATA_DIR="/home/kabbach/FrameNetData/fndata-1.5"
export JAVA_HOME_BIN="/usr/bin"
#export FRAMENET_DATA_DIR="/Users/AKB/Desktop/fndata-1.5"
#export JAVA_HOME_BIN="/Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home/bin"


export model_name="acl2015_baseline_replication_with_homemade_preprocessing" # choose a name for the model to train

export num_threads=55 # should set to roughly the number of cores available (minus one)
export gc_threads=37 # (3+5N/8) with N number of cores (remove one core on total number of cores just in case. Ex: count 55 cores for 56 cores total)
export min_ram=40g # min RAM allocated to the JVM in GB. Corresponds to the -Xms argument. Training needs at least 40g (yeah, I know...)
export max_ram=55g # max RAM allocated to the JVM in GB. Corresponds to the -Xmx argument

# You should not need to change anything below
export SEMAFOR_HOME="${BASE_DIR}/semafor"
export CLASSPATH="${SEMAFOR_HOME}/target/Semafor-3.0-alpha-05-adadelta-pfn.jar"
export EXPERIMENTS_DIR="${SEMAFOR_HOME}/experiments/${model_name}"
export EXPERIMENT_DATA_DIR="${EXPERIMENTS_DIR}/data"
export MODEL_DIR="${EXPERIMENTS_DIR}/model"

export fn_id_req_data_file="${MODEL_DIR}/reqData.jobj"

echo "Environment variables:"
echo "SEMAFOR_HOME=${SEMAFOR_HOME}"
echo "FRAMENET_DATA_DIR=${FRAMENET_DATA_DIR}"
echo "CLASSPATH=${CLASSPATH}"
echo "JAVA_HOME_BIN=${JAVA_HOME_BIN}"
echo "model_name=${model_name}"
echo "MODEL_DIR=${MODEL_DIR}"
echo "EXPERIMENTS_DIR=${EXPERIMENTS_DIR}"
echo "EXPERIMENT_DATA_DIR=${EXPERIMENT_DATA_DIR}"