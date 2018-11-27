#!/usr/bin/env bash

"D:\Logiciels\maven\bin\mvn" -q clean install -DskipTests exec:java@mapGen "-Dexec.args=-f param.json" > map.json