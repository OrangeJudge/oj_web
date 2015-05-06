#!/usr/bin/env bash

sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update
echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections
sudo apt-get -y install git vim oracle-java8-installer

export DEBIAN_FRONTEND=noninteractive
sudo apt-get -q -y install mysql-server

mysql -u root -e "CREATE USER 'oj'@'localhost' IDENTIFIED BY 'oj';"
mysql -u root -e "CREATE DATABASE oj;"
mysql -u root -e "GRANT ALL PRIVILEGES ON oj.* TO 'oj'@'localhost';"
