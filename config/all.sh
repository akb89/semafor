#!/bin/bash

export BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." > /dev/null && pwd )"

# Change the following settings according to your environment
export FRAMENET_DATA_DIR="/home/kabbach/FrameNetData/fndata-1.5"
export JAVA_HOME_BIN="/usr/bin"
#export FRAMENET_DATA_DIR="/Users/AKB/Desktop/fndata-1.5"
#export JAVA_HOME_BIN="/Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home/bin"

export with_exemplars=true # set to true to include exemplar sentences in addition to fulltext
export dependency_parser=MST # set to MST, MALT or TURBO according to the dependency parser you want to use
export clean_after_preprocessing=false # Remove unnecessary temporary files used for preprocessing. Set to false if you need to debug preprocessing

export lambda=0.000001 # hyperparameter for argument identification. Refer to Kshirsagar et al. (2015) for details.
export batch_size=40000 # number of batches processed at once for argument identification.

export model_name="test_prep_with_exemplars_${with_exemplars}_dep_parser_${dependency_parser}_lambda_${lambda}"

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