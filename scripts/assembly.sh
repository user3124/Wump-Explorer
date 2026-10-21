#!/bin/bash
# kotlinc сам обходит вложенные папки (domain/, services/, infrastructure/)
mkdir -p ../output
kotlinc ../src -cp ../lib/kotlinx-cli-jvm-0.3.6.jar -include-runtime -d ../output/app.jar
