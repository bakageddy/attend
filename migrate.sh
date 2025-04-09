#!/usr/bin/env sh

sudo cp ./app/build/libs/app.war /var/lib/tomcat10/webapps/
sudo systemctl restart tomcat10
