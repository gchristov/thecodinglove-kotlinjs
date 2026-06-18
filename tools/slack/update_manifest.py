#!/usr/bin/env python3
"""
Updates a Slack app's manifest using the Slack Apps API.

Usage:
    python update_manifest.py --app-id <APP_ID> --token <USER_TOKEN>

The token must be a user token (xoxp-...) with the `apps:write` scope,
obtained from https://api.slack.com/apps (your app's OAuth & Permissions page,
or via https://api.slack.com/tools/explorer).

The manifest file is read from the same directory as this script:
    thecodinglove-slack-manifest.json
"""

import argparse
import json
import sys
import urllib.request
import urllib.error
from pathlib import Path


MANIFEST_FILE = Path(__file__).parent / "thecodinglove-slack-manifest.json"
API_URL = "https://slack.com/api/apps.manifest.update"


def load_manifest() -> dict:
    with open(MANIFEST_FILE) as f:
        return json.load(f)


def update_manifest(app_id: str, token: str) -> None:
    manifest = load_manifest()
    payload = json.dumps({
        "app_id": app_id,
        "manifest": manifest,
    }).encode()

    req = urllib.request.Request(
        API_URL,
        data=payload,
        headers={
            "Authorization": f"Bearer {token}",
            "Content-Type": "application/json; charset=utf-8",
        },
        method="POST",
    )

    try:
        with urllib.request.urlopen(req) as resp:
            body = json.loads(resp.read())
    except urllib.error.HTTPError as e:
        print(f"HTTP error: {e.code} {e.reason}", file=sys.stderr)
        sys.exit(1)

    if not body.get("ok"):
        error = body.get("error", "unknown error")
        errors = body.get("errors", [])
        msg = f"Slack API error: {error}"
        if errors:
            msg += "\n" + "\n".join(f"  - {e}" for e in errors)
        print(msg, file=sys.stderr)
        sys.exit(1)

    print(f"Manifest updated successfully for app {app_id}.")


def main():
    parser = argparse.ArgumentParser(description="Update a Slack app manifest.")
    parser.add_argument("--app-id", required=True, help="Slack app ID (e.g. AFNEWBNFN)")
    parser.add_argument("--token", required=True, help="Slack user token (xoxp-...)")
    args = parser.parse_args()
    update_manifest(args.app_id, args.token)


if __name__ == "__main__":
    main()
