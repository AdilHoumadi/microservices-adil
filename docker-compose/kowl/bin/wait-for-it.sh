#!/bin/bash

set -eu pipefail

echo "Attempting to connect to kafka"
until $(nc -zv broker 9092); do
    printf '.'
    sleep 5
done