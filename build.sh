#!/bin/bash

JBOSS_TARGET_DIR="komodoweb-distros/target"

# Clean up war
rm -rf `find $JBOSS_TARGET_DIR -name *.war`

# Build
mvn clean install

