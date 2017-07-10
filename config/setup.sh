#!/bin/bash

# Change the following settings according to your environment

export FRAMENET_DATA_DIR="/home/kabbach/FrameNetData/fndata-1.5"
export JAVA_HOME_BIN="/usr/bin"
#export FRAMENET_DATA_DIR="/Users/AKB/Dropbox/FrameNetData/fndata-1.5"
#export JAVA_HOME_BIN="/Library/Java/JavaVirtualMachines/jdk1.8.0_20.jdk/Contents/Home/bin"

export with_exemplars=false # set to true to include exemplar sentences in addition to fulltext
export dependency_parser=MALT # set to MST, MALT or TURBO according to the dependency parser you want to use

export clean_after_preprocessing=true # remove unnecessary temporary files used for preprocessing. Set to false if you need to debug preprocessing
export clean_after_training=true # remove unnecessary temporary files used for training. Set to false if you need to debug training
export clean_after_scoring=true # remove unnecessary temporary files used for scoring. Set to false if you need to debug scoring

export lambda=0.000001 # hyperparameter for argument identification. Refer to Kshirsagar et al. (2015) for details.
export batch_size=40000 # number of batches processed at once for argument identification.
export save_every_k_batches=400 # for argument identification
export num_models_to_save=60 # for argument identification

#export model_name="acl2015_baseline_with_old_semafor_data"
#export model_name="acl2015_baseline_with_exemplars_${with_exemplars^^}_dep_parser_${dependency_parser}_lambda_${lambda}"
model_prefix="baseline_fn15_no_hierarchy"
export model_name="${model_prefix}_with_exemplars_${with_exemplars^^}_dep_parser_${dependency_parser}_lambda_${lambda}"

export num_threads=55 # should set to roughly the number of cores available (minus one)
export gc_threads=37 # (3+5N/8) with N number of cores (remove one core on total number of cores just in case. Ex: count 55 cores for 56 cores total)
export min_ram=40g # min RAM allocated to the JVM in GB. Corresponds to the -Xms argument. Training needs at least 40g (yeah, I know...)
export max_ram=55g # max RAM allocated to the JVM in GB. Corresponds to the -Xmx argument