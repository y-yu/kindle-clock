#! /bin/sh

rm /mnt/us/screensaver/done.png

curl -sSfL -o "/mnt/us/screensaver/done.png" "https://kindle-clock.onrender.com/?=<<AUTH_TOKEN>>"
eips -g /mnt/us/screensaver/done.png -f
