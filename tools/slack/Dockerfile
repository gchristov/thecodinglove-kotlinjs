FROM python:3.12-alpine

WORKDIR /app
COPY update_manifest.py thecodinglove-slack-manifest.json ./

ENTRYPOINT ["python3", "update_manifest.py"]
