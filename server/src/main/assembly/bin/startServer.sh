#!/bin/bash
export CLASSPATH=./
for d in ../lib/*.jar;
do
	CLASSPATH=${CLASSPATH}:"$d"
done;

export JAVA_CMD="java -Xmn256m -Xms4g -Xmx4g -cp "$CLASSPATH" com.alibaba.leviathan.server.LeviathanServer "

echo $JAVA_CMD

$JAVA_CMD

