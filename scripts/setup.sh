#!/usr/bin/env bash

# This script is for installing required components used by Orange Judge web application, as well as setting
# up the MySQL database by creating a local user named 'oj'.

sudo add-apt-repository -y ppa:webupd8team/java
sudo apt-get update
echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true | sudo debconf-set-selections
sudo apt-get -y install oracle-java8-installer

export DEBIAN_FRONTEND=noninteractive
sudo apt-get -q -y install mysql-server

mysql -u root -e "CREATE USER 'oj'@'localhost' IDENTIFIED BY 'oj';"
mysql -u root -e "CREATE DATABASE oj;"
mysql -u root -e "GRANT ALL PRIVILEGES ON oj.* TO 'oj'@'localhost';"
