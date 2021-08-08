#!/bin/bash

REPOSITORY_DIR=/home/ubuntu/app
APP_NAME=work-assistant-api
PID_PATH=$REPOSITORY_DIR/PID

SESSION_PROPERTIES=$REPOSITORY_DIR/application-production-session-storage.yaml
THIRD_PARTY_PROPERTIES=$REPOSITORY_DIR/application-thid-party.yaml
DB_PROPERTIES=$REPOSITORY_DIR/application-production-db.yaml

echo "change directory"

cd $REPOSITORY_DIR/$APP_NAME

echo "git pull"

git pull

echo "app build"

./gradlew clean build

echo "copy executable jar file"

cp $REPOSITORY_DIR/$APP_NAME/build/libs/*.jar $REPOSITORY_DIR

echo "check current pid"

if [ -f $PID_PATH ]; then
	PID=$(cat $PID_PATH)
	echo "stop current pid $PID"
	kill -15 $PID
	sleep 10
fi

echo "new application deploy"

echo $(ls -tr $REPOSITORY_DIR | grep .jar | tail -n 1)

JAR_NAME=$(ls -tr $REPOSITORY_DIR/ | grep .jar | tail -n 1)

echo "jar name : $REPOSITORY_DIR/$JAR_NAME"

nohup java -jar \
  -Dspring.config.location=classpath:/application.yaml,$SESSION_PROPERTIES,$THIRD_PARTY_PROPERTIES,$DB_PROPERTIES \
  -Dspring.profiles.active=production \
   $REPOSITORY_DIR/$JAR_NAME 2>&1 & echo $! > $PID_PATH
