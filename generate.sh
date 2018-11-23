#!/usr/bin/env bash

cd composer
mvn exec:java

cd ../gen
./merge.sh
