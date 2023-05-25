#!/bin/bash

n_tests=$1

for i in $(seq 1 $n_tests)
do
    python ../nums_gen.py -max 1000 -n 1000 >> tests
done
