#!/usr/bin/env bash

./activator dist

sh scripts_deploy/release_prepare.sh

sh scripts_deploy/release_gcs.sh

