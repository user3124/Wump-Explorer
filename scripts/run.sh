#!/bin/bash
CP_SEP=":"
if [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" || "$OSTYPE" == "win32" ]]; then
    CP_SEP=";"
fi

java -cp "../output/app.jar${CP_SEP}../lib/kotlinx-cli-jvm-0.3.6.jar" MainKt "$@"

# java -cp "../output/app.jar:../lib/kotlinx-cli-jvm-0.3.6.jar" MainKt -l aboba -p 1234 -a read -r A.B.C -v 20
# java -cp "../output/app.jar:../lib/kotlinx-cli-jvm-0.3.6.jar" MainKt -h