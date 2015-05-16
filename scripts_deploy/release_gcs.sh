#!/usr/bin/env bash

echo "Preparing release to GCS."

gsutil cp -r target/release/* gs://orange-judge-builds

echo "Released to GCS."