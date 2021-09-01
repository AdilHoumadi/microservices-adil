#!/bin/bash

set -eu pipefail

echo "Attempting to connect to kafka"
until $(nc -zv broker 9092); do
    printf '.'
    sleep 5
done

echo "Attempting to connect to schema-registry"
until $(nc -zv schema-registry 8081); do
    printf '.'
    sleep 5
done