#!/bin/sh

# build native image
mvn clean package -Pnative\
  -Dquarkus.container-image.build=true\
  -Dquarkus.container-image.group=registry.heroku.com/rss-engine-native\
  -Dquarkus.container-image.name=web\
  -Dquarkus.container-image.tag=latest

# push to heroku
docker push registry.heroku.com/rss-engine-native/web

# trigger release
heroku container:release web --app rss-engine-native