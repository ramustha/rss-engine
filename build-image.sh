#!/bin/sh

mvn clean package -Pnative\
  -Dquarkus.container-image.build=true\
  -Dquarkus.container-image.group=registry.heroku.com/rss-engine-native\
  -Dquarkus.container-image.name=web\
  -Dquarkus.container-image.tag=latest


# docker push registry.heroku.com/rss-engine-native/web
# heroku container:release web --app rss-engine-native