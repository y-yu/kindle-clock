#! /bin/sh

rm /mnt/us/screensaver/done.png

wget -O "/mnt/us/screensaver/done.png" "http://<<kindle-server-name>>.herokuapp.com/?=<<token>>"
eips -g /mnt/us/screensaver/done.png -f
