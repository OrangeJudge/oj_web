#!/usr/bin/env bash

# This script is for releasing the Orange Judge web application.
# Currently it is used by Travis-CI.
# Files in target/release will be uploaded into a Google Cloud Storage after each compilation of master branch.

if [ -d "target/release" ]; then
    echo "Release directory exists, delete it."
    rm -rf target/release
fi

echo "Create release directory."

mkdir target/release

echo "Copy files to release directory."

# Copy install script
cp scripts_deploy/install.sh target/release

# Copy packaged zip
cp target/universal/*.zip target/release

# Delete share folder
zip --delete target/release/*.zip oj_web-\*/share/\*

# Create latest zip
cp target/release/*.zip target/release/oj_web-LASTEST.zip

echo "Release process finished."
