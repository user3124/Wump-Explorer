#!/bin/bash
OK=0; TOTAL=14

run_test() {
    bash ./run.sh "$@" > /dev/null 2>&1
    local code=$?
    if [ $code -eq $expected ]; then
        echo "$name: OK"
        OK=$((OK + 1))
    else
        echo "$name: FAIL ($expected -> $code)"
    fi
}

name="Test 1 (Success)"; expected=0
run_test -l alice -p qwerty -a read -r A.B -v 10

name="Test 2 (Help Flag)"; expected=1
run_test -h

name="Test 3 (Missing Args)"; expected=1
run_test -l alice

name="Test 4 (Wrong Pass)"; expected=2
run_test -l alice -p wrong_pass -a read -r A.B -v 10

name="Test 5 (Wrong Login)"; expected=3
run_test -l unknown -p qwerty -a read -r A.B -v 10

name="Test 6 (Wrong Action)"; expected=4
run_test -l alice -p qwerty -a delete -r A.B -v 10

name="Test 7 (No Access)"; expected=5
run_test -l bob -p 12345 -a read -r A.B -v 10

name="Test 8 (Wrong Resource)"; expected=6
run_test -l alice -p qwerty -a read -r A.UNKNOWN -v 10

name="Test 9 (Invalid Volume)"; expected=7
run_test -l alice -p qwerty -a read -r A.B -v text

name="Test 10 (Volume Exceeded)"; expected=8
run_test -l alice -p qwerty -a read -r A.B.C -v 999

name="Test 11 (Unknown Flag)"; expected=1
run_test -l alice -x

name="Test 12 (Flag Without Value)"; expected=1
run_test -l alice -p qwerty -a read -r A.B -v

name="Test 13 (Inherited Permission)"; expected=0
run_test -l alice -p qwerty -a read -r A.B.C -v 10

name="Test 14 (Bad Resource Format)"; expected=7
run_test -l alice -p qwerty -a read -r A..B -v 1

echo "Total: $OK/$TOTAL"
