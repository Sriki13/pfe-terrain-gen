#!/usr/bin/env bash

for i in `ls compositions`
do
echo "@$i"
curl -d "@compositions/$i" -H "Content-Type: application/json" -X POST http://35.189.243.176/compositions
echo
done