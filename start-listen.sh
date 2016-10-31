#! /bin/sh
cd ~/dhus \
&& ~/dhus/start.sh > /dev/null 2>&1 \
& java -jar ~/dhus-listener.jar 180 ~/dhus/dhus.log \
&& ~/dhus/stop.sh