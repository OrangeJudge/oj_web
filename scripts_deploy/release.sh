#!/usr/bin/env bash

# This script is for releasing the Orange Judge web application.
# Currently it is used by Travis-CI.
# Files in target/release will be uploaded into a Google Cloud Storage after each compilation of master branch.

if [ -d "target/release" ]; then
    echo "Release directory exists, delete it."
    rm -rf target/release
fi

mkdir target/release

echo "Copy the packed file to release directory."

cp target/universal/*.zip target/release
cp target/release/*.zip target/release/oj_web-LASTEST.zip

echo "Release process finished."
