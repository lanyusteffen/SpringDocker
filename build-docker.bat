@echo off
set base_dir=%~dp0
%base_dir:~0,2%
pushd %base_dir%
copy /y %~dp0src\main\docker\Dockerfile %~dp0build\libs\Dockerfile
docker build -t stelylan/spring-boot-docker %~dp0build\libs
del %~dp0build\libs\Dockerfile
popd