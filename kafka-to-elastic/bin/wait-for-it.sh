#!/bin/bash

set -e
echo "Initiating..."
until curl --output /dev/null --silent --head --fail "http://config-server:8888/actuator/health"; do
  >&2 echo "config-server is unavailable - sleeping"
  sleep 1
done
>&2 echo "Config-server is up"
