#!/bin/bash

# You should not need to change anything below

source "$(dirname "${BASH_SOURCE[0]}")/setup.sh"

export BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." > /dev/null && pwd )"

export SEMAFOR_HOME="${BASE_DIR}/semafor"
export CLASSPATH="${SEMAFOR_HOME}/target/Semafor-3.0-alpha-05-adadelta-pfn.jar"
export EXPERIMENTS_DIR="${SEMAFOR_HOME}/experiments/${model_name}"
export EXPERIMENT_DATA_DIR="${EXPERIMENTS_DIR}/data"
export MODEL_DIR="${EXPERIMENTS_DIR}/model"
export LOGS_DIR="${EXPERIMENTS_DIR}/log"

export fn_id_req_data_file="${MODEL_DIR}/reqData.jobj"

mkdir -p ${LOGS_DIR}