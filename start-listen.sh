#! /bin/sh
cd ~/dhus \
&& ~/dhus/start.sh > /dev/null \
& java -jar ~/dhus-listener.jar 180 ~/dhus/dhus.log \
&& ~/dhus/stop.sh