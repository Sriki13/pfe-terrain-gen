#!/usr/bin/env bash


cd generator-factory
docker build -t $DOCKER_USERNAME/pfe_generator_factory .

echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
docker push $DOCKER_USERNAME/pfe_generator_factory