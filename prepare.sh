#! /bin/sh
cd /home/dhus/go-dhus-environment \
&& rm -rf /home/dhus/go-dhus-environment/dhus \
&& mkdir /home/dhus/go-dhus-environment/dhus \
&& unzip -o /home/dhus/go-dhus-environment/dhus-software-distribution.zip -d /home/dhus/go-dhus-environment/dhus \
&& rm /home/dhus/go-dhus-environment/dhus-software-distribution.zip \
&& sed -ie "s|local_dhus|/home/dhus/go-dhus-environment/dhus/local_dhus|" /home/dhus/go-dhus-environment/dhus/etc/dhus.xml \
&& cp start.sh /home/dhus/go-dhus-environment/dhus/start.sh \
&& chmod +x /home/dhus/go-dhus-environment/dhus/start.sh \
&& chmod +x /home/dhus/go-dhus-environment/dhus/stop.sh \
&& chmod +x /home/dhus/go-dhus-environment/start-listen.sh