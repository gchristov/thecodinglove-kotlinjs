#!/bin/bash
set -e
# Lists all valid services from the project
# Should be invoked from the root of the project as all paths are relative.

all_services=($(find . -maxdepth 2 -type d -name infra -exec test -f "{}/Pulumi.yaml" \; -print | awk -F'/' '{print $2}' | xargs -I {} echo {}))

if [ ${#all_services[@]} -eq 0 ]; then
  echo "No services detected"
  exit 1
else
  echo "${all_services[@]}"
  exit 0
fi