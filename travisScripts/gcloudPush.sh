#!/usr/bin/env bash

gcloud compute --project "la-roquette" ssh --zone "europe-west1-b" "factory" --command="bash gcloudDockerReset.sh"