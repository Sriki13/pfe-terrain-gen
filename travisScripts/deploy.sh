#!/usr/bin/env bash

mvn deploy

cd algo
./pushMetaData.sh
cd ..