#! /bin/sh
rm -rf ~/dhus/* \
&& unzip -o ~/dhus-software-openstack-0.2.0-distribution.zip -d ~/dhus \
&& rm ~/dhus-software-openstack-0.2.0-distribution.zip \
&& sed -ie "s|local_dhus|/home/dhus/dhus/local_dhus|" ~/dhus/etc/dhus.xml \
&& chmod +x ~/dhus/start.sh \
&& chmod +x ~/dhus/stop.sh \
&& ~/dhus/start.sh \
& java -jar ~/dhus-listener.jar 180 dhus/dhus.log