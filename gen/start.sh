#!/usr/bin/env bash

mvn clean package
./merge.sh

java -cp generator.jar pfe.terrain.gen.AppGen

