#!/bin/bash

if [ -z $JBOSS_HOME ]; then
  echo "Please set the JBOSS_HOME variable prior to running this script"
  exit 1
fi

JBOSS_TARGET_DIR="komodoweb-distros/target"
JBOSS_DEPLOY_DIR="$JBOSS_HOME/standalone/deployments"
JBOSS_DEPLOYED_NAME="komodoweb.war"

JBOSS_WAR=`find $JBOSS_TARGET_DIR -name *.war`
if [ -z $JBOSS_WAR ]; then
  echo "No jboss war found ... building ..."
  ./build.sh
fi

echo "Publishing jboss war ..."

# Publish
cp -f "$JBOSS_WAR" $JBOSS_DEPLOY_DIR/$JBOSS_DEPLOYED_NAME

if [ -f $JBOSS_DEPLOY_DIR/$JBOSS_DEPLOYED_NAME ]; then
  echo "Success!"
else
  echo "Failed!"
fi
