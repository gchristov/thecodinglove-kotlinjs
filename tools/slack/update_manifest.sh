#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
IMAGE="thecodinglove-slack-update-manifest"

docker build -t "$IMAGE" "$SCRIPT_DIR"
docker run --rm "$IMAGE" "$@"
