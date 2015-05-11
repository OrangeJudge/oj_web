#!/usr/bin/env bash

echo "Preparing release to GCS."

gsutil cp target/release/* gs://orange-judge-builds

echo "Released to GCS."