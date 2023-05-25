#!/bin/bash

i=0

while IFS= read -r linha
do
    if [ $((contador % 2)) -eq 0 ]; then
        echo "Teste $i:"
    fi
    i=$((i+1))
    echo $linha | ./main $(cat)
done < tests
