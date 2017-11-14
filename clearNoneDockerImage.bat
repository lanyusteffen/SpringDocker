@echo off
set base_dir=%~dp0
%base_dir:~0,2%
pushd %base_dir%
docker rmi -f  $(docker images | grep "^<none>" | awk '{print $3}')
popd
pause