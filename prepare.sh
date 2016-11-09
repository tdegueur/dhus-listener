#! /bin/sh
cd /home/dhus/go-dhus-environment \
&& rm -rf ./dhus \
&& mkdir ./dhus \
&& unzip -o dhus-software-distribution.zip -d ./dhus \
&& rm dhus-software-distribution.zip \
&& sed -ie "s|local_dhus|/home/dhus/go-dhus-environment/dhus/local_dhus|" dhus/etc/dhus.xml \
&& cp start.sh ./dhus/start.sh \
&& chmod +x ./dhus/start.sh \
&& chmod +x ./dhus/stop.sh \
&& chmod +x ./start-listen.sh