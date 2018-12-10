#!/usr/bin/env bash

mvn deploy
mvn help:evaluate -Dexpression=project.artifactId

cd algo
./pushMetaData.sh
cd ..