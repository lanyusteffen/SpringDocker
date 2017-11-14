@echo off
set base_dir=%~dp0
%base_dir:~0,2%
pushd %base_dir%
docker build -t stelylanlife/spring-boot-docker %~dp0/build/docker
popd
pause