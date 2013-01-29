#!/bin/sh

libs=$( echo ${MICRO_HOME}/lib/*.jar ${MICRO_HOME}/dist/*.jar . | sed 's/ /:/g')

# Start Micro with the embedded Jetty server.
OPT='-Dnet.sf.ehcache.skipUpdateCheck=true -Xmx128m -Xss512k -XX:+UseCompressedOops'
java $OPT -cp "$libs" ca.simplegames.micro.WebServer . 8080 $1 $2 $3 $4 $5 $6
