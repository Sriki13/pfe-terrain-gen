#!/usr/bin/env bash

for module in `ls -d */`
do
    cd $module
    for contract in `ls -d */`
    do
        cd $contract
        name=`mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=project.artifactId | grep -v '\['`
        mvn -q exec:java -Dexec.mainClass="pfe.terrain.gen.algo.reflection.ContractPrinter" > $name.json
        curl -X PUT -T $name.json http://35.189.252.97/artifactory/algo-data/$name.json
        rm $name.json
        cd ..
    done
    cd ..
done
