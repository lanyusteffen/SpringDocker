#!/usr/bin/env bash
protoc -I=/home/stelylan/IdeaProjects/SpringDocker/src/main/protobuf --java_out=/home/stelylan/IdeaProjects/SpringDocker/src/main/java/ /home/stelylan/IdeaProjects/SpringDocker/src/main/protobuf/MessageProto.proto
