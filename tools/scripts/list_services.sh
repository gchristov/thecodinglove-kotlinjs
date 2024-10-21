#!/bin/bash
set -e
# Lists all valid services from the project

all_services=$(find . -maxdepth 2 -type d -name infra -exec test -f "{}/Pulumi.yaml" \; -print | awk -F'/' '{print $2}')

if [[ -z "$all_services" ]]; then
  echo "No services detected"
  exit 1
else
  echo "$all_services"
  exit 0
fi