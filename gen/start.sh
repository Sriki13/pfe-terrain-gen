#!/usr/bin/env bash

./merge.sh

java -jar generator.jar $*
