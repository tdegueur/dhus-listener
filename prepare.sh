#! /bin/sh
rm -rf ~/dhus/* \
&& unzip -o ~/dhus-software-distribution.zip -d ~/dhus \
&& rm ~/dhus-software-distribution.zip \
&& sed -ie "s|local_dhus|/home/dhus/dhus/local_dhus|" ~/dhus/etc/dhus.xml \
&& chmod +x ~/dhus/start.sh \
&& chmod +x ~/dhus/stop.sh