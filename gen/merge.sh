#!/usr/bin/env bash

mvn clean package

mkdir tmp


cd lib
for file in `ls`
do
unzip -uo $file -d ../tmp
echo $file
rm ../tmp/META-INF/MANIFEST.MF
done
cd ..

unzip -uo ./target/gen-1.0-SNAPSHOT-jar-with-dependencies.jar -d ./tmp

jar cvfm generator.jar ./tmp/META-INF/MANIFEST.MF -C tmp/ .

rm -r tmp/


