@echo off
set base_dir=%~dp0
%base_dir:~0,2%
pushd %base_dir%
docker run -d -p 8080:8080 --link dockerpostgresreplication_postgres-master_1:dockerpostgresreplication_postgres-master_1 --link dockerpostgresreplication_postgres-slave_1:dockerpostgresreplication_postgres-slave_1 --link redis_docker-redis-sentinel1_1:redis_docker-redis-sentinel1_1 --link redis_docker-redis-sentinel2_1:redis_docker-redis-sentinel2_1 --link redis_docker-redis-sentinel3_1:redis_docker-redis-sentinel3_1 stelylanlife/spring-boot-docker
popd


