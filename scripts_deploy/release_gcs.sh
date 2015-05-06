#!/usr/bin/env bash

sh scripts_deploy/release.sh

echo "Preparing release to GCS."

gsutil cp target/release/* gs://orange-judge-builds

echo "Released to GCS."
