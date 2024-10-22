#!/bin/bash
set -e
# Runs all unit tests.
# Should be invoked from the root of the project as all paths are relative.

services=($(bash tools/scripts/list_services.sh))
for service in "${services[@]}"; do
    if [ -e "$service/gradlew" ]; then
      # Need to explicitly cd into each service, otherwise we get yarn cache clashes
      cd $service
      ./gradlew --no-daemon --continue jsTest
      cd ..
    else
      echo "Skipping step as required file doesn't exist"
    fi
done