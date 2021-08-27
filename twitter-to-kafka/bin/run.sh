#!/usr/bin/env bash

/app/wait-for-it.sh
java -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar