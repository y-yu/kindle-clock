Kindle Clock
=====================================

![Example image](https://y-yu.github.io/kindle-clock/example.png)

## How to use

You have to install

- Docker and docker-compose
- JDK
- [Nature Remo](https://en.nature.global/products/)
- [Awair](https://www.getawair.com/home/element)

then:

```console
$ export NATURE_REMO_OAUTH_TOKEN=<<YOUR_NATURE_REMO_OAUTH_TOKEN>> \
AWAIR_OAUTH_TOKEN=<<YOUR_AWAIR_OAUTH_TOKEN>> \
OPEN_WEATHER_MAP_ID=<<YOUR_LOCATION_ID>> \
OPEN_WEATHER_MAP_APP_ID=<<YOUR_APP_ID>> \
AWAIR_CACHE_EXPIRE_SECONDS=30 \
AWAIR_INTERVAL_MINUTES=5

$ ./sbt "primary / run"
```