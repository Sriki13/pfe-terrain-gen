#!/usr/bin/env bash

mvn clean install

cd composer
mvn exec:java

cd ../gen
./merge.sh
