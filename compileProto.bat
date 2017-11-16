@echo off
set base_dir=%~dp0
%base_dir:~0,2%
pushd %base_dir%
protoc -I=/home/stelylan/IdeaProjects/SpringDocker/src/main/protobuf --java_out=/home/stelylan/IdeaProjects/SpringDocker/src/main/java/ /home/stelylan/IdeaProjects/SpringDocker/src/main/protobuf/MessageProto.proto
popd