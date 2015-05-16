#!/usr/bin/env bash

./activator doc

gsutil -m cp -r target/scala-*/api gs://orange-judge-builds
