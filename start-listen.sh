#! /bin/sh
cd /home/dhus/go-dhus-environment/dhus \
&& ./start.sh > /dev/null 2>&1 \
& java -jar /home/dhus/go-dhus-environment/dhus-listener.jar 180 dhus.log \