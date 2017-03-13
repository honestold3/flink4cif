#!/usr/bin/env bash
cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`
SERVER_NAME="oplog-syncer"
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
#nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS -Dqueue.size=3000 -Dxml.name=$XMLFILE  -classpath $CONF_DIR:$LIB_JARS com.puhui.nbsp.cif.Application $CONFIG_FILE> $STDOUT_FILE 2>&1 &

echo $LIB_JARS
/usr/local/cloud/spark/bin/spark-submit --master local[*]  --class com.finup.nbsp.cif.trickle.ODSTrickleMain --files /home/xiongyuanbiao/cif/cif-trickle/conf/app.conf --jars $LIB_JARS /home/xiongyuanbiao/cif/cif-trickle/lib/cif.trickle-1.0.jar /home/xiongyuanbiao/cif/cif-trickle/conf/app.conf 

echo "OK!"
PIDS=`ps -f | grep java | grep "$DEPLOY_DIR" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
