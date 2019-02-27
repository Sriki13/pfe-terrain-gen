#!/usr/bin/env bash

for module in `ls -d */`
do
    cd $module
    for contract in `ls -d */`
    do
        echo $contract
        cd $contract
        name=`mvn help:evaluate -Dexpression=project.artifactId | grep -v '\['`
        mvn -q exec:java -Dexec.mainClass="pfe.terrain.gen.algo.reflection.ContractPrinter" > $name.json
        curl -X PUT -T $name.json http://34.76.98.30/artifactory/algo-data/$name.json
        rm $name.json
        cd ..
    done
    cd ..
done
