#!/usr/bin/env bash

containerName=facto
docker stop $containerName
docker rm $containerName
docker pull lucasmatteo/pfe_generator_factory:latest

docker run --name $containerName -d -p 80:9090 lucasmatteo/pfe_generator_factory:latest