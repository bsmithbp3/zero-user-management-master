#!/usr/bin/env bash
mkdir -p ./src/app/protobuf
pbjs --force-number -t static-module  ../app/protobuf/messages.proto -o ./src/app/protobuf/models.js
pbts --no-comments ./src/app/protobuf/models.js -o ./src/app/protobuf/models.d.ts

