#!/usr/bin/env bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
SERVER_NAME="cif-trickle"
if [ -r "$DEPLOY_DIR"/bin/setenv.sh ]; then
    . "$DEPLOY_DIR"/bin/setenv.sh
else
    echo "Cannot find $DEPLOY_DIR/bin/setenv.sh"
    echo "This file is needed to run this program"
    exit 1
fi
DATA_DIR=data
if [ ! -d "data" ]; then
	mkdir $DATA_DIR
fi
echo -e "Starting the $SERVER_NAME ...\c"

echo $APP_JARS
/Users/wq/opt/flink-1.2.0/bin/flink run -c com.finup.nbsp.datastream.elasticsearch2.ElasticSearch2Demo  $APP_JARS

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
