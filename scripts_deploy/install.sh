#!/usr/bin/env bash

# This script is used by a single-line bash command for downloading the latest version of Orange Judge web application
# to oj_web folder in current directory.
#
# If there is an existing installation, this script will replace the old version. No back up will be done.

if [ ! -d "oj_web" ]; then
    mkdir oj_web
fi

wget http://storage.googleapis.com/orange-judge-builds/oj_web-LASTEST.zip

unzip -o oj_web-*
rsync -a -v oj_web-*/ oj_web/

rm oj_web-*.zip
rm -rf oj_web-*
