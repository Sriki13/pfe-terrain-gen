#!/usr/bin/env bash
if [ ! -d ${HOME}/google-cloud-sdk ]; then
  export CLOUDSDK_CORE_DISABLE_PROMPTS=1;
  curl https://sdk.cloud.google.com | bash;
fi

gcloud auth activate-service-account --key-file secret.json
gcloud compute --project "la-roquette" ssh --zone "europe-west1-b" "factory" --command="bash gcloudDockerReset.sh"