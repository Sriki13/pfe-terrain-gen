#!/usr/bin/env bash

mkdir tmp
unzip -uo ./target/gen-1.0-SNAPSHOT-jar-with-dependencies.jar -d ./tmp

cd lib
for file in `ls`
do
unzip -uo $file -d ../tmp
echo $file
done
cd ..

jar cvf generator.jar -C tmp/ .

rm -r tmp/


