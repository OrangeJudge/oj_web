#!/usr/bin/env bash

sbt dist

sh scripts_deploy/release_prepare.sh

sh scripts_deploy/release_gcs.sh

