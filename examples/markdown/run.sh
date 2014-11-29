#!/bin/sh

libs=$( echo ${MICRO_HOME}/lib/*.jar ${MICRO_HOME}/dist/*.jar WEB-INF/lib/*.jar . | sed 's/ /:/g')

# Start Micro with the embedded Jetty server.
# DEBUG="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

OPT="-Dnet.sf.ehcache.skipUpdateCheck=true -Xmx128m -Xss512k -XX:+UseCompressedOops"
java $OPT -cp "$libs:WEB-INF/classes" ca.simplegames.micro.WebServer . 8080 $1 $2 $3 $4 $5 $6
