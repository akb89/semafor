# SEMAFOR training

## Create target distribution
Run the following command under the semafor directory:
```
mvn package
```

## Modify configuration
Change the content of `config/all.sh` according to your environment. You should only have to change the following parameters:
- `FRAMENET_DATA_DIR` pointing to your directory containing FrameNet data (e.g. fndata-1.5)
- `JAVA_HOME_BIN` pointing to your java home
- `model_name` : the name of your model
- `num_threads`: the number of threads used to run semafor. You can set this value to the number of cores available, minus one
- `gc_threads`: the number of threads available for garbage collection. Set this value to 3+5N/8 with N number of cores (remove one core on total number of cores just in case. Ex: count 55 cores for 56 cores total)
- `min_ram`: -Xms argument in the JVM. Below 40g training will most likely fail... 
- `max_ram`: -Xmx argument in the JVM. The more, the merrier... 

## Necessary files
